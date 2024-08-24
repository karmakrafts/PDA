/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.shader.Sampler;
import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderType;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformBuffer;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import io.karma.pda.api.reload.Reloadable;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.hook.ExtendedRenderSystem;
import io.karma.pda.mod.client.hook.ExtendedShader;
import io.karma.pda.mod.reload.DefaultReloadHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderProgram extends RenderStateShard.ShaderStateShard implements ShaderProgram, Reloadable {
    private final int id;
    private final VertexFormat vertexFormat;
    private final ArrayList<DefaultShaderObject> objects;
    private final Consumer<ShaderProgram> bindCallback;
    private final Consumer<ShaderProgram> unbindCallback;
    private final Object2IntOpenHashMap<String> uniformLocations = new Object2IntOpenHashMap<>();
    private final Object2IntOpenHashMap<String> uniformBlockIndices = new Object2IntOpenHashMap<>();
    private final DefaultUniformCache uniformCache;
    private final HashMap<String, UniformBuffer> uniformBuffers;
    private final Object2IntOpenHashMap<String> samplerIds;
    private final Int2ObjectArrayMap<Sampler> samplers;
    private final LinkedHashMap<String, Object> constants;
    private final LinkedHashMap<String, Object> defines;

    private final ArrayList<Sampler> dynamicSamplers = new ArrayList<>();
    private final HashMap<ShaderObject, ProgramAdaptor> adaptorCache = new HashMap<>();
    private final ExtendedShaderAdaptor extendedShaderAdaptor = new ExtendedShaderAdaptor();

    private boolean isLinked;
    private boolean isRelinkRequested;
    private boolean isBound;

    DefaultShaderProgram(final VertexFormat vertexFormat,
                         final ArrayList<DefaultShaderObject> objects,
                         final LinkedHashMap<String, Uniform> uniforms,
                         final HashMap<String, UniformBuffer> uniformBuffers,
                         final Consumer<ShaderProgram> bindCallback,
                         final Consumer<ShaderProgram> unbindCallback,
                         final Object2IntOpenHashMap<String> samplerIds,
                         final LinkedHashMap<String, Object> constants,
                         final LinkedHashMap<String, Object> defines,
                         final Int2ObjectArrayMap<IntSupplier> staticSamplers) {
        this.vertexFormat = vertexFormat;
        this.objects = objects;
        this.bindCallback = bindCallback;
        this.unbindCallback = unbindCallback;
        this.samplerIds = samplerIds;
        samplers = new Int2ObjectArrayMap<>(samplerIds.size()); // Pre-allocate sampler texture buffer
        this.constants = constants;
        this.defines = defines;
        id = GL20.glCreateProgram();
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Created new shader program {}", id);
        uniformCache = new DefaultUniformCache(uniforms);
        this.uniformBuffers = uniformBuffers;
        setupAttributes();

        PDAMod.DISPOSITION_HANDLER.register(this);
        DefaultReloadHandler.INSTANCE.register(this);

        // Attach shader objects to program
        for (final var object : objects) {
            object.attach(this);
        }

        // Set up static samplers
        for (final var sampler : staticSamplers.int2ObjectEntrySet()) {
            final var samplerId = sampler.getIntKey();
            final var textureId = sampler.getValue();
            // @formatter:off
            final var name = samplerIds.object2IntEntrySet()
                .stream()
                .filter(e -> e.getIntValue() == samplerId)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow();
            // @formatter:on
            final var samplerInstance = StaticSampler.create(samplerId, name, textureId);
            samplers.put(samplerId, samplerInstance);
            if (samplerInstance.isDynamic()) {
                // If ARB_bindless_texture is not available, these will be created as dynamic samplers
                dynamicSamplers.add(samplerInstance);
            }
        }

        // Set up dynamic samplers
        for (final var sampler : samplerIds.object2IntEntrySet()) {
            final var samplerId = sampler.getIntValue();
            if (staticSamplers.containsKey(samplerId)) {
                continue; // We don't want to process the static samplers
            }
            final var samplerInstance = new DynamicSampler(samplerId, sampler.getKey());
            samplers.put(samplerId, samplerInstance);
            dynamicSamplers.add(samplerInstance);
        }
    }

    private void setupUniformBuffers() {
        // Set up uniform buffers
        for (final var buffer : uniformBuffers.entrySet()) {
            buffer.getValue().setup(buffer.getKey(), this);
        }
    }

    private void setupAttributes() {
        // Bind vertex format attribute locations for VS ins
        final var attribs = vertexFormat.getElementAttributeNames();
        for (var i = 0; i < attribs.size(); i++) {
            final var attrib = attribs.get(i);
            GL20.glBindAttribLocation(id, i, attrib);
            PDAMod.LOGGER.debug(LogMarkers.RENDERER,
                "Bound vertex format attribute {}={} for program {}",
                attrib,
                i,
                id);
        }
    }

    @Override
    public void prepareReload(final ResourceManager manager) {
        unbind();
        isLinked = false;
    }

    @Override
    public void reload(final ResourceManager manager) {
        for (final var object : objects) {
            object.recompile(this, manager);
        }
        PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Relinking program {}", id);
        GL20.glLinkProgram(id);
        if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER,
                "Could not link shader program {}: {}",
                id,
                GL20.glGetProgramInfoLog(id));
            return;
        }
        uniformLocations.clear();
        uniformCache.updateAll(); // Flag all uniforms to be re-applied statically
        for (final var sampler : samplers.values()) {
            sampler.setup(this);
        }
        uniformBlockIndices.clear();
        setupUniformBuffers();
        isLinked = true;
    }

    @Override
    public int getUniformLocation(final String name) {
        return uniformLocations.computeIfAbsent(name, (String n) -> GL20.glGetUniformLocation(id, n));
    }

    @Override
    public int getUniformBlockIndex(final String name) {
        return uniformBlockIndices.computeIfAbsent(name, (String n) -> GL31.glGetUniformBlockIndex(id, n));
    }

    @Override
    public Map<String, UniformBuffer> getUniformBuffers() {
        return uniformBuffers;
    }

    @Override
    public Map<String, Object> getDefines() {
        return defines;
    }

    @Override
    public Map<String, Object> getConstants() {
        return constants;
    }

    @Override
    public void setSampler(final String name, final int textureId) {
        final var sampler = getSampler(name);
        if (!(sampler instanceof DynamicSampler dynamicSampler)) {
            throw new IllegalArgumentException(String.format("Sampler '%s' is not dynamic", name));
        }
        dynamicSampler.setTextureId(() -> textureId);
    }

    @Override
    public void setSampler(final String name, final ResourceLocation location) {
        final var manager = Minecraft.getInstance().getTextureManager();
        final var texture = manager.getTexture(location);
        setSampler(name, texture.getId());
    }

    @Override
    public Sampler getSampler(final String name) {
        return samplers.get(samplerIds.getInt(name));
    }

    @Override
    public UniformCache getUniformCache() {
        return uniformCache;
    }

    @Override
    public ShaderStateShard asStateShard() {
        return this;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void unbind() {
        if (!isBound || !isLinked) {
            return;
        }
        unbindCallback.accept(this);
        // Unbind/disable samplers
        for (final var sampler : dynamicSamplers) {
            sampler.unbind(this);
        }
        // Undbind/disable UBOs
        for (final var buffer : uniformBuffers.entrySet()) {
            buffer.getValue().unbind(buffer.getKey(), this);
        }
        GL20.glUseProgram(0);
        isBound = false;
    }

    @Override
    public void bind() {
        if (isBound || !isLinked) {
            return;
        }
        GL20.glUseProgram(id);
        // Bind/enable UBOs
        for (final var buffer : uniformBuffers.entrySet()) {
            buffer.getValue().bind(buffer.getKey(), this);
        }
        // Bind/enable samplers
        for (final var sampler : dynamicSamplers) {
            sampler.bind(this);
        }
        bindCallback.accept(this);
        // Update uniforms
        uniformCache.applyAll(this);
        isBound = true;
    }

    @Override
    public void dispose() {
        isLinked = false;
        // Detach and free all shader objects
        for (final var object : objects) {
            final var objectId = object.getId();
            GL20.glDetachShader(id, objectId);
            GL20.glDeleteShader(objectId);
        }
        GL20.glDeleteProgram(id);
    }

    @Override
    public boolean isLinked() {
        return isLinked;
    }

    @Override
    public void setupRenderState() {
        ExtendedRenderSystem.getInstance().setExtendedShader(() -> extendedShaderAdaptor);
    }

    @Override
    public void clearRenderState() {
        ExtendedRenderSystem.getInstance().setExtendedShader(() -> null);
    }

    @Override
    public void requestRelink() {
        isRelinkRequested = true;
    }

    @Override
    public boolean isRelinkRequested() {
        return isRelinkRequested;
    }

    @Override
    public ShaderObject getObject(final ShaderType type) { // @formatter:off
        return objects.stream()
            .filter(obj -> obj.getType() == type)
            .findFirst()
            .orElseThrow();
    } // @formatter:on

    @Override
    public @NotNull String toString() {
        return String.format("DefaultShaderProgram[id=%d,objects=%s]", id, objects);
    }

    @Override
    public int hashCode() {
        return HashUtils.combine(vertexFormat.hashCode(),
            objects.hashCode(),
            uniformCache.hashCode(),
            HashUtils.hash(uniformBuffers),
            samplerIds.hashCode(),
            HashUtils.hashValuesAsStrings(constants),
            HashUtils.hashValuesAsStrings(defines));
    }

    private static final class ProgramAdaptor extends Program {
        private final ShaderObject object;

        public ProgramAdaptor(final ShaderObject object) {
            super(getType(object.getType()), object.getId(), object.getLocation().toString());
            this.object = object;
        }

        private static Type getType(final ShaderType type) {
            return switch (type) {
                case VERTEX -> Type.VERTEX;
                case FRAGMENT -> Type.FRAGMENT;
            };
        }

        @Override
        public int getId() {
            return object.getId();
        }

        @Override
        public @NotNull String getName() {
            return object.getLocation().toString();
        }

        @Override
        public void attachToShader(final @NotNull Shader shader) {
        }

        @Override
        public void close() {
        }
    }

    private final class ExtendedShaderAdaptor implements ExtendedShader {
        @Override
        public int getId() {
            return id;
        }

        @Override
        public void apply() {
            bind();
        }

        @Override
        public void clear() {
            unbind();
        }

        @Override
        public void setSampler(final String name, final Object id) {
        }

        @Override
        public void markDirty() {
        }

        @Override
        public @NotNull Program getVertexProgram() {
            final var object = getObject(ShaderType.VERTEX);
            if (object instanceof Program) {
                return (Program) object;
            }
            return adaptorCache.computeIfAbsent(object, ProgramAdaptor::new);
        }

        @Override
        public @NotNull Program getFragmentProgram() {
            final var object = getObject(ShaderType.FRAGMENT);
            if (object instanceof Program) {
                return (Program) object;
            }
            return adaptorCache.computeIfAbsent(object, ProgramAdaptor::new);
        }

        @Override
        public void attachToProgram() {
        }
    }
}

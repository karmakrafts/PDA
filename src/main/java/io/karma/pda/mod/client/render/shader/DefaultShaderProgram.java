/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderType;
import io.karma.pda.api.client.render.shader.uniform.DefaultUniformType;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.hook.ExtendedRenderSystem;
import io.karma.pda.mod.client.hook.ExtendedShader;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderProgram extends RenderStateShard.ShaderStateShard
    implements ShaderProgram, ResourceManagerReloadListener {
    private final int id;
    private final VertexFormat vertexFormat;
    private final ArrayList<DefaultShaderObject> objects;
    private final Consumer<ShaderProgram> bindCallback;
    private final Consumer<ShaderProgram> unbindCallback;
    private final DefaultUniformCache uniformCache;
    private final Object2IntLinkedOpenHashMap<String> samplers;
    private final Int2IntArrayMap samplerTextures = new Int2IntArrayMap();
    private final HashMap<String, Object> constants;
    private final Object2IntLinkedOpenHashMap<String> defines;
    private final AtomicBoolean isLinked = new AtomicBoolean(false);
    private final AtomicBoolean isRelinkRequested = new AtomicBoolean(false);
    private final ExtendedShaderAdaptor extendedShader;
    private int previousTextureUnit;

    DefaultShaderProgram(final VertexFormat vertexFormat, final ArrayList<DefaultShaderObject> objects,
                         final HashMap<String, Uniform> uniforms, final @Nullable Consumer<ShaderProgram> bindCallback,
                         final @Nullable Consumer<ShaderProgram> unbindCallback,
                         final Object2IntLinkedOpenHashMap<String> samplers, final HashMap<String, Object> constants,
                         final Object2IntLinkedOpenHashMap<String> defines) {
        this.vertexFormat = vertexFormat;
        this.objects = objects;
        this.bindCallback = bindCallback;
        this.unbindCallback = unbindCallback;
        this.samplers = samplers;
        this.constants = constants;
        this.defines = defines;
        extendedShader = new ExtendedShaderAdaptor(); // Objects must be initialized before constructing this

        id = GL20.glCreateProgram();
        PDAMod.DISPOSITION_HANDLER.addObject(this);
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);

        // Attach shader objects to program
        for (final var object : objects) {
            GL20.glAttachShader(id, object.getId()); // Attach all objects right in-place
        }
        // Set up uniform cache with combined samplers
        final var combinedUniforms = new HashMap<>(uniforms);
        for (final var sampler : samplers.object2IntEntrySet()) {
            final var samplerName = sampler.getKey();
            combinedUniforms.put(samplerName, DefaultUniformType.INT.create(samplerName, sampler.getIntValue()));
        }
        uniformCache = new DefaultUniformCache(this, combinedUniforms);
    }

    @Override
    public Object2IntMap<String> getDefines() {
        return defines;
    }

    @Override
    public Map<String, Object> getConstants() {
        return constants;
    }

    @Override
    public void setSampler(final String name, final int textureId) {
        samplerTextures.put(samplers.getInt(name), textureId);
    }

    @Override
    public void setSampler(final String name, final ResourceLocation location) {
        final var textureManager = Minecraft.getInstance().getTextureManager();
        final var texture = textureManager.getTexture(location);
        samplerTextures.put(samplers.getInt(name), texture.getId());
    }

    @Override
    public int getSampler(final String name) {
        return samplerTextures.getOrDefault(samplers.getInt(name), -1);
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
    public void unbind() {
        if (!isLinked.get()) {
            return;
        }
        for (final var object : objects) {
            object.onUnbindProgram(this);
        }
        if (unbindCallback != null) {
            unbindCallback.accept(this);
        }
        // Unbind/disable samplers
        for (final var sample : samplers.object2IntEntrySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + sample.getIntValue());
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
        GL13.glActiveTexture(previousTextureUnit);
        GL20.glUseProgram(0);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void bind() {
        if (!isLinked.get()) {
            return;
        }
        if (isRelinkRequested.compareAndSet(true, false)) {
            relink(Minecraft.getInstance().getResourceManager());
        }
        for (final var object : objects) {
            object.onBindProgram(this);
        }
        GL20.glUseProgram(id);
        if (bindCallback != null) {
            bindCallback.accept(this);
        }
        // Update uniforms
        uniformCache.updateAll();
        // Bind/enable samplers
        previousTextureUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        for (final var sample : samplers.object2IntEntrySet()) {
            final var index = sample.getIntValue();
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, samplerTextures.get(index));
        }
    }

    @Override
    public void dispose() {
        isLinked.set(false);
        for (final var object : objects) {
            final var objectId = object.getId();
            GL20.glDetachShader(id, objectId);
            GL20.glDeleteShader(objectId);
        }
        GL20.glDeleteProgram(id);
    }

    @Override
    public boolean isLinked() {
        return isLinked.get();
    }

    @Override
    public void setupRenderState() {
        ExtendedRenderSystem.getInstance().setExtendedShader(() -> extendedShader);
    }

    @Override
    public void clearRenderState() {
        ExtendedRenderSystem.getInstance().setExtendedShader(() -> null);
    }

    @Override
    public void onResourceManagerReload(final @NotNull ResourceManager resourceManager) {
        relink(resourceManager);
    }

    @Override
    public void requestRelink() {
        isRelinkRequested.set(true);
    }

    @Override
    public boolean isRelinkRequested() {
        return isRelinkRequested.get();
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

    private void relink(final ResourceProvider provider) {
        isLinked.set(false);
        for (final var object : objects) {
            object.recompile(this, provider);
        }
        Minecraft.getInstance().execute(() -> {
            // Bind vertex format attribute locations for VS ins
            final var attribs = vertexFormat.getElementAttributeNames();
            for (var i = 0; i < attribs.size(); i++) {
                final var attrib = attribs.get(i);
                GL20.glBindAttribLocation(id, i, attrib);
                PDAMod.LOGGER.debug("Bound vertex format attribute {}={} for program {}", attrib, i, id);
            }
            GL20.glLinkProgram(id);
            if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                final var length = GL20.glGetProgrami(id, GL20.GL_INFO_LOG_LENGTH);
                final var log = GL20.glGetProgramInfoLog(id, length);
                PDAMod.LOGGER.error("Could not link shader program {}: {}", id, log);
            }
            isLinked.set(true);
        });
    }

    private static final class ProgramAdaptor extends Program {
        public ProgramAdaptor(final ShaderObject object) {
            super(getType(object.getType()), object.getId(), object.getLocation().toString());
        }

        private static Type getType(final ShaderType type) {
            return switch (type) {
                case VERTEX -> Type.VERTEX;
                case FRAGMENT -> Type.FRAGMENT;
            };
        }

        @Override
        public void attachToShader(final @NotNull Shader shader) {
        }

        @Override
        public void close() {
        }
    }

    private final class ExtendedShaderAdaptor implements ExtendedShader {
        private final ProgramAdaptor vertexProgram = new ProgramAdaptor(getObject(ShaderType.VERTEX));
        private final ProgramAdaptor fragmentProgram = new ProgramAdaptor(getObject(ShaderType.FRAGMENT));

        @Override
        public void setSampler(String name, Object id) {
            // TODO: implement sampler support
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
        public int getId() {
            return id;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public @NotNull Program getVertexProgram() {
            return vertexProgram;
        }

        @Override
        public @NotNull Program getFragmentProgram() {
            return fragmentProgram;
        }

        @Override
        public void attachToProgram() {
        }
    }
}

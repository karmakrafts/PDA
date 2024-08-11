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
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderProgram extends RenderStateShard.ShaderStateShard
    implements ShaderProgram, ExtendedShader {
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
    private final HashMap<ShaderObject, ProgramAdaptor> adaptorCache = new HashMap<>();
    private boolean isLinked;
    private boolean isRelinkRequested;
    private int previousTextureUnit;
    private boolean isBound;

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

        id = GL20.glCreateProgram();
        setupAttributes();
        PDAMod.DISPOSITION_HANDLER.addObject(this);

        // Attach shader objects to program
        for (final var object : objects) {
            object.attach(this);
        }
        // Set up uniform cache with combined samplers
        final var combinedUniforms = new HashMap<>(uniforms);
        for (final var sampler : samplers.object2IntEntrySet()) {
            final var samplerName = sampler.getKey();
            combinedUniforms.put(samplerName, DefaultUniformType.INT.create(samplerName, sampler.getIntValue()));
        }
        uniformCache = new DefaultUniformCache(this, combinedUniforms);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::reload);
    }

    private void setupAttributes() {
        // Bind vertex format attribute locations for VS ins
        final var attribs = vertexFormat.getElementAttributeNames();
        for (var i = 0; i < attribs.size(); i++) {
            final var attrib = attribs.get(i);
            GL20.glBindAttribLocation(id, i, attrib);
            PDAMod.LOGGER.debug("Bound vertex format attribute {}={} for program {}", attrib, i, id);
        }
    }

    private void reload(final RegisterShadersEvent event) {
        relink(Minecraft.getInstance().getResourceManager());
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
    public int getId() {
        return id;
    }

    @Override
    public void unbind() {
        if (!isLinked || !isBound) {
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
        isBound = false;
    }

    @Override
    public void bind() {
        if (!isLinked || isBound) {
            return;
        }
        if (isRelinkRequested) {
            relink(Minecraft.getInstance().getResourceManager());
            isRelinkRequested = false;
        }
        for (final var object : objects) {
            object.onBindProgram(this);
        }
        GL20.glUseProgram(id);
        if (bindCallback != null) {
            bindCallback.accept(this);
        }
        // Update uniforms
        uniformCache.applyAll();
        // Bind/enable samplers
        previousTextureUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        for (final var sample : samplers.object2IntEntrySet()) {
            final var index = sample.getIntValue();
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, samplerTextures.get(index));
        }
        isBound = true;
    }

    @Override
    public void dispose() {
        isLinked = false;
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
        ExtendedRenderSystem.getInstance().setExtendedShader(() -> this);
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

    private void relink(final ResourceProvider provider) {
        unbind();
        isLinked = false;
        for (final var object : objects) { // @formatter:off
            object.recompile(this, provider);
        } // @formatter:on
        GL20.glLinkProgram(id);
        if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            PDAMod.LOGGER.error("Could not link shader program {}: {}", id, GL20.glGetProgramInfoLog(id));
        }
        uniformCache.clear();
        uniformCache.updateAll(); // Flag all uniforms to be re-applied statically
        isLinked = true;
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
}

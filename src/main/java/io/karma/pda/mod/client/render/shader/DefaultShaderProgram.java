/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.uniform.Uniform;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import io.karma.pda.mod.PDAMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
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
    private final Object2IntOpenHashMap<String> samplers;
    private final HashMap<String, Object> constants;
    private final Object2IntOpenHashMap<String> defines;
    private final AtomicBoolean isLinked = new AtomicBoolean(false);
    private final AtomicBoolean isRelinkRequested = new AtomicBoolean(false);

    DefaultShaderProgram(final VertexFormat vertexFormat, final ArrayList<DefaultShaderObject> objects,
                         final HashMap<String, Uniform> uniforms, final @Nullable Consumer<ShaderProgram> bindCallback,
                         final @Nullable Consumer<ShaderProgram> unbindCallback,
                         final Object2IntOpenHashMap<String> samplers, final HashMap<String, Object> constants,
                         final Object2IntOpenHashMap<String> defines) {
        this.vertexFormat = vertexFormat;
        this.objects = objects;
        this.bindCallback = bindCallback;
        this.unbindCallback = unbindCallback;
        this.samplers = samplers;
        this.constants = constants;
        this.defines = defines;
        uniformCache = new DefaultUniformCache(this, uniforms);
        id = GL20.glCreateProgram();
        PDAMod.DISPOSITION_HANDLER.addObject(this);
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        for (final var object : objects) {
            GL20.glAttachShader(id, object.getId()); // Attach all objects right in-place
        }
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
    public int getSampler(final String name) {
        return samplers.getOrDefault(name, -1);
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
        for (final var object : objects) {
            object.onUnbindProgram(this);
        }
        if (unbindCallback != null) {
            unbindCallback.accept(this);
        }
        GL20.glUseProgram(0);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void bind() {
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
        bind();
    }

    @Override
    public void clearRenderState() {
        unbind();
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

    private void relink(final ResourceProvider provider) {
        isLinked.set(false);
        for (final var object : objects) {
            object.recompile(this, provider);
        }
        Minecraft.getInstance().execute(() -> {
            GL20.glLinkProgram(id);
            if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                final var length = GL20.glGetProgrami(id, GL20.GL_INFO_LOG_LENGTH);
                final var log = GL20.glGetProgramInfoLog(id, length);
                PDAMod.LOGGER.error("Could not link shader program {}: {}", id, log);
            }
            isLinked.set(true);
        });
    }
}

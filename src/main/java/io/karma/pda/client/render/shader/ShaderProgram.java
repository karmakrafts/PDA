/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.shader;

import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.common.PDAMod;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ShaderProgram implements ResourceManagerReloadListener, Disposable {
    private final int id;
    private final ArrayList<ShaderObject> objects = new ArrayList<>();
    private boolean isLinked;

    public ShaderProgram() {
        id = GL20.glCreateProgram();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        PDAMod.DISPOSITION_HANDLER.addObject(this);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
    }

    private void relink(final ResourceProvider provider) {
        isLinked = false;
        for (final var object : objects) {
            object.recompile(provider);
        }
        GL20.glLinkProgram(id);
        if (GL11.glGetInteger(GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            final var length = GL11.glGetInteger(GL20.GL_INFO_LOG_LENGTH);
            final var log = GL20.glGetProgramInfoLog(id, length);
            PDAMod.LOGGER.error("Could not link shader program {}: {}", id, log);
            return;
        }
        isLinked = true;
    }

    public ShaderProgram withObject(final ShaderObject object) {
        if (objects.contains(object)) {
            throw new IllegalArgumentException("Object already attached");
        }
        objects.add(object);
        return this;
    }

    public void bind() {
        if (!isLinked) {
            // Lazily relink/recompile is really needed
            relink(Minecraft.getInstance().getResourceManager());
        }
        GL20.glUseProgram(id);
    }

    @Override
    public void onResourceManagerReload(final @NotNull ResourceManager resourceManager) {
        relink(resourceManager);
    }

    @Override
    public void dispose() {
        for (final var object : objects) {
            final var objectId = object.getId();
            GL20.glDetachShader(id, objectId);
            GL20.glDeleteShader(objectId);
        }
        GL20.glDeleteProgram(id);
    }
}

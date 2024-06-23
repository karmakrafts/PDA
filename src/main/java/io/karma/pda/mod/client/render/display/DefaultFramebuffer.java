/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.display;

import io.karma.pda.mod.PDAMod;
import io.karma.pda.api.client.render.display.Framebuffer;
import io.karma.pda.api.display.DisplayResolution;
import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.mod.client.util.TextureUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFramebuffer implements Framebuffer, Disposable {
    private final int id;
    private final int textureId;
    private final int depthTextureId;
    private int previousDrawId = -1;
    private int previousReadId;
    private int previousTexture;
    private int width;
    private int height;

    public DefaultFramebuffer(final DisplayResolution resolution) {
        textureId = TextureUtils.createTexture();
        depthTextureId = TextureUtils.createTexture();
        id = GL30.glGenFramebuffers();
        resize(resolution.getWidth(), resolution.getHeight());
        PDAMod.DISPOSITION_HANDLER.addObject(this);
    }

    @Override
    public void dispose() {
        GL11.glDeleteTextures(depthTextureId);
        GL11.glDeleteTextures(textureId);
        GL30.glDeleteFramebuffers(id);
    }

    public void resize(final int width, final int height) {
        // Resize color and depth texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, 0L);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureId);
        // @formatter:off
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32, width, height,
            0, GL30.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0L);
        // @formatter:on
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Resize framebuffer
        bind();
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureId, 0);
        // @formatter:off
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D,
            depthTextureId, 0);
        // @formatter:on
        unbind();
        // Update internal size
        this.width = width;
        this.height = height;
    }

    @Override
    public void bind() {
        if (previousDrawId != -1) {
            return;
        }
        previousDrawId = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        previousReadId = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
    }

    @Override
    public void unbind() {
        if (previousDrawId == -1) {
            return;
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, previousDrawId);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, previousReadId);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
        previousDrawId = -1;
    }

    @Override
    public void clear(final float r, final float g, final float b, final float a) {
        GL11.glClearColor(r, g, b, a);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public int getColorTexture() {
        return textureId;
    }

    @Override
    public int getDepthTexture() {
        return depthTextureId;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}

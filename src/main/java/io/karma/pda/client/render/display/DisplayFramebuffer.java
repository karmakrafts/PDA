/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.display;

import io.karma.pda.api.common.dispose.Disposable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DisplayFramebuffer implements Disposable {
    private final int id;
    private final int textureId;
    private final int depthTextureId;
    private int previousDrawId = -1;
    private int previousReadId;
    private int previousTexture;
    private int width;
    private int height;
    private boolean isDisposed;

    public DisplayFramebuffer(final int initialWidth, final int initialHeight) {
        textureId = GL11.glGenTextures();
        depthTextureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        applyTextureParams();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureId);
        applyTextureParams();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        id = GL30.glGenFramebuffers();
        resize(initialWidth, initialHeight);
    }

    private static void applyTextureParams() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        GL11.glDeleteTextures(depthTextureId);
        GL11.glDeleteTextures(textureId);
        GL30.glDeleteFramebuffers(id);
        isDisposed = true;
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

    public void unbind() {
        if (previousDrawId == -1) {
            return;
        }
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, previousDrawId);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, previousReadId);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
        previousDrawId = -1;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getDepthTextureId() {
        return depthTextureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

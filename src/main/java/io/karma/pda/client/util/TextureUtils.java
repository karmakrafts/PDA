/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Alexander Hinze
 * @since 08/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class TextureUtils {
    // @formatter:off
    private TextureUtils() {}
    // @formatter:on

    public static int createTexture() {
        final var id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return id;
    }

    public static ByteBuffer toBuffer(final BufferedImage image) {
        final var width = image.getWidth();
        final var data = image.getRGB(0, 0, width, image.getHeight(), null, 0, width);
        final var buffer = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(data);
        buffer.flip();
        return buffer;
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render;

import io.karma.pda.api.util.IntBiConsumer;
import io.karma.pda.mod.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

/**
 * A simple wrapper around GL_ARB_direct_state_access APIs to allow
 * a failsafe fallback onto the default GL API if the extension is not available.
 *
 * @author Alexander Hinze
 * @since 26/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DSA {
    public static final boolean IS_SUPPORTED = GL.getCapabilities().GL_ARB_direct_state_access;

    static {
        if (IS_SUPPORTED) {
            PDAMod.LOGGER.info("Detected GL_ARB_direct_state_access support, enabling DSA");
        }
        else {
            PDAMod.LOGGER.info("Detected no GL_ARB_direct_state_access support, disabling DSA");
        }
    }

    // @formatter:off
    private DSA() {}
    // @formatter:on

    public static int createTexture() {
        if (IS_SUPPORTED) {
            return ARBDirectStateAccess.glCreateTextures(GL11.GL_TEXTURE_2D);
        }
        return GL11.glGenTextures();
    }

    public static void texImage2D(final int texture,
                                  final int level,
                                  final int border,
                                  final int width,
                                  final int height,
                                  final int internalFormat,
                                  final int format,
                                  final int type,
                                  final int[] data) {
        if (IS_SUPPORTED) {
            ARBDirectStateAccess.glTextureStorage2D(texture, 1, internalFormat, width, height);
            if (data == null) {
                return;
            }
            ARBDirectStateAccess.glTextureSubImage2D(texture, level, border, border, width, height, format, type, data);
            return;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, internalFormat, width, height, border, format, type, data);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public static void texParameteri(final int texture, final Consumer<IntBiConsumer> closure) {
        if (IS_SUPPORTED) {
            closure.accept((n, v) -> ARBDirectStateAccess.glTextureParameteri(texture, n, v));
            return;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        closure.accept((n, v) -> GL11.glTexParameteri(GL11.GL_TEXTURE_2D, n, v));
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}

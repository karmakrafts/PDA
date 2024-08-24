/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.mod.PDAMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;

/**
 * @author Alexander Hinze
 * @since 22/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class SSBO {
    private static final boolean IS_SUPPORTED;

    static {
        IS_SUPPORTED = GL.getCapabilities().GL_ARB_shader_storage_buffer_object;
        if (IS_SUPPORTED) {
            PDAMod.LOGGER.info("Detected no support for SSBOs");
        }
        else {
            PDAMod.LOGGER.info("Detected support for SSBOs");
        }
    }
}

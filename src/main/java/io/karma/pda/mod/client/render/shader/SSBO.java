/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.util.LogMarkers;
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
    public static final boolean IS_SUPPORTED = GL.getCapabilities().GL_ARB_shader_storage_buffer_object;

    static {
        if (IS_SUPPORTED) {
            PDAMod.LOGGER.info(LogMarkers.RENDERER,
                "Detected GL_ARB_shader_storage_buffer_object support, enabling SSBOs");
        }
        else {
            PDAMod.LOGGER.info(LogMarkers.RENDERER,
                "Detected no GL_ARB_shader_storage_buffer_object support, disabling SSBOs");
        }
    }
}

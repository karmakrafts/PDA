package io.karma.pda.client.util;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 15/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RenderUtils {
    // @formatter:off
    private RenderUtils() {}
    // @formatter:on

    public static void doOnRenderThread(final RenderCall runnable) {
        if (RenderSystem.isOnRenderThread()) {
            runnable.execute();
        }
        RenderSystem.recordRenderCall(runnable);
    }
}

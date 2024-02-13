package io.karma.pda.client.render.app;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class LauncherAppRenderer implements AppRenderer {
    private final App app;

    public LauncherAppRenderer(final App app) {
        this.app = app;
    }

    @Override
    public void render(final MultiBufferSource bufferSource, final PoseStack poseStack) {

    }
}

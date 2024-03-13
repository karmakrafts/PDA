package io.karma.pda.client.screen;

import io.karma.pda.client.DockAnimationHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 10/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DockScreen extends Screen {
    public DockScreen(final BlockPos pos) {
        super(Component.empty());
        DockAnimationHandler.INSTANCE.engage(pos);
    }

    @Override
    public void onClose() {
        DockAnimationHandler.INSTANCE.disengage();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

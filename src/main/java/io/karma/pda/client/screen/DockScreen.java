package io.karma.pda.client.screen;

import io.karma.pda.client.ClientEventHandler;
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
        ClientEventHandler.INSTANCE.engageDock(pos);
    }

    @Override
    public void onClose() {
        ClientEventHandler.INSTANCE.disengageDock();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

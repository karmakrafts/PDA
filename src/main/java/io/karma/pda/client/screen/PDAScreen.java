package io.karma.pda.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAScreen extends Screen {
    public PDAScreen() {
        super(Component.empty());
    }
}

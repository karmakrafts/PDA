package io.karma.pda.client.screen;

import io.karma.pda.common.PDAMod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAScreen extends Screen {
    public PDAScreen() {
        super(Component.translatable(String.format("screen.%s.pda", PDAMod.MODID)));
    }
}

package io.karma.pda.client.screen;

import io.karma.pda.client.util.ScreenUtils;
import io.karma.pda.common.PDAMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAScreen extends Screen {
    private static final int SCREEN_WIDTH = 160;
    private static final int SCREEN_HEIGHT = 230;
    private static final int DISPLAY_BORDER = 8;
    private static final int DISPLAY_WIDTH = SCREEN_WIDTH - (DISPLAY_BORDER << 1);
    private static final int DISPLAY_OFFSET = 30;
    private static final int DISPLAY_HEIGHT = SCREEN_HEIGHT - (DISPLAY_BORDER << 1) - DISPLAY_OFFSET;

    public PDAScreen() {
        super(Component.translatable(String.format("screen.%s.pda", PDAMod.MODID)));
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY,
                       final float partialTick) {
        renderBackground(graphics);

        final var viewWidth = getBackgroundWidth();
        final var viewHeight = getBackgroundHeight();
        final var bgX = (width >> 1) - (viewWidth >> 1);
        final var bgY = (height >> 1) - (viewHeight >> 1);
        ScreenUtils.drawBackground(graphics, bgX, bgY, viewWidth, viewHeight);

        final var displayWidth = getDisplayWidth();
        final var displayHeight = getDisplayHeight();
        final var displayX = (width >> 1) - (displayWidth >> 1);
        final var displayY = (height >> 1) - (displayHeight >> 1) - (DISPLAY_OFFSET >> 1);
        ScreenUtils.drawBox(graphics, displayX, displayY, displayWidth, displayHeight);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private int getDisplayWidth() {
        return DISPLAY_WIDTH;
    }

    private int getDisplayHeight() {
        return DISPLAY_HEIGHT;
    }

    private int getBackgroundWidth() {
        return SCREEN_WIDTH;
    }

    private int getBackgroundHeight() {
        return SCREEN_HEIGHT;
    }
}

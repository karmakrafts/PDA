package io.karma.pda.client.util;

import io.karma.pda.api.util.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ScreenUtils {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MODID, "textures/gui/default.png");
    private static final int SLICE_SIZE = 4;
    private static final int MIN_SIZE = SLICE_SIZE << 1;
    private static final int TEXTURE_SIZE = SLICE_SIZE * 3;
    private static final int SLOT_SIZE = 18;

    public static void drawBackground(final GuiGraphics graphics, final int x, final int y, int w, int h) {
        if (w < MIN_SIZE) {
            w = MIN_SIZE;
        }
        if (h < MIN_SIZE) {
            h = MIN_SIZE;
        }
        graphics.blitNineSliced(TEXTURE, x, y, w, h, SLICE_SIZE, SLICE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, 0, 0);
    }

    public static void drawBox(final GuiGraphics graphics, final int x, final int y, int w, int h) {
        if (w < MIN_SIZE) {
            w = MIN_SIZE;
        }
        if (h < MIN_SIZE) {
            h = MIN_SIZE;
        }
        // @formatter:off
        graphics.blitNineSliced(TEXTURE, x, y, w, h, SLICE_SIZE, SLICE_SIZE,
            TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE << 1, 0);
        // @formatter:on
    }

    public static void drawSlot(final GuiGraphics graphics, final int x, final int y, int w, int h) {
        if (w < MIN_SIZE) {
            w = MIN_SIZE;
        }
        if (h < MIN_SIZE) {
            h = MIN_SIZE;
        }
        // @formatter:off
        graphics.blitNineSliced(TEXTURE, x, y, w, h, SLICE_SIZE, SLICE_SIZE,
            TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, 0);
        // @formatter:on
    }

    public static void drawSlot(final GuiGraphics graphics, final int x, final int y) {
        drawSlot(graphics, x, y, SLOT_SIZE, SLOT_SIZE);
    }
}

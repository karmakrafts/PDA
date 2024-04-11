/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.gfx;

import io.karma.pda.api.common.util.Color;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrushes {
    private static Brush white;
    private static Brush black;
    private static Brush red;
    private static Brush green;
    private static Brush blue;
    private static Brush lightBlue;
    private static Brush yellow;
    private static Brush orange;
    private static Brush cyan;
    private static Brush magenta;
    private static Brush pink;

    // @formatter:off
    private DefaultBrushes() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void setup(final BrushFactory factory) {
        white = factory.create(Color.WHITE);
        black = factory.create(Color.BLACK);
        red = factory.create(Color.RED);
        green = factory.create(Color.GREEN);
        blue = factory.create(Color.BLUE);
        lightBlue = factory.create(Color.LIGHT_BLUE);
        yellow = factory.create(Color.YELLOW);
        orange = factory.create(Color.ORANGE);
        cyan = factory.create(Color.CYAN);
        magenta = factory.create(Color.MAGENTA);
        pink = factory.create(Color.PINK);
    }

    public static Brush white() {
        return white;
    }

    public static Brush black() {
        return black;
    }

    public static Brush red() {
        return red;
    }

    public static Brush green() {
        return green;
    }

    public static Brush blue() {
        return blue;
    }

    public static Brush lightBlue() {
        return lightBlue;
    }

    public static Brush yellow() {
        return yellow;
    }

    public static Brush orange() {
        return orange;
    }

    public static Brush cyan() {
        return cyan;
    }

    public static Brush magenta() {
        return magenta;
    }

    public static Brush pink() {
        return pink;
    }
}

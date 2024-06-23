/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.flex;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.util.yoga.Yoga;

import java.util.Arrays;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public enum FlexEdge {
    // @formatter:off
    LEFT  (Yoga.YGEdgeLeft),
    RIGHT (Yoga.YGEdgeRight),
    TOP   (Yoga.YGEdgeTop),
    BOTTOM(Yoga.YGEdgeBottom);
    // @formatter:on

    private final int value;

    FlexEdge(final int value) {
        this.value = value;
    }

    public static FlexEdge byValue(final int value) { // @formatter:off
        return Arrays.stream(values())
            .filter(edge -> edge.value == value)
            .findFirst()
            .orElseThrow();
    } // @formatter:on

    public int getValue() {
        return value;
    }
}

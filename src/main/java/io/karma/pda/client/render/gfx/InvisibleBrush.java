/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.common.util.Color;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
public class InvisibleBrush implements Brush {
    public static final InvisibleBrush INSTANCE = new InvisibleBrush();

    // @formatter:off
    private InvisibleBrush() {}
    // @formatter:on

    @Override
    public Color getColor(final int vertexIndex) {
        return Color.NONE;
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}

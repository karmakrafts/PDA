/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.common.util.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ColorBrush implements Brush {
    private final Color color;

    public ColorBrush(final Color color) {
        this.color = color;
    }

    @Override
    public Color getColor(final int vertexIndex) {
        return color;
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        return null;
    }
}

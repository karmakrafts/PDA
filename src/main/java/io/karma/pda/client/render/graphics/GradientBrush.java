/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.color.Gradient;
import io.karma.pda.api.common.util.RectangleCorner;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class GradientBrush implements Brush {
    private final RenderType renderType;
    private final Gradient gradient;
    private final ResourceLocation texture;

    public GradientBrush(final RenderType renderType, final Gradient gradient,
                         final @Nullable ResourceLocation texture) {
        this.renderType = renderType;
        this.gradient = gradient;
        this.texture = texture;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public Color getColor(final RectangleCorner corner) {
        return gradient.sample(corner);
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        return texture;
    }
}

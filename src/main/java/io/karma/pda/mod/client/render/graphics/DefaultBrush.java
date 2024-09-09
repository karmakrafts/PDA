/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.peregrine.api.color.ColorProvider;
import io.karma.peregrine.api.util.RectangleCorner;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 27/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrush implements Brush {
    private final Function<DisplayMode, RenderType> renderType;
    private final ColorProvider color;
    private final ResourceLocation texture;

    public DefaultBrush(final Function<DisplayMode, RenderType> renderType,
                        final ColorProvider color,
                        final ResourceLocation texture) {
        this.renderType = renderType;
        this.color = color;
        this.texture = texture;
    }

    @Override
    public RenderType getRenderType(final DisplayMode displayMode) {
        return renderType.apply(displayMode);
    }

    @Override
    public int getColor(final RectangleCorner corner) {
        return color.getColor(corner);
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        return texture;
    }
}

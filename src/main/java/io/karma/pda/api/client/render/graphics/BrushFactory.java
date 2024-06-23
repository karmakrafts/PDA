/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.color.ColorProvider;
import io.karma.pda.api.util.Identifiable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface BrushFactory {
    Brush getInvisible();

    Brush createDebug(final Identifiable identifiable);

    Brush create(final ColorProvider color);

    Brush create(final ColorProvider color, final ResourceLocation texture);

    Brush create(final RenderType renderType, final ColorProvider color, final ResourceLocation texture);

    Brush create(final Function<DisplayMode, RenderType> renderType, final ColorProvider color,
                 final ResourceLocation texture);
}

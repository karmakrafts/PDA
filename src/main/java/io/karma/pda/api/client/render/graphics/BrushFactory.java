/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.util.Color;
import io.karma.pda.api.common.util.Identifiable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface BrushFactory {
    Brush getInvisible();

    Brush createDebugColor(final Identifiable identifiable);

    Brush createColor(final Color color);

    Brush createTexture(final Color color, final ResourceLocation texture);

    Brush create(final RenderType renderType, final Color color, final ResourceLocation texture);
}

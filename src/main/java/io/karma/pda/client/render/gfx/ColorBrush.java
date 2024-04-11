/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.GFXContext;
import io.karma.pda.api.common.util.Color;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ColorBrush implements Brush {
    private final Supplier<Color> color;

    public ColorBrush(final Supplier<Color> color) {
        this.color = color;
    }

    @Override
    public RenderType getRenderType() {
        return DefaultGFXRenderTypes.COLOR;
    }

    @Override
    public void apply(final GFXContext context) {

    }
}

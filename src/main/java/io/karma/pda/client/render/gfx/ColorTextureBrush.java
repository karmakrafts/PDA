/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.GFXContext;
import io.karma.pda.api.common.util.Color;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ColorTextureBrush implements Brush {
    private final Supplier<Color> color;
    private final Supplier<ResourceLocation> texture;

    public ColorTextureBrush(final Supplier<Color> color, final Supplier<ResourceLocation> texture) {
        this.color = color;
        this.texture = texture;
    }

    @Override
    public RenderType getRenderType() {
        return DefaultGFXRenderTypes.COLOR_TEXTURE;
    }

    @Override
    public void apply(final GFXContext context) {

    }
}

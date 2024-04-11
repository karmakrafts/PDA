/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.BrushFactory;
import io.karma.pda.api.client.render.gfx.GFXContext;
import io.karma.pda.api.common.util.Color;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrushFactory implements BrushFactory {
    public static final DefaultBrushFactory INSTANCE = new DefaultBrushFactory();

    // @formatter:off
    private DefaultBrushFactory() {}
    // @formatter:on

    @Override
    public Brush create(final Supplier<Color> colorSupplier) {
        return new ColorBrush(colorSupplier);
    }

    @Override
    public Brush create(final Supplier<ResourceLocation> textureSupplier, final Supplier<Color> colorSupplier) {
        return new ColorTextureBrush(colorSupplier, textureSupplier);
    }

    @Override
    public Brush create(final RenderType renderType, final Consumer<GFXContext> callback) {
        return new DefaultBrush(renderType, callback);
    }
}

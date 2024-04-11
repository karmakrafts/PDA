/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.gfx;

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
public interface BrushFactory {
    Brush create(final Supplier<Color> colorSupplier);

    default Brush create(final Color color) {
        return create(() -> color);
    }

    Brush create(final Supplier<ResourceLocation> textureSupplier, final Supplier<Color> colorSupplier);

    default Brush create(final ResourceLocation texture, final Color color) {
        return create(() -> texture, () -> color);
    }

    Brush create(final RenderType renderType, final Consumer<GFXContext> callback);

    default Brush create(final RenderType renderType) {
        return create(renderType, context -> {
        });
    }
}

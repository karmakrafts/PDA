/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.gfx;

import io.karma.pda.api.common.util.Color;
import io.karma.pda.api.common.util.Identifiable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface BrushFactory {
    Brush createInvisible();

    Brush createDebugBrush(final Identifiable identifiable);

    Brush create(final Color color);

    default Brush create(final Supplier<Color> colorSupplier) {
        return create(colorSupplier.get());
    }

    Brush create(final ResourceLocation texture, final Color color);

    default Brush create(final Supplier<ResourceLocation> textureSupplier, final Supplier<Color> colorSupplier) {
        return create(textureSupplier.get(), colorSupplier.get());
    }
}

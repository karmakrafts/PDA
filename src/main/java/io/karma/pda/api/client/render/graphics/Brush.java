/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.graphics;

import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.util.RectangleCorner;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Brush {
    RenderType getRenderType();

    Color getColor(final RectangleCorner corner);

    @Nullable
    ResourceLocation getTexture();

    default boolean isVisible() {
        return true;
    }
}

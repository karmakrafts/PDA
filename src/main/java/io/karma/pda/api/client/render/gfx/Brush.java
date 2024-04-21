/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.gfx;

import io.karma.pda.api.common.util.Color;
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
    Color getColor(final int vertexIndex);

    @Nullable
    ResourceLocation getTexture();

    void apply(final GFX graphics);
}

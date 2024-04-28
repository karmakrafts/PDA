/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.util.RectangleCorner;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class InvisibleBrush implements Brush {
    public static final InvisibleBrush INSTANCE = new InvisibleBrush();

    // @formatter:off
    private InvisibleBrush() {}
    // @formatter:on

    @Override
    public RenderType getRenderType() {
        return GraphicsRenderTypes.COLOR_TRIS;
    }

    @Override
    public Color getColor(final RectangleCorner corner) {
        return Color.NONE;
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}

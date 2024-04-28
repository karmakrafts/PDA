/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.common.util.Color;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 27/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrush implements Brush {
    private final RenderType renderType;
    private final Color color;
    private final ResourceLocation texture;

    public DefaultBrush(final RenderType renderType, final Color color, final ResourceLocation texture) {
        this.renderType = renderType;
        this.color = color;
        this.texture = texture;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public Color getColor(final int vertexIndex) {
        return color;
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        return texture;
    }
}

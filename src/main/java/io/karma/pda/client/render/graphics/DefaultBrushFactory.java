/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.client.render.graphics.BrushFactory;
import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.color.Gradient;
import io.karma.pda.api.common.util.Identifiable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.digest.MurmurHash3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrushFactory implements BrushFactory {
    public static final DefaultBrushFactory INSTANCE = new DefaultBrushFactory();
    private final HashMap<BrushKey, Brush> brushes = new HashMap<>();

    // @formatter:off
    private DefaultBrushFactory() {}
    // @formatter:on

    @Override
    public Brush getInvisible() {
        return InvisibleBrush.INSTANCE;
    }

    @Override
    public Brush createDebug(final Identifiable identifiable) {
        final var id = identifiable.getId();
        final var lsb = id.getLeastSignificantBits();
        final var msb = id.getMostSignificantBits();
        final var data1 = ((lsb & 0xFFFF_FFFFL) << 32) | (msb >> 32) & 0xFFFF_FFFFL;
        final var data2 = ((msb & 0xFFFF_FFFFL) << 32) | (lsb >> 32) & 0xFFFF_FFFFL;
        final var color1 = Color.unpackRGBA(MurmurHash3.hash32(lsb, msb) | 0xFF);
        final var color2 = Color.unpackRGBA(MurmurHash3.hash32(data1, data2) | 0xFF);
        return create(color1.getLuminance() > color2.getLuminance() ? color1 : color2);
    }

    @Override
    public Brush create(final Color color) {
        return create(GraphicsRenderTypes.COLOR_TRIS, color, null);
    }

    @Override
    public Brush create(final Color color, final ResourceLocation texture) {
        return create(GraphicsRenderTypes.COLOR_TEXTURE_TRIS.apply(texture), color, texture);
    }

    @Override
    public Brush create(final RenderType renderType, final Color color, final ResourceLocation texture) {
        if (color.equals(Color.NONE)) {
            return InvisibleBrush.INSTANCE;
        }
        return brushes.computeIfAbsent(new BrushKey(renderType.name, "none", color, color, texture),
            key -> new DefaultBrush(renderType, color, texture));
    }

    @Override
    public Brush createGradient(final Gradient gradient) {
        return createGradient(GraphicsRenderTypes.COLOR_TRIS, gradient, null);
    }

    @Override
    public Brush createGradient(final Gradient gradient, final ResourceLocation texture) {
        return createGradient(GraphicsRenderTypes.COLOR_TEXTURE_TRIS.apply(texture), gradient, texture);
    }

    @Override
    public Brush createGradient(final RenderType renderType, final Gradient gradient, final ResourceLocation texture) {
        final var start = gradient.getStartColor();
        final var end = gradient.getEndColor();
        if (start.equals(Color.NONE) && end.equals(Color.NONE)) {
            return InvisibleBrush.INSTANCE;
        }
        final var function = gradient.getFunction();
        return brushes.computeIfAbsent(new BrushKey(renderType.name,
            function.getName().toString(),
            start,
            end,
            texture), key -> new GradientBrush(renderType, gradient, texture));
    }

    private record BrushKey(String renderTypeName, String functionName, Color start, Color end,
                            @Nullable ResourceLocation texture) {
    }
}

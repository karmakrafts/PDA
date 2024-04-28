/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.client.render.graphics.BrushFactory;
import io.karma.pda.api.common.util.Color;
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
    public Brush createDebugColor(final Identifiable identifiable) {
        final var id = identifiable.getId();
        final var lsb = id.getLeastSignificantBits();
        final var msb = id.getMostSignificantBits();
        final var data1 = ((lsb & 0xFFFF_FFFFL) << 32) | (msb >> 32) & 0xFFFF_FFFFL;
        final var data2 = ((msb & 0xFFFF_FFFFL) << 32) | (lsb >> 32) & 0xFFFF_FFFFL;
        final var color1 = Color.unpackRGBA(MurmurHash3.hash32(lsb, msb) | 0xFF);
        final var color2 = Color.unpackRGBA(MurmurHash3.hash32(data1, data2) | 0xFF);
        return createColor(color1.getLuminance() > color2.getLuminance() ? color1 : color2);
    }

    @Override
    public Brush createColor(final Color color) {
        return create(GraphicsRenderTypes.COLOR_TRIS, color, null);
    }

    @Override
    public Brush createTexture(final Color color, final ResourceLocation texture) {
        return create(GraphicsRenderTypes.COLOR_TEXTURE_TRIS.apply(texture), color, texture);
    }

    @Override
    public Brush create(final RenderType renderType, final Color color, final ResourceLocation texture) {
        if (color.equals(Color.NONE)) {
            return InvisibleBrush.INSTANCE;
        }
        return brushes.computeIfAbsent(new BrushKey(renderType.name, color, texture),
            key -> new DefaultBrush(renderType, color, texture));
    }

    private record BrushKey(String renderTypeName, Color color, @Nullable ResourceLocation texture) {
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics;

import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.graphics.Brush;
import io.karma.pda.api.client.render.graphics.BrushFactory;
import io.karma.pda.api.client.render.graphics.GraphicsContext;
import io.karma.pda.api.color.Color;
import io.karma.pda.api.color.ColorProvider;
import io.karma.pda.api.util.Identifiable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.digest.MurmurHash3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrushFactory implements BrushFactory {
    private final GraphicsContext context;
    private final HashMap<BrushKey, Brush> brushes = new HashMap<>();

    public DefaultBrushFactory(final GraphicsContext context) {
        this.context = context;
    }

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
    public Brush create(final ColorProvider color) {
        return create(GraphicsRenderTypes.COLOR_TRIS.apply(context.getDisplayMode()), color, null);
    }

    @Override
    public Brush create(final ColorProvider color, final ResourceLocation texture) {
        return create(GraphicsRenderTypes.getColorTextureTris(context.getDisplayMode(), texture), color, texture);
    }

    @Override
    public Brush create(final RenderType renderType, final ColorProvider color, final ResourceLocation texture) {
        if (color == Color.NONE) {
            return InvisibleBrush.INSTANCE;
        }
        return brushes.computeIfAbsent(new BrushKey(renderType.name, "none", color, color, texture),
            key -> new DefaultBrush(displayMode -> renderType, color, texture));
    }

    @Override
    public Brush create(final Function<DisplayMode, RenderType> renderType, final ColorProvider color,
                        final ResourceLocation texture) {
        if (color == Color.NONE) {
            return InvisibleBrush.INSTANCE;
        }
        return brushes.computeIfAbsent(new BrushKey(renderType.apply(context.getDisplayMode()).name,
            "none",
            color,
            color,
            texture), key -> new DefaultBrush(renderType, color, texture));
    }

    private record BrushKey(String renderTypeName, String functionName, ColorProvider start, ColorProvider end,
                            @Nullable ResourceLocation texture) {
    }
}

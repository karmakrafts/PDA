/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.BrushFactory;
import io.karma.pda.api.common.util.Color;
import io.karma.pda.api.common.util.Identifiable;
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
    public Brush createDebugBrush(final Identifiable identifiable) {
        final var id = identifiable.getId();
        return create(Color.unpackRGBA(MurmurHash3.hash32(id.getLeastSignificantBits(),
            id.getMostSignificantBits()) | 0xFF));
    }

    @Override
    public Brush create(final Color color) {
        return brushes.computeIfAbsent(new BrushKey(GFXRenderTypes.COLOR_TRIS.name, color, null),
            key -> new ColorBrush(color));
    }

    @Override
    public Brush create(final ResourceLocation texture, final Color color) {
        return brushes.computeIfAbsent(new BrushKey(GFXRenderTypes.COLOR_TEXTURE_TRIS.name, color, texture),
            key -> new ColorTextureBrush(color, texture));
    }

    private record BrushKey(String renderTypeName, Color color, @Nullable ResourceLocation texture) {
    }
}

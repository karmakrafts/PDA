/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.gfx;

import io.karma.pda.api.client.render.gfx.Brush;
import io.karma.pda.api.client.render.gfx.GFXContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultBrush implements Brush {
    private final RenderType renderType;
    private final Consumer<GFXContext> callback;

    public DefaultBrush(final RenderType renderType, final Consumer<GFXContext> callback) {
        this.renderType = renderType;
        this.callback = callback;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public void apply(final GFXContext context) {
        callback.accept(context);
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.app;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class AppRenderers {
    private static final HashMap<AppType<?>, AppRenderer<?>> RENDERERS = new HashMap<>();

    // @formatter:off
    private AppRenderers() {}
    // @formatter:on

    @Internal
    public static <A extends App> void register(final AppType<A> type, final AppRenderer<A> renderer) {
        if (RENDERERS.containsKey(type)) {
            throw new IllegalArgumentException(String.format("App renderer '%s' is already registered", type));
        }
        RENDERERS.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public static <A extends App> AppRenderer<A> get(final AppType<A> type) {
        final var renderer = RENDERERS.get(type);
        if (renderer == null) {
            throw new IllegalArgumentException(String.format("No renderer registered for app '%s'", type));
        }
        return (AppRenderer<A>) renderer;
    }
}

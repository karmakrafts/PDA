/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.component;

import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ComponentRenderers {
    private static final HashMap<ComponentType<?>, ComponentRenderer<?>> RENDERERS = new HashMap<>();

    // @formatter:off
    private ComponentRenderers() {}
    // @formatter:on

    @ApiStatus.Internal
    public static <C extends Component> void register(final ComponentType<C> type,
                                                      final ComponentRenderer<C> renderer) {
        if (RENDERERS.containsKey(type)) {
            throw new IllegalArgumentException(String.format("Component renderer '%s' is already registered", type));
        }
        RENDERERS.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Component> ComponentRenderer<C> get(final ComponentType<C> type) {
        final var renderer = RENDERERS.get(type);
        if (renderer == null) {
            throw new IllegalArgumentException(String.format("No renderer registered for component '%s'", type));
        }
        return (ComponentRenderer<C>) renderer;
    }
}

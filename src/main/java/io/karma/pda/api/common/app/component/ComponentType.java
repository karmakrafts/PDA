/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;
import java.util.function.BiFunction;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ComponentType<C extends Component> {
    private static final ComponentType<?> NULL = new ComponentType<>(null, null);

    private final ResourceLocation name;
    private final BiFunction<ComponentType<C>, UUID, C> factory;

    public ComponentType(final ResourceLocation name, final BiFunction<ComponentType<C>, UUID, C> factory) {
        this.name = name;
        this.factory = factory;
    }

    public ResourceLocation getName() {
        return name;
    }

    public C create(final UUID uuid) {
        return factory.apply(this, uuid);
    }

    public C create() {
        return create(UUID.randomUUID());
    }

    @SuppressWarnings("unchecked")
    public static <C extends Component> ComponentType<C> nullType() {
        return (ComponentType<C>) NULL;
    }
}

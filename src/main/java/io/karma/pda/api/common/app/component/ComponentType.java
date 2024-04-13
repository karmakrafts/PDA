/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.flex.StaticFlexNode;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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

    public C create(final UUID uuid, final Consumer<StaticFlexNode.Builder> props) {
        final var builder = StaticFlexNode.builder();
        props.accept(builder);
        final var component = factory.apply(this, uuid);
        component.getFlexNode().setFrom(builder.build());
        return component;
    }

    public C create(final Consumer<StaticFlexNode.Builder> props) {
        return create(UUID.randomUUID(), props);
    }

    public C create() {
        return create(props -> {
        });
    }

    @SuppressWarnings("unchecked")
    public static <C extends Component> ComponentType<C> nullType() {
        return (ComponentType<C>) NULL;
    }
}

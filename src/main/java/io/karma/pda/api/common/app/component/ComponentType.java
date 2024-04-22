/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.flex.DefaultFlexNode;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ComponentType<C extends Component> {
    private static final ComponentType<?> NULL = new ComponentType<>(null, null, null);

    private final ResourceLocation name;
    private final Class<C> type;
    private final BiFunction<ComponentType<C>, UUID, C> factory;

    public ComponentType(final ResourceLocation name, final Class<C> type,
                         final BiFunction<ComponentType<C>, UUID, C> factory) {
        this.name = name;
        this.type = type;
        this.factory = factory;
    }

    public ResourceLocation getName() {
        return name;
    }

    public Class<C> getType() {
        return type;
    }

    public C create(final UUID uuid, final Consumer<DefaultFlexNode.Builder> props) {
        final var builder = DefaultFlexNode.builder();
        props.accept(builder);
        final var component = factory.apply(this, uuid);
        component.getFlexNode().setFrom(builder.build());
        return component;
    }

    public C create(final Consumer<DefaultFlexNode.Builder> props) {
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

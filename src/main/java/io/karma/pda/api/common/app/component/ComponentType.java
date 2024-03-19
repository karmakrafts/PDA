/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ComponentType<C extends Component> {
    private final ResourceLocation name;
    private final Function<UUID, C> factory;

    public ComponentType(final ResourceLocation name, final Function<UUID, C> factory) {
        this.name = name;
        this.factory = factory;
    }

    public ResourceLocation getName() {
        return name;
    }

    public C create(final UUID uuid) {
        return factory.apply(uuid);
    }

    public C create() {
        return create(UUID.randomUUID());
    }
}

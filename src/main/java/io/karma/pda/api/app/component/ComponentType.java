package io.karma.pda.api.app.component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public enum ComponentType {
    // @formatter:off
    CONTAINER   (ContainerComponent::new),
    TEXT        (TextComponent::new);
    // @formatter:on

    private final Supplier<Component> factory;

    ComponentType(final Supplier<Component> factory) {
        this.factory = factory;
    }

    public static Optional<ComponentType> byName(final String name) {
        return Arrays.stream(values()).filter(type -> type.name().toLowerCase().equals(name)).findFirst();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public Component create() {
        return factory.get();
    }
}

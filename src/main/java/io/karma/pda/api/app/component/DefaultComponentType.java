package io.karma.pda.api.app.component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public enum DefaultComponentType implements ComponentType {
    // @formatter:off
    CONTAINER   (ContainerComponent::new),
    TEXT        (TextComponent::new);
    // @formatter:on

    private final Supplier<Component> factory;

    DefaultComponentType(final Supplier<Component> factory) {
        this.factory = factory;
    }

    public static Optional<DefaultComponentType> byName(final String name) {
        return Arrays.stream(values()).filter(type -> type.name().toLowerCase().equals(name)).findFirst();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Override
    public Component create() {
        return factory.get();
    }
}

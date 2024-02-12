package io.karma.pda.common.dom;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public enum NodeType {
    // @formatter:off
    DOCUMENT    (DefaultDocument::new),
    CONTAINER   (ContainerNode::new),
    TEXT        (TextNode::new);
    // @formatter:on

    private final Supplier<Node> factory;

    NodeType(final Supplier<Node> factory) {
        this.factory = factory;
    }

    public static Optional<NodeType> byName(final String name) {
        return Arrays.stream(values()).filter(type -> type.name().toLowerCase().equals(name)).findFirst();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public Node create() {
        return factory.get();
    }
}

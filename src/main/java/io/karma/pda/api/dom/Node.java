package io.karma.pda.api.dom;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Node {
    NodeType getType();

    void serialize(final ObjectNode node);

    void deserialize(final ObjectNode node);

    @Nullable Node getParent();

    void setParent(final @Nullable Node parent);

    default List<Node> getChildren() {
        return Collections.emptyList();
    }
}

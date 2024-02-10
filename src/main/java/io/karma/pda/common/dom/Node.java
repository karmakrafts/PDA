package io.karma.pda.common.dom;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Node {
    @Nullable Node getParent();

    void setParent(final @Nullable Node parent);

    default List<Node> getChildren() {
        return Collections.emptyList();
    }
}

package io.karma.pda.api.dom;

import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractNode implements Node {
    protected Node parent;

    @Override
    public @Nullable Node getParent() {
        return parent;
    }

    @Override
    public void setParent(final @Nullable Node parent) {
        this.parent = parent;
    }
}

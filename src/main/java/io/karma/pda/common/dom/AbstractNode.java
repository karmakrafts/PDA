package io.karma.pda.common.dom;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.yoga.YGNode;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractNode implements Node {
    protected final YGNode layout = YGNode.malloc();
    protected Node parent;
    private boolean isDisposed;

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        layout.free();
        isDisposed = true;
    }

    @Override
    public YGNode getLayout() {
        return layout;
    }

    @Override
    public @Nullable Node getParent() {
        return parent;
    }

    @Override
    public void setParent(final @Nullable Node parent) {
        this.parent = parent;
    }
}

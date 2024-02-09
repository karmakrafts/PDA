package io.karma.pda.common.dom;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractNode implements Node {
    protected long layout = Yoga.YGNodeNew();
    protected Node parent;
    private boolean isDisposed;

    public AbstractNode() {
        if (layout == MemoryUtil.NULL) {
            throw new IllegalStateException("Could not allocate layout");
        }
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        Yoga.YGNodeFree(layout);
        layout = MemoryUtil.NULL;
        isDisposed = true;
    }

    @Override
    public long getLayout() {
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

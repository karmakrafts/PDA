package io.karma.pda.api.common.app.component;

import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractComponent implements Component {
    protected final ComponentType<?> type;
    protected Component parent;

    protected AbstractComponent(final ComponentType<?> type) {
        this.type = type;
    }

    @Override
    public @Nullable Component getParent() {
        return parent;
    }

    @Override
    public void setParent(final @Nullable Component parent) {
        this.parent = parent;
    }

    @Override
    public ComponentType<?> getType() {
        return type;
    }
}

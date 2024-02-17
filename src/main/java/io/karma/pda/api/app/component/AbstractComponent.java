package io.karma.pda.api.app.component;

import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractComponent implements Component {
    protected final DefaultComponentType type;
    protected Component parent;

    protected AbstractComponent(final DefaultComponentType type) {
        this.type = type;
    }

    @Override
    public @Nullable Component getParent() {
        return parent;
    }

    @Override
    public DefaultComponentType getType() {
        return type;
    }

    @Override
    public void setParent(final @Nullable Component parent) {
        this.parent = parent;
    }
}

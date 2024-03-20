/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.flex.FlexNode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractComponent implements Component {
    protected final ComponentType<?> type;
    protected final UUID uuid;
    protected final FlexNode flexNode = new FlexNode();
    protected Component parent;

    protected AbstractComponent(final ComponentType<?> type, final UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    @Override
    public void dispose() {
        flexNode.dispose();
    }

    @Override
    public UUID getUUID() {
        return uuid;
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

    @Override
    public FlexNode getFlexboxNode() {
        return flexNode;
    }
}

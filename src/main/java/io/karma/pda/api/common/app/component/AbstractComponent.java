/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.flex.FlexNode;
import io.karma.pda.client.flex.DefaultFlexNode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public abstract class AbstractComponent implements Component {
    protected final ComponentType<?> type;
    protected final UUID id;
    protected final DefaultFlexNode flexNode = new DefaultFlexNode();
    protected Component parent;

    protected AbstractComponent(final ComponentType<?> type, final UUID id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
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
    public FlexNode getLayoutSpec() {
        return flexNode;
    }
}

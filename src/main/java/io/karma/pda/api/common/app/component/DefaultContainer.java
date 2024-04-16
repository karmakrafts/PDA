/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class DefaultContainer extends AbstractComponent implements Container {
    protected final HashMap<UUID, Component> children = new HashMap<>();

    public DefaultContainer(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }

    @Override
    public @Nullable Component findChild(final UUID id) {
        return children.get(id);
    }

    @Override
    public Collection<Component> getChildren() {
        return children.values();
    }

    @Override
    public void addChild(final Component child) {
        children.put(child.getId(), child);
        child.setParent(this);
    }

    @Override
    public void removeChild(final Component child) {
        children.remove(child.getId());
        child.setParent(null);
    }

    @Override
    public void dispose() {
        for (final var child : children.values()) {
            child.dispose();
        }
        super.dispose();
    }

    public void clear() {
        children.clear();
    }
}

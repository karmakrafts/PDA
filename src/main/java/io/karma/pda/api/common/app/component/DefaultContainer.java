/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class DefaultContainer extends AbstractComponent implements Container {
    protected final LinkedHashMap<UUID, Component> children = new LinkedHashMap<>();

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
        flexNode.addChild(child.getFlexNode());
    }

    @Override
    public void removeChild(final Component child) {
        children.remove(child.getId());
        child.setParent(null);
        flexNode.removeChild(child.getFlexNode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Container container)) {
            return false;
        }
        for (final var childId : children.keySet()) {
            if (container.findChild(childId) != null) {
                continue;
            }
            return false;
        }
        return id.equals(container.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, children);
    }
}

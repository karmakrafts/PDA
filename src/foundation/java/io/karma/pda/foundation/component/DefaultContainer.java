/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.app.component.Container;
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

    private static @Nullable Component findChildRecursively(final Component component, final UUID id) {
        if (component.getId().equals(id)) {
            return component;
        }
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                final var subChild = findChildRecursively(child, id);
                if (subChild == null) {
                    continue;
                }
                return subChild;
            }
        }
        return null;
    }

    private static @Nullable Component findChildRecursively(final Component component, final String localName) {
        final var name = component.getLocalName();
        if (name != null && name.equals(localName)) {
            return component;
        }
        if (component instanceof Container container) {
            for (final var child : container.getChildren()) {
                final var subChild = findChildRecursively(child, localName);
                if (subChild == null) {
                    continue;
                }
                return subChild;
            }
        }
        return null;
    }

    @Override
    public @Nullable Component findChild(final String localName) {
        for (final var child : children.values()) {
            final var name = child.getLocalName();
            if (name == null || !name.equals(localName)) {
                continue;
            }
            return child;
        }
        return null;
    }

    @Override
    public @Nullable Component findChild(final UUID id) {
        return children.get(id);
    }

    @Override
    public @Nullable Component findChildRecursively(final UUID id) {
        for (final var child : getChildren()) {
            final var component = findChildRecursively(child, id);
            if (component == null) {
                continue;
            }
            return component;
        }
        return null;
    }

    @Override
    public @Nullable Component findChildRecursively(final String localName) {
        for (final var child : getChildren()) {
            final var component = findChildRecursively(child, localName);
            if (component == null) {
                continue;
            }
            return component;
        }
        return null;
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
        return id.get().equals(container.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, children);
    }
}

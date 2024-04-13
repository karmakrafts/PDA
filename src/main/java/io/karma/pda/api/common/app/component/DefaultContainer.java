/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.sliced.slice.Slice;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class DefaultContainer extends AbstractComponent implements Container {
    protected final ArrayList<Component> children = new ArrayList<>();
    protected final HashMap<UUID, Component> idToChildren = new HashMap<>();

    public DefaultContainer(final ComponentType<?> type, final UUID uuid) {
        super(type, uuid);
    }

    @Override
    public @Nullable Component findChild(final UUID uuid) {
        return idToChildren.get(uuid);
    }

    @Override
    public Slice<Component> getChildren() {
        return Slice.of(children);
    }

    @Override
    public void addChild(final Component child) {
        children.add(child);
        idToChildren.put(child.getId(), child);
        child.setParent(this);
    }

    @Override
    public void removeChild(final Component child) {
        children.remove(child);
        idToChildren.remove(child.getId());
        child.setParent(null);
    }

    public void addChild(final int index, final Component child) {
        children.add(index, child);
        child.setParent(this);
    }

    public int indexOfChild(final Component child) {
        return children.indexOf(child);
    }

    public void clear() {
        while (!children.isEmpty()) {
            removeChild(children.remove(0));
        }
    }
}

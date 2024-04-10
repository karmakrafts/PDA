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
    protected final HashMap<UUID, Component> uuidToChildren = new HashMap<>();

    public DefaultContainer(final UUID uuid) {
        super(DefaultComponents.CONTAINER, uuid);
    }

    @Override
    public @Nullable Component findChild(final UUID uuid) {
        return uuidToChildren.get(uuid);
    }

    @Override
    public Slice<Component> getChildren() {
        return Slice.of(children);
    }

    @Override
    public void addChild(final Component child) {
        children.add(child);
        uuidToChildren.put(child.getUUID(), child);
        child.setParent(this);
    }

    @Override
    public void removeChild(final Component child) {
        children.remove(child);
        uuidToChildren.remove(child.getUUID());
        child.setParent(null);
    }

    public void addChild(final int index, final Component child) {
        children.add(index, child);
        child.setParent(this);
    }

    public int indexOfChild(final Component child) {
        return children.indexOf(child);
    }
}

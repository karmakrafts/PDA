/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class DefaultContainer extends AbstractComponent implements Container {
    public static final String KEY_CHILDREN = "children";

    protected final ArrayList<Component> children = new ArrayList<>();

    public DefaultContainer() {
        super(DefaultComponents.CONTAINER);
    }

    public DefaultContainer(final Component... components) {
        this();
        children.addAll(Arrays.asList(components));
    }

    @Override
    public List<Component> getChildren() {
        return children;
    }

    public void addChild(final int index, final Component child) {
        children.add(index, child);
        child.setParent(this);
    }

    public void addChild(final Component child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(final Component child) {
        children.remove(child);
        child.setParent(null);
    }

    public int indexOfChild(final Component child) {
        return children.indexOf(child);
    }
}

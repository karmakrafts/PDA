package io.karma.pda.api.app.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class ContainerComponent extends AbstractComponent {
    public static final String KEY_CHILDREN = "children";

    protected final ArrayList<Component> children = new ArrayList<>();

    public ContainerComponent() {
    }

    public ContainerComponent(final Component... components) {
        children.addAll(Arrays.asList(components));
    }

    @Override
    public ComponentType getType() {
        return ComponentType.CONTAINER;
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

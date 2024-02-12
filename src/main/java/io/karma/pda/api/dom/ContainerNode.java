package io.karma.pda.api.dom;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class ContainerNode extends AbstractNode {
    public static final String KEY_CHILDREN = "children";

    protected final ArrayList<Node> children = new ArrayList<>();

    public ContainerNode() {
    }

    public ContainerNode(final Node... nodes) {
        children.addAll(Arrays.asList(nodes));
    }

    @Override
    public NodeType getType() {
        return NodeType.CONTAINER;
    }

    @Override
    public void serialize(final ObjectNode node) {

    }

    @Override
    public void deserialize(final ObjectNode node) {

    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    public void addChild(final int index, final Node child) {
        children.add(index, child);
        child.setParent(this);
    }

    public void addChild(final Node child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(final Node child) {
        children.remove(child);
        child.setParent(null);
    }

    public int indexOfChild(final Node child) {
        return children.indexOf(child);
    }
}

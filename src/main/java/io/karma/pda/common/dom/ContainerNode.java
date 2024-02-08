package io.karma.pda.common.dom;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class ContainerNode extends AbstractNode {
    protected final ArrayList<Node> children = new ArrayList<>();

    public ContainerNode() {
    }

    public ContainerNode(final Node... nodes) {
        children.addAll(Arrays.asList(nodes));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(final Graphics graphics, float partialTick, int mouseX, int mouseY) {
        final var layout = this.layout.layout();
        final var x = (int) layout.positions(0);
        final var y = (int) layout.positions(1);
        graphics.translate(x, y); // Translate to current layout position
        for (final var child : children) {
            child.render(graphics, partialTick, mouseX, mouseY);
        }
    }

    @Override
    public void dispose() {
        for (final var child : getChildren()) {
            child.dispose();
        }
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

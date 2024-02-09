package io.karma.pda.common.dom;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.util.yoga.Yoga;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Node {
    long getLayout();

    default Vector2i getPixelPosition() {
        final var address = getLayout();
        var x = Yoga.YGNodeLayoutGetLeft(address);
        var y = Yoga.YGNodeLayoutGetTop(address);
        final var parent = getParent();
        if (parent != null) {
            final var parentPos = parent.getPosition();
            x += parentPos.x;
            y += parentPos.y;
        }
        return new Vector2i((int) x, (int) y);
    }

    default Vector2f getPosition() {
        final var address = getLayout();
        var x = Yoga.YGNodeLayoutGetLeft(address);
        var y = Yoga.YGNodeLayoutGetTop(address);
        final var parent = getParent();
        if (parent != null) {
            final var parentPos = parent.getPosition();
            x += parentPos.x;
            y += parentPos.y;
        }
        return new Vector2f(x, y);
    }

    default Vector2i getPixelSize() {
        final var address = getLayout();
        return new Vector2i((int) Yoga.YGNodeLayoutGetWidth(address), (int) Yoga.YGNodeLayoutGetHeight(address));
    }

    default Vector2f getSize() {
        final var address = getLayout();
        return new Vector2f(Yoga.YGNodeLayoutGetWidth(address), Yoga.YGNodeLayoutGetHeight(address));
    }

    @Nullable Node getParent();

    void setParent(final @Nullable Node parent);

    default void dispose() {
    }

    default List<Node> getChildren() {
        return Collections.emptyList();
    }
}

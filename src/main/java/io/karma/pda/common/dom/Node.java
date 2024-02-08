package io.karma.pda.common.dom;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.yoga.YGNode;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public interface Node {
    YGNode getLayout();

    @Nullable Node getParent();

    void setParent(final @Nullable Node parent);

    @OnlyIn(Dist.CLIENT)
    void render(final Graphics graphics, final float partialTick, final int mouseX, final int mouseY);

    default void dispose() {
    }

    default List<Node> getChildren() {
        return Collections.emptyList();
    }
}

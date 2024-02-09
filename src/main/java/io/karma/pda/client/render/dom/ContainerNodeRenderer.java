package io.karma.pda.client.render.dom;

import io.karma.pda.common.dom.ContainerNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ContainerNodeRenderer implements NodeRenderer<ContainerNode> {
    public static final ContainerNodeRenderer INSTANCE = new ContainerNodeRenderer();

    // @formatter:off
    private ContainerNodeRenderer() {}
    // @formatter:on

    @Override
    public void render(final ContainerNode node, final NodeRenderContext context) {
        final var poseStack = context.getGraphics().pose();
        final var x = Yoga.YGNodeLayoutGetLeft(node.getLayout());
        final var y = Yoga.YGNodeLayoutGetTop(node.getLayout());
        poseStack.pushPose();
        poseStack.translate(x, y, 1F);
        for (final var child : node.getChildren()) {
            NodeRenderers.render(child, context);
        }
        poseStack.popPose();
    }
}

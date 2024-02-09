package io.karma.pda.client.render.dom;

import io.karma.pda.common.dom.ContainerNode;
import io.karma.pda.common.dom.Node;
import io.karma.pda.common.dom.TextNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class NodeRenderers {
    private static final HashMap<Class<? extends Node>, NodeRenderer<? extends Node>> RENDERERS = new HashMap<>();

    // @formatter:off
    private NodeRenderers() {}
    // @formatter:on

    public static void setup() {
        register(ContainerNode.class, ContainerNodeRenderer.INSTANCE);
        register(TextNode.class, TextNodeRenderer.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public static void render(final Node node, final NodeRenderContext context) {
        ((NodeRenderer<Node>) get(node.getClass())).render(node, context);
    }

    public static <N extends Node> void register(final Class<N> type, final NodeRenderer<N> renderer) {
        if (RENDERERS.containsKey(type)) {
            throw new IllegalArgumentException(String.format("Renderer already registered for type %s", type));
        }
        RENDERERS.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Node> NodeRenderer<N> get(final Class<N> type) {
        if (!RENDERERS.containsKey(type)) {
            throw new IllegalArgumentException(String.format("No renderer registered for type %s", type));
        }
        return (NodeRenderer<N>) RENDERERS.get(type);
    }
}

package io.karma.pda.client.render.dom;

import io.karma.pda.client.render.display.DisplayRenderContext;
import io.karma.pda.common.dom.Node;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface NodeRenderer<N extends Node> {
    void render(final N node, final DisplayRenderContext context);
}

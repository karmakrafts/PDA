package io.karma.pda.client.render.dom;

import io.karma.pda.client.render.display.DisplayRenderContext;
import io.karma.pda.common.dom.TextNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class TextNodeRenderer implements NodeRenderer<TextNode> {
    public static final TextNodeRenderer INSTANCE = new TextNodeRenderer();

    // @formatter:off
    private TextNodeRenderer() {}
    // @formatter:on

    @Override
    public void render(final TextNode node, final DisplayRenderContext context) {

    }
}

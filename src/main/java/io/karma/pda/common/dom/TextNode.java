package io.karma.pda.common.dom;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public final class TextNode extends AbstractNode {
    private final String text;
    private final int color;

    public TextNode(final String text, final int color) {
        this.text = text;
        this.color = color;
    }

    public TextNode(final String text) {
        this(text, 0xFF101010);
    }

    @Override
    public NodeType getType() {
        return NodeType.TEXT;
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }
}

package io.karma.pda.common.dom;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public final class TextNode extends AbstractNode {
    private static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    private String text;
    private int color;

    public TextNode(final String text, final int color) {
        this.text = text;
        this.color = color;
    }

    public TextNode(final String text) {
        this(text, DEFAULT_TEXT_COLOR);
    }

    public TextNode() {
        this("", DEFAULT_TEXT_COLOR);
    }

    @Override
    public NodeType getType() {
        return NodeType.TEXT;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(final int color) {
        this.color = color;
    }
}

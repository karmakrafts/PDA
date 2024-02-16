package io.karma.pda.api.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public final class TextComponent extends AbstractComponent {
    private static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    private String text;
    private int color;

    public TextComponent(final String text, final int color) {
        this.text = text;
        this.color = color;
    }

    public TextComponent(final String text) {
        this(text, DEFAULT_TEXT_COLOR);
    }

    public TextComponent() {
        this("", DEFAULT_TEXT_COLOR);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.TEXT;
    }

    @Override
    public void serialize(final ObjectNode node) {

    }

    @Override
    public void deserialize(final ObjectNode node) {

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

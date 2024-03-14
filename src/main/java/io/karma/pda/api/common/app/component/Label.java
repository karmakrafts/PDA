/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class Label extends AbstractComponent {
    private static final String TAG_TEXT = "text";
    private static final String TAG_COLOR = "color";
    private static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    private String text;
    private int color;

    public Label(final String text, final int color) {
        super(DefaultComponents.LABEL);
        this.text = text;
        this.color = color;
    }

    public Label(final String text) {
        this(text, DEFAULT_TEXT_COLOR);
    }

    public Label() {
        this("", DEFAULT_TEXT_COLOR);
    }

    @Override
    public void serialize(final ObjectNode node) {
        node.put(TAG_TEXT, text);
        node.put(TAG_COLOR, color);
    }

    @Override
    public void deserialize(final ObjectNode node) {
        text = node.get(TAG_TEXT).asText();
        color = node.get(TAG_COLOR).asInt();
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

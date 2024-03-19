/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

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

    public Label(final UUID uuid) {
        super(DefaultComponents.LABEL, uuid);
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

    public void setText(final String text) {
        this.text = text;
    }

    public void setColor(final int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }
}

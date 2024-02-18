/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class LabelComponent extends AbstractComponent {
    private static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    private String text;
    private int color;

    public LabelComponent(final String text, final int color) {
        super(DefaultComponents.LABEL);
        this.text = text;
        this.color = color;
    }

    public LabelComponent(final String text) {
        this(text, DEFAULT_TEXT_COLOR);
    }

    public LabelComponent() {
        this("", DEFAULT_TEXT_COLOR);
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

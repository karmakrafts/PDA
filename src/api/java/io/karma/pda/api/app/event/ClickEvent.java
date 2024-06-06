/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.event;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class ClickEvent {
    private final int x;
    private final int y;
    private final int button;

    public ClickEvent(int x, int y, int button) {
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getKey() {
        return button;
    }
}

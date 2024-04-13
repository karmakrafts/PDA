/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.event;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public final class MouseMoveEvent {
    private final int x;
    private final int y;

    public MouseMoveEvent(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

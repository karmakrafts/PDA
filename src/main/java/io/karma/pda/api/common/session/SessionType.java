/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.session;

import net.minecraft.world.InteractionHand;

/**
 * @author Alexander Hinze
 * @since 04/04/2024
 */
public enum SessionType {
    // @formatter:off
    HANDHELD_MAIN(true),
    HANDHELD_OFF (true),
    DOCKED       (false);
    // @formatter:on

    private final boolean isHandheld;

    SessionType(final boolean isHandheld) {
        this.isHandheld = isHandheld;
    }

    public static SessionType fromHand(final InteractionHand hand) {
        return switch (hand) {
            case OFF_HAND -> HANDHELD_OFF;
            default -> HANDHELD_MAIN;
        };
    }

    public boolean isHandheld() {
        return isHandheld;
    }
}

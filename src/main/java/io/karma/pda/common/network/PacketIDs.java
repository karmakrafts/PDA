/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class PacketIDs {
    public static final int SB_CREATE_SESSION = 1;
    public static final int SB_TERMINATE_SESSION = 2;

    public static final int CB_CREATE_SESSION = 3;

    // @formatter:off
    private PacketIDs() {}
    // @formatter:on
}

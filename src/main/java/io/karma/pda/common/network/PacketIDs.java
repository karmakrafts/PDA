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
    public static final int SB_SYNC_VALUES = 3;
    public static final int SB_OPEN_APP = 4;
    public static final int SB_CLOSE_APP = 5;

    public static final int CB_CREATE_SESSION = 6;
    public static final int CB_TERMINATE_SESSION = 7;
    public static final int CB_SYNC_VALUES = 8;
    public static final int CB_OPEN_APP = 9;
    public static final int CB_CLOSE_APP = 10;

    // @formatter:off
    private PacketIDs() {}
    // @formatter:on
}

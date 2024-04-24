/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class PacketIDs {
    private static int currentId = 0;

    public static final int SB_CREATE_SESSION = getNextId();
    public static final int SB_TERMINATE_SESSION = getNextId();
    public static final int SB_OPEN_APP = getNextId();
    public static final int SB_CLOSE_APP = getNextId();
    public static final int SB_SYNC_VALUES = getNextId();

    public static final int CB_CREATE_SESSION = getNextId();
    public static final int CB_TERMINATE_SESSION = getNextId();
    public static final int CB_OPEN_APP = getNextId();
    public static final int CB_CLOSE_APP = getNextId();
    public static final int CB_SYNC_VALUES = getNextId();

    // @formatter:off
    private PacketIDs() {}
    // @formatter:on

    private static int getNextId() {
        return currentId++;
    }
}

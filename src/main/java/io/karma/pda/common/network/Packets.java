package io.karma.pda.common.network;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class Packets {
    public static final int S_OPEN_APP = 1;
    // @formatter:on
    public static final int S_OPEN_BE_APP = 2;
    public static final int S_OPEN_IE_APP = 3;
    public static final int S_CLOSE_APP = 4;
    public static final int S_CLOSE_BE_APP = 5;
    public static final int S_CLOSE_IE_APP = 6;

    // @formatter:off
    private Packets() {}
}

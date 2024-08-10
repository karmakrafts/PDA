/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
public final class Exceptions {
    // @formatter:off
    private Exceptions() {}
    // @formatter:on

    public static String getStackTrace(final Throwable error) {
        // @formatter:off
        return Arrays.stream(error.getStackTrace())
            .map(e -> String.format("\t%s", e))
            .collect(Collectors.joining("\n"));
        // @formatter:on
    }

    public static String toFancyString(final Throwable error) {
        return String.format("%s\n%s", error, getStackTrace(error));
    }
}

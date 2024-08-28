/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import org.apache.commons.io.file.DeletingPathVisitor;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Alexander Hinze
 * @since 28/08/2024
 */
public final class IOUtils {
    // @formatter:off
    private IOUtils() {}
    // @formatter:on

    public static boolean deleteIfExists(final Path path) {
        try {
            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    Files.walkFileTree(path, DeletingPathVisitor.withLongCounters());
                    return true;
                }
                Files.delete(path);
                return true;
            }
            return false;
        }
        catch (Throwable error) {
            return false;
        }
    }
}

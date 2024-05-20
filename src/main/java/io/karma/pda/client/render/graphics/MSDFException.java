/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

/**
 * A general exception type used by {@link MSDFUtils} and {@link MSDFGenFont}.
 *
 * @author Alexander Hinze
 */
public final class MSDFException extends RuntimeException {
    public MSDFException() {
        super();
    }

    public MSDFException(String message) {
        super(message);
    }

    public MSDFException(String message, Throwable cause) {
        super(message, cause);
    }

    public MSDFException(Throwable cause) {
        super(cause);
    }
}

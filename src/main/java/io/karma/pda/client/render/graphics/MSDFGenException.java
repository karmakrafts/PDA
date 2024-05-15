/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

/**
 * A general exception type used by {@link MSDFGenUtil} and {@link MSDFGenFont}.
 *
 * @author Alexander Hinze
 */
public final class MSDFGenException extends RuntimeException {
    public MSDFGenException() {
        super();
    }

    public MSDFGenException(String message) {
        super(message);
    }

    public MSDFGenException(String message, Throwable cause) {
        super(message, cause);
    }

    public MSDFGenException(Throwable cause) {
        super(cause);
    }
}

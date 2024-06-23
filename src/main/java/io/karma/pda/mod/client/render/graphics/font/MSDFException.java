/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics.font;

import io.karma.pda.mod.client.util.MSDFUtils;

/**
 * A general exception type used by {@link MSDFUtils} and {@link MSDFFont}.
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

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.session;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface SelectiveSessionContext<S> extends SessionContext {
    S getSelector();
}

/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public interface App {
    AppType<?> getType();

    void init();
}

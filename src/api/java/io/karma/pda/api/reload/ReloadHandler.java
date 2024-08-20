/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.reload;

import java.util.List;

/**
 * @author Alexander Hinze
 * @since 21/08/2024
 */
public interface ReloadHandler {
    void register(final Reloadable<?> reloadable);

    void unregister(final Reloadable<?> reloadable);

    List<Reloadable<?>> getObjects();
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.theme.DefaultThemes;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.sync.Sync;
import io.karma.pda.api.common.sync.Synced;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
public final class LauncherSettings {
    @Sync
    public final Synced<Theme> theme = Synced.of(DefaultThemes.DEFAULT_DARK);
    @Sync
    public final Synced<Integer> iconSize = Synced.of(48);
}

/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.AbstractApp;
import io.karma.pda.api.common.app.DefaultApps;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class LauncherApp extends AbstractApp {
    public LauncherApp() {
        super(DefaultApps.LAUNCHER);
    }

    @Override
    public void init() {

    }
}

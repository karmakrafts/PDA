/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.app.component.Container;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class LauncherApp extends AbstractApp {
    public LauncherApp() {
        super(DefaultApps.LAUNCHER);
    }

    @Override
    public void populate(final Container container) {

    }
}

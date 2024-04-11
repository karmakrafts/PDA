/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app;

import io.karma.pda.api.common.app.AbstractApp;
import io.karma.pda.api.common.app.AppContext;
import io.karma.pda.api.common.app.DefaultApps;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class SettingsApp extends AbstractApp {
    public SettingsApp() {
        super(DefaultApps.SETTINGS);
    }

    @Override
    public void init(final AppContext context) {

    }
}

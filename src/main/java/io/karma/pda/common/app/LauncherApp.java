package io.karma.pda.common.app;

import io.karma.pda.api.app.DefaultApp;
import io.karma.pda.common.init.ModApps;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class LauncherApp extends DefaultApp {
    public LauncherApp() {
        super(ModApps.launcher.get());
    }
}

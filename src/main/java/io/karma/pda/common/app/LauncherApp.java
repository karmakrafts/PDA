package io.karma.pda.common.app;

import io.karma.pda.api.common.app.AbstractApp;
import io.karma.pda.common.app.component.ContainerComponent;
import io.karma.pda.common.init.ModApps;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class LauncherApp extends AbstractApp {
    public LauncherApp() {
        super(ModApps.launcher.get());
    }

    @Override
    public void populate(final ContainerComponent container) {

    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.util.Constants;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.app.LauncherApp;
import io.karma.pda.common.app.SettingsApp;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class ModApps {
    // @formatter:off
    private ModApps() {}
    // @formatter:on

    public static void register() {
        PDAMod.LOGGER.info("Registering apps");
        register("launcher", LauncherApp::new);
        register("settings", SettingsApp::new);
    }

    private static <A extends App> void register(final String name, final Function<AppType<A>, A> factory) {
        PDAMod.APPS.register(name, () -> new AppType<>(new ResourceLocation(Constants.MODID, name), factory));
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.app.LauncherApp;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class ModApps {
    // @formatter:off
    private ModApps() {}
    // @formatter:on

    public static void register(final DeferredRegister<AppType<?>> register) {
        register.register("launcher",
            () -> new AppType<>(new ResourceLocation(Constants.MODID, "launcher"), LauncherApp::new));
    }
}

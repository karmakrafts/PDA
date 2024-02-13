package io.karma.pda.common.init;

import io.karma.pda.api.app.App;
import io.karma.pda.common.app.LauncherApp;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class ModApps {
    public static RegistryObject<App> launcher;

    // @formatter:off
    private ModApps() {}
    // @formatter:on

    public static void register(final DeferredRegister<App> register) {
        launcher = register.register("launcher", LauncherApp::new);
    }
}

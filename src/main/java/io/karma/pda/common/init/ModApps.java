package io.karma.pda.common.init;

import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.app.LauncherApp;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class ModApps {
    public static RegistryObject<AppType<LauncherApp>> launcher;

    // @formatter:off
    private ModApps() {}
    // @formatter:on

    public static void register(final DeferredRegister<AppType<?>> register) {
        launcher = register.register("launcher",
            () -> new AppType<>(new ResourceLocation(Constants.MODID, "launcher"), LauncherApp::new));
    }
}

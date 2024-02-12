package io.karma.pda.common.init;

import io.karma.pda.api.app.App;
import io.karma.pda.common.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class ModRegistries {
    public static Supplier<IForgeRegistry<App>> apps;

    // @formatter:off
    private ModRegistries() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void setup() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModRegistries::onNewRegistry);
    }

    private static void onNewRegistry(final NewRegistryEvent event) {
        apps = event.create(RegistryBuilder.of(new ResourceLocation(PDAMod.MODID, "apps")));
    }
}

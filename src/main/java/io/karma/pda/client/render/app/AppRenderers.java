package io.karma.pda.client.render.app;

import io.karma.pda.api.API;
import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class AppRenderers {
    private static final HashMap<ResourceLocation, Function<App, AppRenderer>> RENDERERS = new HashMap<>();

    // @formatter:off
    private AppRenderers() {}
    // @formatter:on

    public static void register(final ResourceLocation appName, final Function<App, AppRenderer> factory) {
        if (RENDERERS.containsKey(appName)) {
            throw new IllegalArgumentException(String.format("App renderer '%s' is already registered", appName));
        }
        RENDERERS.put(appName, factory);
    }

    public static AppRenderer create(final App app) {
        final var appName = API.getAppRegistry().getKey(app);
        if (appName == null) {
            throw new IllegalArgumentException("App type is not registered");
        }
        final var factory = RENDERERS.get(appName);
        if (factory == null) {
            throw new IllegalArgumentException(String.format("No renderer registered for app '%s'", appName));
        }
        return factory.apply(app);
    }
}

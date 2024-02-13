package io.karma.pda.api.event;

import io.karma.pda.api.API;
import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RegisterAppRenderersEvent extends Event {
    private final BiConsumer<ResourceLocation, Function<App, AppRenderer>> register;

    public RegisterAppRenderersEvent(final BiConsumer<ResourceLocation, Function<App, AppRenderer>> register) {
        this.register = register;
    }

    public void register(final ResourceLocation appName, final Function<App, AppRenderer> factory) {
        register.accept(appName, factory);
    }

    public void register(final App app, final Function<App, AppRenderer> factory) {
        register.accept(API.getAppRegistry().getKey(app), factory);
    }
}

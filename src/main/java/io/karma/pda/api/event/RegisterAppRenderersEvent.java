package io.karma.pda.api.event;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppRenderer;
import io.karma.pda.api.app.AppType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RegisterAppRenderersEvent extends Event {
    private final BiConsumer<AppType<?>, AppRenderer<?>> register;

    public RegisterAppRenderersEvent(final BiConsumer<AppType<?>, AppRenderer<?>> register) {
        this.register = register;
    }

    public <A extends App> void register(final AppType<A> type, final AppRenderer<A> renderer) {
        register.accept(type, renderer);
    }
}

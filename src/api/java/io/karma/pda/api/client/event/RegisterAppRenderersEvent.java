/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.event;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.client.render.app.AppRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RegisterAppRenderersEvent extends Event {
    private final BiConsumer<AppType<?>, AppRenderer<?>> register;

    @Internal
    public RegisterAppRenderersEvent(final BiConsumer<AppType<?>, AppRenderer<?>> register) {
        this.register = register;
    }

    public <A extends App> void register(final AppType<A> type, final AppRenderer<A> renderer) {
        register.accept(type, renderer);
    }
}

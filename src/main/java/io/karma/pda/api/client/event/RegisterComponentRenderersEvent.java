/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.event;

import io.karma.pda.api.client.render.ComponentRenderer;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RegisterComponentRenderersEvent extends Event {
    private final BiConsumer<ComponentType<?>, ComponentRenderer<?>> register;

    @ApiStatus.Internal
    public RegisterComponentRenderersEvent(final BiConsumer<ComponentType<?>, ComponentRenderer<?>> register) {
        this.register = register;
    }

    public <C extends Component> void register(final ComponentType<C> type, final ComponentRenderer<C> renderer) {
        register.accept(type, renderer);
    }
}

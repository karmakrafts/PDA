/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.event;

import io.karma.pda.api.common.state.StateHandler;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */
public final class RegisterStateReflectorsEvent extends Event {
    private final StateHandler stateHandler;

    @ApiStatus.Internal
    public RegisterStateReflectorsEvent(final StateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }

    public StateHandler getStateHandler() {
        return stateHandler;
    }
}

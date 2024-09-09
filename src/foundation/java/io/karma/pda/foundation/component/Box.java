/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import io.karma.peregrine.api.color.Color;
import io.karma.peregrine.api.color.ColorProvider;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
public class Box extends DefaultContainer {
    @Synchronize
    public final MutableState<ColorProvider> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<ColorProvider> foreground = MutableState.of(Color.NONE);

    public Box(ComponentType<?> type, UUID id) {
        super(type, id);
    }
}

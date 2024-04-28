/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.color.Gradient;
import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
public class Panel extends DefaultContainer {
    @Synchronize
    public final MutableState<Gradient> background = MutableState.of(Gradient.solid(Color.NONE));
    @Synchronize
    public final MutableState<Gradient> foreground = MutableState.of(Gradient.solid(Color.NONE));

    public Panel(ComponentType<?> type, UUID id) {
        super(type, id);
    }
}

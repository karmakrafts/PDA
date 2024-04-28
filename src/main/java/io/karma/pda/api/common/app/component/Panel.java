/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;
import io.karma.pda.api.common.util.Color;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
public class Panel extends DefaultContainer {
    @Synchronize
    public final MutableState<Color> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<Color> foreground = MutableState.of(Color.NONE);

    public Panel(ComponentType<?> type, UUID id) {
        super(type, id);
    }
}

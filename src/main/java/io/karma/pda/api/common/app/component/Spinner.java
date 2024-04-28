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
 * @since 20/04/2024
 */
public class Spinner extends AbstractComponent {
    @Synchronize
    public final MutableState<Gradient> color = MutableState.of(Gradient.solid(Color.WHITE));

    public Spinner(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

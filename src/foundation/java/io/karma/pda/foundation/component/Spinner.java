/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import io.karma.peregrine.api.color.Color;
import io.karma.peregrine.api.color.ColorProvider;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 20/04/2024
 */
public class Spinner extends AbstractComponent {
    @Synchronize
    public final MutableState<ColorProvider> color = MutableState.of(Color.WHITE);

    public Spinner(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

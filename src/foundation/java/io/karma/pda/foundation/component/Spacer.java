/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.color.Color;
import io.karma.pda.api.color.ColorProvider;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class Spacer extends AbstractComponent {
    @Synchronize
    public final MutableState<ColorProvider> color = MutableState.of(Color.WHITE);
    @Synchronize
    public final MutableState<Integer> width = MutableState.of(1);
    @Synchronize
    public final MutableState<Orientation> orientation = MutableState.of(Orientation.HORIZONTAL);

    public Spacer(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
}

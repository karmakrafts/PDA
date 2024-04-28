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
 * @since 13/04/2024
 */
public class Separator extends AbstractComponent {
    @Synchronize
    public final MutableState<Color> color = MutableState.of(Color.WHITE);
    @Synchronize
    public final MutableState<Integer> width = MutableState.of(1);
    @Synchronize
    public final MutableState<Orientation> orientation = MutableState.of(Orientation.HORIZONTAL);

    public Separator(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
}

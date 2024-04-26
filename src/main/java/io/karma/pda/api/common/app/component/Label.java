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
 * @since 08/02/2024
 */
public class Label extends AbstractComponent {
    @Synchronize
    public final MutableState<String> text = MutableState.of("");
    @Synchronize
    public final MutableState<Color> color = MutableState.of(Color.WHITE);

    public Label(final ComponentType<?> type, final UUID uuid) {
        super(type, uuid);
    }
}

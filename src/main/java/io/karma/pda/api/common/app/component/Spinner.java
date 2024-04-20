/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.sync.Sync;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.util.Color;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 20/04/2024
 */
public class Spinner extends AbstractComponent {
    @Sync
    public final Synced<Color> color = Synced.of(Color.WHITE);

    public Spinner(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

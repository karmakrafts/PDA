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
 * @since 23/04/2024
 */
public class Panel extends DefaultContainer {
    @Sync
    public final Synced<Color> background = Synced.of(Color.WHITE);
    @Sync
    public final Synced<Color> foreground = Synced.of(Color.NONE);

    public Panel(ComponentType<?> type, UUID id) {
        super(type, id);
    }
}

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
 * @since 08/02/2024
 */
public class Label extends AbstractComponent {
    private static final String TAG_TEXT = "text";
    private static final String TAG_COLOR = "color";
    private static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    @Sync
    public final Synced<String> text = Synced.withInitial("");
    @Sync
    public final Synced<Color> color = Synced.withInitial(Color.WHITE);

    public Label(final ComponentType<?> type, final UUID uuid) {
        super(type, uuid);
    }
}

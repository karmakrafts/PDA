/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.sync.Synced;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class PlayerImage extends AbstractComponent {
    public final Synced<UUID> player = Synced.ofType(UUID.class);

    public PlayerImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

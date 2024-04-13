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
public class Button extends AbstractComponent {
    public final Synced<String> text = Synced.withInitial("");

    public Button(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

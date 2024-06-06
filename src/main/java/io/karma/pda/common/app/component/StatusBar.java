/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app.component;

import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.foundation.component.DefaultContainer;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 27/05/2024
 */
public final class StatusBar extends DefaultContainer {
    public StatusBar(final ComponentType<StatusBar> type, final UUID id) {
        super(type, id);
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class Image extends AbstractComponent {
    public Image(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

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
    private final Synced<String> text = Synced.withInitial("");
    private final Synced<Color> color = Synced.withInitial(Color.WHITE);

    public Label(final UUID uuid) {
        super(DefaultComponents.LABEL, uuid);
    }

    public Synced<String> getText() {
        return text;
    }

    public Synced<Color> getColor() {
        return color;
    }
}

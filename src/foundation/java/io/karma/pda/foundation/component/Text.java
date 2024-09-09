/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.app.theme.DefaultFontFamilies;
import io.karma.pda.api.flex.FlexNodeType;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import io.karma.peregrine.api.color.Color;
import io.karma.peregrine.api.color.ColorProvider;
import io.karma.peregrine.api.font.Font;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public class Text extends AbstractComponent {
    @Synchronize
    public final MutableState<Font> font = MutableState.of(DefaultFontFamilies.NOTO_SANS.getDefaultFont());
    @Synchronize
    public final MutableState<String> text = MutableState.of("");
    @Synchronize
    public final MutableState<ColorProvider> color = MutableState.of(Color.WHITE);
    @Synchronize
    public final MutableState<Boolean> wrapping = MutableState.of(true);

    public Text(final ComponentType<?> type, final UUID uuid) {
        super(type, uuid);
        flexNode.setType(FlexNodeType.TEXT);
    }
}

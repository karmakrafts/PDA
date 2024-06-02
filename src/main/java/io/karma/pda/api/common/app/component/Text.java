/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.app.theme.font.DefaultFontFamilies;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.color.ColorProvider;
import io.karma.pda.api.common.flex.FlexNodeType;
import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;

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

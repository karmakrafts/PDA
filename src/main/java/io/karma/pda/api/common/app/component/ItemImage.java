/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.color.Color;
import io.karma.pda.api.common.color.ColorProvider;
import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class ItemImage extends AbstractComponent {
    @Synchronize
    public final MutableState<ResourceLocation> item = MutableState.of(new ResourceLocation("apple"));
    @Synchronize
    public final MutableState<ColorProvider> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<ColorProvider> foreground = MutableState.of(Color.NONE);

    public ItemImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

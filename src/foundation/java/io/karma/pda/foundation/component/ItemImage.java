/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.color.Color;
import io.karma.pda.api.color.ColorProvider;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class ItemImage extends AbstractComponent {
    @Synchronize
    public final MutableState<Item> item = MutableState.of(Items.APPLE);
    @Synchronize
    public final MutableState<ColorProvider> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<ColorProvider> foreground = MutableState.of(Color.NONE);

    public ItemImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

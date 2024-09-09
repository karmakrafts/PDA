/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import io.karma.peregrine.api.color.Color;
import io.karma.peregrine.api.color.ColorProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class RecipeImage extends AbstractComponent {
    @Synchronize
    public final MutableState<ResourceLocation> recipe = MutableState.of(new ResourceLocation("bucket"));
    @Synchronize
    public final MutableState<ColorProvider> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<ColorProvider> foreground = MutableState.of(Color.NONE);

    public RecipeImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

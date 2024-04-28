/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;
import io.karma.pda.api.common.util.Color;
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
    public final MutableState<Color> background = MutableState.of(Color.NONE);
    @Synchronize
    public final MutableState<Color> foreground = MutableState.of(Color.NONE);

    public RecipeImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

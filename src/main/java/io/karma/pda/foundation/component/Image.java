/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class Image extends AbstractComponent {
    @Synchronize
    public final MutableState<ResourceLocation> image = MutableState.of(new ResourceLocation("pack.png"));

    public Image(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

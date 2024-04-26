/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.state.MutableState;
import io.karma.pda.api.common.state.Synchronize;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class BlockImage extends AbstractComponent {
    @Synchronize
    public final MutableState<ResourceLocation> block = MutableState.of(new ResourceLocation("dirt"));

    public BlockImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

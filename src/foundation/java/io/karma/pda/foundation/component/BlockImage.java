/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.component;

import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.state.MutableState;
import io.karma.pda.api.state.Synchronize;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
public class BlockImage extends AbstractComponent {
    @Synchronize
    public final MutableState<Block> block = MutableState.of(Blocks.DIRT);

    public BlockImage(final ComponentType<?> type, final UUID id) {
        super(type, id);
    }
}

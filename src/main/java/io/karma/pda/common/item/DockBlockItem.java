/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class DockBlockItem extends BlockItem {
    public DockBlockItem(final Block block) {
        super(block, new Properties());
    }
}

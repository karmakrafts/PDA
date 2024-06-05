/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.block.DockBlock;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class ModBlocks {
    public static RegistryObject<DockBlock> dock;

    // @formatter:off
    private ModBlocks() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register() {
        PDAMod.LOGGER.info("Registering blocks");
        dock = PDAMod.BLOCKS.register("dock", DockBlock::new);
    }
}

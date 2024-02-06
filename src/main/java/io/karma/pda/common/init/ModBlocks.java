package io.karma.pda.common.init;

import io.karma.pda.common.block.DockBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class ModBlocks {
    public static RegistryObject<DockBlock> dock;

    // @formatter:off
    private ModBlocks() {}
    // @formatter:on

    public static void register(final DeferredRegister<Block> register) {
        dock = register.register("dock", DockBlock::new);
    }
}

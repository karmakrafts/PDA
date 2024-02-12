package io.karma.pda.common.init;

import com.google.common.collect.Sets;
import io.karma.pda.common.entity.DockBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class ModBlockEntities {
    public static RegistryObject<BlockEntityType<DockBlockEntity>> dock;

    // @formatter:off
    private ModBlockEntities() {}
    // @formatter:on

    public static void register(final DeferredRegister<BlockEntityType<?>> register) {
        dock = register.register("dock",
            () -> new BlockEntityType<>(DockBlockEntity::new, Sets.newHashSet(ModBlocks.dock.get()), null));
    }
}

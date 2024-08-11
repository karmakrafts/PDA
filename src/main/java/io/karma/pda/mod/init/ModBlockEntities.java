/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.init;

import com.google.common.collect.Sets;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.entity.DockBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public final class ModBlockEntities {
    public static RegistryObject<BlockEntityType<DockBlockEntity>> dock;

    // @formatter:off
    private ModBlockEntities() {}
    // @formatter:on

    @SuppressWarnings("all")
    @Internal
    public static void register() {
        PDAMod.LOGGER.info("Registering block entity types");
        dock = PDAMod.BLOCK_ENTITIES.register("dock",
            () -> new BlockEntityType<>(DockBlockEntity::new, Sets.newHashSet(ModBlocks.dock.get()), null));
    }
}

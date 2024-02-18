/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.init;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.item.DockBlockItem;
import io.karma.pda.common.item.MemoryCardItem;
import io.karma.pda.common.item.PDAItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class ModItems {
    public static RegistryObject<PDAItem> pda;
    public static RegistryObject<MemoryCardItem> memoryCard;

    // @formatter:off
    private ModItems() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register(final DeferredRegister<Item> register) {
        PDAMod.LOGGER.info("Registering items");
        pda = register.register("pda", PDAItem::new);
        memoryCard = register.register("memory_card", MemoryCardItem::new);
        register.register("dock", () -> new DockBlockItem(ModBlocks.dock.get()));
    }
}

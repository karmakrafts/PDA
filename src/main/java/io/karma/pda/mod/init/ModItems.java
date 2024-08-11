/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.init;

import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.item.DockBlockItem;
import io.karma.pda.mod.item.MemoryCardItem;
import io.karma.pda.mod.item.PDAItem;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus.Internal;

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

    @Internal
    public static void register() {
        PDAMod.LOGGER.info("Registering items");
        pda = PDAMod.ITEMS.register("pda", PDAItem::new);
        memoryCard = PDAMod.ITEMS.register("memory_card", MemoryCardItem::new);
        PDAMod.ITEMS.register("dock", () -> new DockBlockItem(ModBlocks.dock.get()));
    }
}

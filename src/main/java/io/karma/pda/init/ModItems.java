package io.karma.pda.init;

import io.karma.pda.item.DockBlockItem;
import io.karma.pda.item.MemoryCardItem;
import io.karma.pda.item.PDAItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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

    public static void register(final DeferredRegister<Item> register) {
        pda = register.register("pda", PDAItem::new);
        memoryCard = register.register("memory_card", MemoryCardItem::new);
        register.register("dock", () -> new DockBlockItem(ModBlocks.dock.get()));
    }
}

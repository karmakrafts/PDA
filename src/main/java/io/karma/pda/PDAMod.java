package io.karma.pda;

import io.karma.pda.init.ModBlocks;
import io.karma.pda.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
@Mod(PDAMod.MODID)
public class PDAMod {
    public static final String MODID = "pda";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // @formatter:off
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable(String.format("itemGroup.%s", MODID)))
        .icon(ModItems.pda.get()::getDefaultInstance)
        .displayItems((params, output) -> {
            ITEMS.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
        })
        .build());
    // @formatter:on

    static {
        ModBlocks.register(BLOCKS);
        ModItems.register(ITEMS);
    }

    public PDAMod() {
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
    }
}

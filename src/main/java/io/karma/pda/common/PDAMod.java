package io.karma.pda.common;

import io.karma.pda.client.screen.PDAStorageScreen;
import io.karma.pda.common.init.ModBlockEntities;
import io.karma.pda.common.init.ModBlocks;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.init.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
        MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
        MODID);
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
        ModBlockEntities.register(BLOCK_ENTITIES);
        ModBlocks.register(BLOCKS);
        ModItems.register(ITEMS);
        ModMenus.register(MENU_TYPES);
    }

    public PDAMod() {
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCK_ENTITIES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
        MENU_TYPES.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::onClientSetup);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.pdaStorageMenu.get(), PDAStorageScreen::new);
        });
    }
}

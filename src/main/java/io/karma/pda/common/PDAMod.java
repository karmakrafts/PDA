package io.karma.pda.common;

import io.karma.pda.api.API;
import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppRenderer;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.event.RegisterAppRenderersEvent;
import io.karma.pda.api.util.Constants;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.render.app.AppRenderers;
import io.karma.pda.client.render.app.LauncherAppRenderer;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.client.render.item.PDAItemRenderer;
import io.karma.pda.client.screen.DockScreen;
import io.karma.pda.client.screen.PDAStorageScreen;
import io.karma.pda.common.init.*;
import io.karma.pda.common.menu.DockMenu;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.network.CommonPacketHandler;
import io.karma.pda.common.util.Disposable;
import io.karma.pda.common.util.DispositionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
@Mod(Constants.MODID)
public class PDAMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final DispositionHandler DISPOSITION_HANDLER = new DispositionHandler(PDAMod::handleDisposition);
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MODID,
            "play"),
        () -> Constants.PROTOCOL_VERSION,
        Constants.PROTOCOL_VERSION::equals,
        Constants.PROTOCOL_VERSION::equals);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
        Constants.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
        Constants.MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
        Constants.MODID);
    // @formatter:off
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable(String.format("itemGroup.%s", Constants.MODID)))
        .icon(ModItems.pda.get()::getDefaultInstance)
        .displayItems((params, output) -> {
            ITEMS.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
        })
        .build());
    private static final DeferredRegister<AppType<?>> APPS = API.makeDeferredAppTypeRegister(Constants.MODID);
    // @formatter:on

    static {
        ModBlockEntities.register(BLOCK_ENTITIES);
        ModBlocks.register(BLOCKS);
        ModItems.register(ITEMS);
        ModMenus.register(MENU_TYPES);
        ModApps.register(APPS);
    }

    public PDAMod() {
        CommonEventHandler.INSTANCE.setup();
        CommonPacketHandler.setup(CHANNEL);

        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK_ENTITIES.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);
        MENU_TYPES.register(modBus);
        APPS.register(modBus);

        MinecraftForge.EVENT_BUS.addListener(this::onGameShutdown);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientEventHandler.INSTANCE.setup();
            PDAItemRenderer.INSTANCE.setup();
            modBus.addListener(this::onClientSetup);
            DisplayRenderer.INSTANCE.setupEarly();
        });
    }

    private static void handleDisposition(final Disposable object) {
        DistExecutor.unsafeRunForDist(() -> () -> {
            Minecraft.getInstance().tell(object::dispose);
            return null;
        }, () -> () -> {
            object.dispose();
            return null;
        });
    }

    private void onGameShutdown(final GameShuttingDownEvent event) {
        DISPOSITION_HANDLER.disposeAll();
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering screens");
            DisplayRenderer.INSTANCE.setup();
            MenuScreens.register(ModMenus.pdaStorage.get(),
                (PDAStorageMenu menu, Inventory inventory, Component title) -> new PDAStorageScreen(menu, inventory));
            MenuScreens.register(ModMenus.dock.get(),
                (DockMenu menu, Inventory inventory, Component title) -> new DockScreen(menu, inventory));

            LOGGER.info("Registering app renderers");
            AppRenderers.register(ModApps.launcher.get(), new LauncherAppRenderer());
            // @formatter:off
            MinecraftForge.EVENT_BUS.post(new RegisterAppRenderersEvent(
                (type, renderer) -> AppRenderers.register((AppType<App>) type, (AppRenderer<App>) renderer)));
            // @formatter:on
        });
    }
}

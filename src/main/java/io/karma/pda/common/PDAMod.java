/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.blaze3d.systems.RenderSystem;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.theme.Theme;
import io.karma.pda.api.common.app.theme.font.FontFamily;
import io.karma.pda.api.common.color.GradientFunction;
import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.api.common.dispose.DispositionHandler;
import io.karma.pda.api.common.state.StateReflector;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.api.common.util.RegistryUtils;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.flex.ClientFlexNodeHandler;
import io.karma.pda.client.interaction.DockInteractionHandler;
import io.karma.pda.client.interaction.PDAInteractionHandler;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.client.render.graphics.GraphicsRenderTypes;
import io.karma.pda.client.render.graphics.font.DefaultFontRenderer;
import io.karma.pda.client.render.item.PDAItemRenderer;
import io.karma.pda.client.screen.DockStorageScreen;
import io.karma.pda.client.screen.PDAStorageScreen;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.init.*;
import io.karma.pda.common.json.JSONCodecs;
import io.karma.pda.common.menu.DockStorageMenu;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.network.ClientPacketHandler;
import io.karma.pda.common.network.CommonPacketHandler;
import io.karma.pda.common.session.DefaultSessionHandler;
import io.karma.pda.common.util.TabItemProvider;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
@Mod(Constants.MODID)
public class PDAMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newWorkStealingPool();
    public static final DispositionHandler DISPOSITION_HANDLER = new DispositionHandler(PDAMod::handleDisposition);

    // @formatter:off
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MODID,
            "play"),
        () -> Constants.PROTOCOL_VERSION,
        Constants.PROTOCOL_VERSION::equals,
        Constants.PROTOCOL_VERSION::equals);
    // @formatter:on

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
        Constants.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
        Constants.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
        Constants.MODID);
    // @formatter:off
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable(String.format("itemGroup.%s", Constants.MODID)))
        .icon(ModItems.pda.get()::getDefaultInstance)
        .displayItems((params, output) -> {
            ITEMS.getEntries().stream().map(RegistryObject::get).forEach(item -> {
                final var block = Block.byItem(item);
                if(block != Blocks.AIR && block instanceof TabItemProvider provider) {
                    final var items = NonNullList.<ItemStack>create();
                    provider.addToTab(items);
                    output.acceptAll(items);
                    return;
                }
                if(item instanceof TabItemProvider provider) {
                    final var items = NonNullList.<ItemStack>create();
                    provider.addToTab(items);
                    output.acceptAll(items);
                    return;
                }
                output.accept(item); // Otherwise just add the default item
            });
        })
        .build());
    public static final DeferredRegister<ComponentType<?>> COMPONENTS = API.makeDeferredComponentTypeRegister(Constants.MODID);
    public static final DeferredRegister<AppType<?>> APPS = API.makeDeferredAppTypeRegister(Constants.MODID);
    public static final DeferredRegister<Theme> THEMES = API.makeThemeRegister(Constants.MODID);
    public static final DeferredRegister<FontFamily> FONT_FAMILIES = API.makeFontFamilyRegister(Constants.MODID);
    public static final DeferredRegister<GradientFunction> GRADIENT_FUNCTIONS = API.makeGradientFunctionRegister(Constants.MODID);
    // @formatter:on

    public static final ServiceLoader<StateReflector> STATE_REFLECTORS = ServiceLoader.load(StateReflector.class);
    public static boolean IS_DEV_ENV;

    static {
        try {
            Class.forName("net.minecraft.world.level.Level");
            IS_DEV_ENV = true;
            LOGGER.info("Detected development environment, enabling debug mode");
        }
        catch (Throwable error) { /* IGNORE */ }

        ModBlockEntities.register(BLOCK_ENTITIES);
        ModBlocks.register(BLOCKS);
        ModItems.register(ITEMS);
        ModMenus.register(MENU_TYPES);
        ModComponents.register();
        ModApps.register();
        ModThemes.register();
        ModFontFamilies.register();
        ModGradientFunctions.register();
    }

    public PDAMod() {
        CommonEventHandler.INSTANCE.setup();
        DefaultSessionHandler.INSTANCE.setup();

        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onCommonSetup);

        BLOCK_ENTITIES.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);
        MENU_TYPES.register(modBus);
        COMPONENTS.register(modBus);
        APPS.register(modBus);
        THEMES.register(modBus);
        FONT_FAMILIES.register(modBus);
        GRADIENT_FUNCTIONS.register(modBus);

        MinecraftForge.EVENT_BUS.addListener(this::onGameShutdown);
        initAPI();
        JSONCodecs.register();
        STATE_REFLECTORS.forEach(StateReflector::init);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            onClientEarlySetup();
            modBus.addListener(this::onClientSetup);
        });
    }

    private static void handleDisposition(final Disposable object) {
        DistExecutor.unsafeRunForDist(() -> () -> {
            // Make sure client-side resources always get disposed on the main thread to allow GL calls
            RenderSystem.recordRenderCall(() -> {
                LOGGER.info("Disposing resource {}", object);
                object.dispose();
            });
            return null;
        }, () -> () -> {
            LOGGER.info("Disposing resource {}", object);
            object.dispose();
            return null;
        });
    }

    private void onGameShutdown(final GameShuttingDownEvent event) {
        DISPOSITION_HANDLER.disposeAll();
        try {
            EXECUTOR_SERVICE.shutdown();
            if (!EXECUTOR_SERVICE.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                EXECUTOR_SERVICE.shutdownNow().forEach(Runnable::run); // Call all remaining tasks immediatly
            }
        }
        catch (Throwable error) {
            LOGGER.error("Could not shutdown executor service: {}", error.getMessage());
        }
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CommonPacketHandler.INSTANCE.registerPackets();
            ClientPacketHandler.INSTANCE.registerPackets();
        });
    }

    private void initAPI() {
        API.setLogger(LOGGER);
        API.setObjectMapper(new ObjectMapper());
        API.setExecutorService(EXECUTOR_SERVICE);
        API.setSessionHandler(DefaultSessionHandler.INSTANCE);
        API.setComponentTypeRegistry(() -> RegistryUtils.getRegistry(Constants.COMPONENT_REGISTRY_NAME));
        API.setAppTypeRegistry(() -> RegistryUtils.getRegistry(Constants.APP_REGISTRY_NAME));
        API.setThemeRegistry(() -> RegistryUtils.getRegistry(Constants.THEME_REGISTRY_NAME));
        API.setFontFamilyRegistry(() -> RegistryUtils.getRegistry(Constants.FONT_FAMILY_REGISTRY_NAME));
        API.setGradientFunctionRegistry(() -> RegistryUtils.getRegistry(Constants.GRADIENT_FUNCTION_REGISTRY_NAME));
        API.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientEarlySetup() {
        ClientEventHandler.INSTANCE.setup();
        DockInteractionHandler.INSTANCE.setup();
        PDAInteractionHandler.INSTANCE.setup();
        PDAItemRenderer.INSTANCE.setup();
        DisplayRenderer.getInstance().setupEarly();
        DefaultFontRenderer.INSTANCE.setupEarly();
        GraphicsRenderTypes.INSTANCE.setupEarly();
        initClientAPI();
    }

    @OnlyIn(Dist.CLIENT)
    private void initClientAPI() {
        ClientAPI.setSessionHandler(ClientSessionHandler.INSTANCE);
        ClientAPI.setFlexNodeHandler(ClientFlexNodeHandler.INSTANCE);
        ClientAPI.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering screens");
            DisplayRenderer.getInstance().setup();
            DefaultFontRenderer.INSTANCE.setup();
            MenuScreens.register(ModMenus.pdaStorage.get(),
                (PDAStorageMenu menu, Inventory inventory, Component title) -> new PDAStorageScreen(menu, inventory));
            MenuScreens.register(ModMenus.dockStorage.get(),
                (DockStorageMenu menu, Inventory inventory, Component title) -> new DockStorageScreen(menu, inventory));
            ClientEventHandler.INSTANCE.fireRegisterEvents();
        });
    }
}

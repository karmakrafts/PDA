/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.blaze3d.systems.RenderSystem;
import io.karma.pda.api.API;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.app.theme.Theme;
import io.karma.pda.api.app.theme.font.FontFamily;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.color.GradientFunction;
import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.state.StateReflector;
import io.karma.pda.api.util.Constants;
import io.karma.pda.api.util.RegistryUtils;
import io.karma.pda.foundation.client.render.ComponentRenderTypes;
import io.karma.pda.mod.client.ClientEventHandler;
import io.karma.pda.mod.client.flex.ClientFlexNodeHandler;
import io.karma.pda.mod.client.interaction.DockInteractionHandler;
import io.karma.pda.mod.client.interaction.PDAInteractionHandler;
import io.karma.pda.mod.client.render.display.DefaultDisplayRenderer;
import io.karma.pda.mod.client.render.graphics.GraphicsRenderTypes;
import io.karma.pda.mod.client.render.graphics.font.DefaultFontRenderer;
import io.karma.pda.mod.client.render.item.DockItemRenderer;
import io.karma.pda.mod.client.render.item.PDAItemRenderer;
import io.karma.pda.mod.client.render.shader.DefaultShaderHandler;
import io.karma.pda.mod.client.session.ClientSessionHandler;
import io.karma.pda.mod.dispose.DefaultDispositionHandler;
import io.karma.pda.mod.init.*;
import io.karma.pda.mod.json.JSONCodecs;
import io.karma.pda.mod.network.ClientPacketHandler;
import io.karma.pda.mod.network.CommonPacketHandler;
import io.karma.pda.mod.reload.DefaultReloadHandler;
import io.karma.pda.mod.session.DefaultSessionHandler;
import io.karma.pda.mod.util.TabItemProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.fml.loading.FMLLoader;
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
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static final DefaultDispositionHandler DISPOSITION_HANDLER = new DefaultDispositionHandler(disposable -> DistExecutor.unsafeRunForDist(
        () -> () -> {
            // Make sure client-side resources always get disposed on the main thread to allow GL calls
            RenderSystem.recordRenderCall(() -> {
                LOGGER.info("Disposing resource {}", disposable);
                disposable.dispose();
            });
            return null;
        },
        () -> () -> {
            LOGGER.info("Disposing resource {}", disposable);
            disposable.dispose();
            return null;
        }));

    // @formatter:off
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MODID, "play"),
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
    public static final DeferredRegister<DisplayModeSpec> DISPLAY_MODES = API.makeDisplayModeRegister(Constants.MODID);
    // @formatter:on

    public static final ServiceLoader<StateReflector> STATE_REFLECTORS = ServiceLoader.load(StateReflector.class);
    private static boolean isDevEnvironment;
    private static final boolean isOculusInstalled;
    private static final boolean isEmbeddiumInstalled;

    static {
        try {
            Class.forName("net.minecraft.world.level.Level");
            isDevEnvironment = true;
            LOGGER.info("Detected development environment, enabling debug mode");
        }
        catch (Throwable error) { /* IGNORE */ }

        isOculusInstalled = FMLLoader.getLoadingModList().getModFileById("oculus") != null;
        if (isOculusInstalled) {
            LOGGER.info("Detected Oculus, enabling compatibility");
        }

        isEmbeddiumInstalled = FMLLoader.getLoadingModList().getModFileById("embeddium") != null;
        if (isEmbeddiumInstalled) {
            LOGGER.info("Detected Embeddium, enabling compatibility");
        }

        ModBlockEntities.register();
        ModBlocks.register();
        ModItems.register();
        ModMenus.register();
        ModComponents.register();
        ModApps.register();
        ModThemes.register();
        ModFontFamilies.register();
        ModGradientFunctions.register();
        ModDisplayModes.register();
    }

    public PDAMod() {
        CommonEventHandler.INSTANCE.setup();
        CommandHandler.INSTANCE.setup();
        DefaultSessionHandler.INSTANCE.setup();
        DefaultReloadHandler.INSTANCE.setup();

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
        DISPLAY_MODES.register(modBus);

        MinecraftForge.EVENT_BUS.addListener(this::onGameShutdown);
        initAPI();
        JSONCodecs.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            onClientEarlySetup();
            modBus.addListener(this::onClientSetup);
        });
    }

    public static boolean isDevEnvironment() {
        return isDevEnvironment;
    }

    public static boolean isOculusInstalled() {
        return isOculusInstalled;
    }

    public static boolean isEmbeddiumInstalled() {
        return isEmbeddiumInstalled;
    }

    private void onGameShutdown(final GameShuttingDownEvent event) {
        DISPOSITION_HANDLER.disposeAll();
        try {
            EXECUTOR_SERVICE.shutdown();
            if (EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow().forEach(Runnable::run);
            }
        }
        catch (Throwable error) {
            LOGGER.error("Could not shutdown executor service", error);
        }
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Starting common setup");
            STATE_REFLECTORS.forEach(StateReflector::init);
            CommonPacketHandler.INSTANCE.registerPackets();
            ClientPacketHandler.INSTANCE.registerPackets();
        });
    }

    private void initAPI() {
        LOGGER.info("Initializing API, PING!");
        API.setLogger(LogManager.getLogger("PDA API"));
        API.setObjectMapper(new ObjectMapper());
        API.setExecutorService(EXECUTOR_SERVICE);
        API.setSessionHandler(DefaultSessionHandler.INSTANCE);
        API.setComponentTypeRegistry(() -> RegistryUtils.getRegistry(Constants.COMPONENT_REGISTRY_NAME));
        API.setAppTypeRegistry(() -> RegistryUtils.getRegistry(Constants.APP_REGISTRY_NAME));
        API.setThemeRegistry(() -> RegistryUtils.getRegistry(Constants.THEME_REGISTRY_NAME));
        API.setFontFamilyRegistry(() -> RegistryUtils.getRegistry(Constants.FONT_FAMILY_REGISTRY_NAME));
        API.setGradientFunctionRegistry(() -> RegistryUtils.getRegistry(Constants.GRADIENT_FUNCTION_REGISTRY_NAME));
        API.setDisplayModeRegistry(() -> RegistryUtils.getRegistry(Constants.DISPLAY_MODE_REGISTRY_NAME));
        API.setReloadHandler(DefaultReloadHandler.INSTANCE);
        API.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientEarlySetup() {
        ClientEventHandler.INSTANCE.setup();
        DockInteractionHandler.INSTANCE.setup();
        PDAInteractionHandler.INSTANCE.setup();
        PDAItemRenderer.INSTANCE.setup();
        DockItemRenderer.INSTANCE.setup();
        DefaultShaderHandler.INSTANCE.setup();

        initClientAPI();
    }

    @OnlyIn(Dist.CLIENT)
    private void initClientAPI() {
        LOGGER.info("Initializing client API, PING!");
        ClientAPI.setSessionHandler(ClientSessionHandler.INSTANCE);
        ClientAPI.setFlexNodeHandler(ClientFlexNodeHandler.INSTANCE);
        ClientAPI.setDisplayRenderer(DefaultDisplayRenderer.INSTANCE);
        ClientAPI.setShaderHandler(DefaultShaderHandler.INSTANCE);
        ClientAPI.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Starting client setup");
            GraphicsRenderTypes.createShaders();
            ComponentRenderTypes.createShaders();
            DefaultFontRenderer.createShaders();
            ModScreens.register();
        });
    }
}

package io.karma.pda.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.karma.pda.api.API;
import io.karma.pda.api.app.App;
import io.karma.pda.api.util.Constants;
import io.karma.pda.client.ClientEventHandler;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.client.render.entity.DockBlockEntityRenderer;
import io.karma.pda.client.render.item.PDAItemRenderer;
import io.karma.pda.client.screen.DockScreen;
import io.karma.pda.client.screen.PDAStorageScreen;
import io.karma.pda.common.init.ModBlockEntities;
import io.karma.pda.common.init.ModBlocks;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.init.ModMenus;
import io.karma.pda.common.item.MemoryCardItem;
import io.karma.pda.common.menu.DockMenu;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.network.CommonPacketHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
@Mod(Constants.MODID)
public class PDAMod {
    public static final Logger LOGGER = LogManager.getLogger();
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
    private static final DeferredRegister<App> APPS = API.makeDeferredAppRegister(Constants.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable(String.format("itemGroup.%s", Constants.MODID)))
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
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onNewRegistry);
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRegisterCommands);

        CommonEventHandler.INSTANCE.setup();
        CommonPacketHandler.setup();

        BLOCK_ENTITIES.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);
        MENU_TYPES.register(modBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientEventHandler.INSTANCE.setup();
            PDAItemRenderer.INSTANCE.setup();
            modBus.addListener(this::onClientSetup);
            modBus.addListener(this::onRegisterEntityRenderers);
            DisplayRenderer.INSTANCE.setupEarly();
        });
    }

    private void onNewRegistry(final NewRegistryEvent event) {
        event.create(RegistryBuilder.of(new ResourceLocation(Constants.MODID, "apps")));
    }

    private void onRegisterCommands(final RegisterCommandsEvent event) {
        LOGGER.info("Registering commands");
        // @formatter:off
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal(Constants.MODID)
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("card")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("lock")
                    .executes(stack -> {
                        final var entity = stack.getSource().getEntity();
                        if (!(entity instanceof ServerPlayer serverPlayer)) {
                            return Command.SINGLE_SUCCESS;
                        }
                        final var inventory = serverPlayer.getInventory();
                        final var heldStack = inventory.getItem(inventory.selected);
                        if (heldStack.isEmpty() || heldStack.getItem() != ModItems.memoryCard.get()) {
                            serverPlayer.sendSystemMessage(Component.literal("No memory card in main hand"));
                            return Command.SINGLE_SUCCESS;
                        }
                        final var tag = heldStack.getOrCreateTag();
                        tag.putUUID(MemoryCardItem.TAG_OWNER_ID, serverPlayer.getUUID());
                        tag.putString(MemoryCardItem.TAG_OWNER_DISPLAY_NAME, serverPlayer.getName().getString());
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("lockWithName")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("ownerName", StringArgumentType.string())
                        .executes(stack -> {
                            final var entity = stack.getSource().getEntity();
                            if (!(entity instanceof ServerPlayer serverPlayer)) {
                                return Command.SINGLE_SUCCESS;
                            }
                            final var inventory = serverPlayer.getInventory();
                            final var heldStack = inventory.getItem(inventory.selected);
                            if (heldStack.isEmpty() || heldStack.getItem() != ModItems.memoryCard.get()) {
                                serverPlayer.sendSystemMessage(Component.literal("No memory card in main hand"));
                                return Command.SINGLE_SUCCESS;
                            }
                            final var tag = heldStack.getOrCreateTag();
                            tag.putUUID(MemoryCardItem.TAG_OWNER_ID, serverPlayer.getUUID());
                            tag.putString(MemoryCardItem.TAG_OWNER_DISPLAY_NAME, stack.getArgument("ownerName", String.class));
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("unlock")
                    .executes(stack -> {
                        final var entity = stack.getSource().getEntity();
                        if (!(entity instanceof ServerPlayer serverPlayer)) {
                            return Command.SINGLE_SUCCESS;
                        }
                        final var inventory = serverPlayer.getInventory();
                        final var heldStack = inventory.getItem(inventory.selected);
                        if (heldStack.isEmpty() || heldStack.getItem() != ModItems.memoryCard.get()) {
                            serverPlayer.sendSystemMessage(Component.literal("No memory card in main hand"));
                            return Command.SINGLE_SUCCESS;
                        }
                        final var tag = heldStack.getTag();
                        if(tag == null) {
                            return Command.SINGLE_SUCCESS; // Just return and ignore
                        }
                        tag.remove(MemoryCardItem.TAG_OWNER_ID);
                        tag.remove(MemoryCardItem.TAG_OWNER_DISPLAY_NAME);
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
        );
        // @formatter:on
    }

    @OnlyIn(Dist.CLIENT)
    private void onRegisterEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        LOGGER.info("Registering block entity renderers");
        event.registerBlockEntityRenderer(ModBlockEntities.dock.get(), DockBlockEntityRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering screens");
            DisplayRenderer.INSTANCE.setup();
            MenuScreens.register(ModMenus.pdaStorage.get(),
                (PDAStorageMenu menu, Inventory inventory, Component title) -> new PDAStorageScreen(menu, inventory));
            MenuScreens.register(ModMenus.dock.get(),
                (DockMenu menu, Inventory inventory, Component title) -> new DockScreen(menu, inventory));
        });
    }
}

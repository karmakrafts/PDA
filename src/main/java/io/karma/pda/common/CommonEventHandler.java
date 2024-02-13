package io.karma.pda.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.karma.pda.api.util.Constants;
import io.karma.pda.common.init.ModBlocks;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.item.MemoryCardItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class CommonEventHandler {
    public static final CommonEventHandler INSTANCE = new CommonEventHandler();

    // @formatter:off
    private CommonEventHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRegisterCommands);
        forgeBus.addListener(this::onRightClickBlock);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onNewRegistry);
    }

    private void onNewRegistry(final NewRegistryEvent event) {
        PDAMod.LOGGER.info("Creating registries");
        event.create(RegistryBuilder.of(new ResourceLocation(Constants.MODID, "apps")));
    }

    private void onRegisterCommands(final RegisterCommandsEvent event) {
        PDAMod.LOGGER.info("Registering commands");
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

    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final var world = event.getLevel();
        final var player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }
        final var pos = event.getPos();
        final var state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.dock.get()) {
            return;
        }
        event.setUseBlock(Event.Result.ALLOW);
    }
}

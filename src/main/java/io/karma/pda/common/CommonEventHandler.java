/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.init.ModBlocks;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.item.MemoryCardItem;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.cb.CPacketOpenApp;
import io.karma.pda.common.session.DefaultSessionHandler;
import io.karma.pda.common.util.TreeGraph;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Collectors;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class CommonEventHandler {
    public static final CommonEventHandler INSTANCE = new CommonEventHandler();
    public static final EntityDataAccessor<Integer> GLITCH_TICK = SynchedEntityData.defineId(Player.class,
        EntityDataSerializers.INT);
    public static final int GLITCH_TICKS = 10;

    // @formatter:off
    private CommonEventHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRegisterCommands);
        forgeBus.addListener(this::onRightClickBlock);
        forgeBus.addListener(this::onLivingDamage);
        forgeBus.addListener(this::onLivingTick);
        forgeBus.addListener(this::onEntityJoinLevel);
        forgeBus.addListener(this::onPlayerLoggedIn);
        forgeBus.addListener(this::onPlayerLoggedOut);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onNewRegistry);
    }

    private void onEntityJoinLevel(final EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        player.getEntityData().define(GLITCH_TICK, 0);
    }

    private void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        final var player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // Make sure to re-send all active sessions to newly joined players
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var activeSessions = sessionHandler.getActiveSessions();
        for (final var entry : activeSessions.entrySet()) {
            final var session = entry.getValue();
            final var context = session.getContext();
            final var hand = context.getHand();
            final var contextObj = hand != null ? hand : context.getPos();
            final var target = PacketDistributor.PLAYER.with(() -> serverPlayer);
            final var sessionId = session.getId();
            final var playerId = context.getPlayer().getUUID();
            // @formatter:off
            PDAMod.LOGGER.debug("Resending session {} to new client", sessionId);
            PDAMod.CHANNEL.send(target, new CPacketCreateSession(
                context.getType(), null, sessionId, playerId, contextObj));
            // @formatter:on
            // Open all already opened apps on the newly joined client
            final var openApps = session.getLauncher().getOpenApps();
            for (final var app : openApps) {
                // @formatter:off
                final var appTypeName = app.getType().getName();
                PDAMod.LOGGER.debug("Resending app {} in {} to new client", appTypeName, sessionId);
                PDAMod.CHANNEL.send(target, new CPacketOpenApp(sessionId, playerId, appTypeName,
                    app.getViews()
                    .stream()
                    .map(view -> Pair.of(view.getName(),
                        TreeGraph.from(view.getContainer(), Container.class, Container::getChildren,
                            io.karma.pda.api.common.app.component.Component::getId).flatten()))
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))));
                // @formatter:on
            }
        }
    }

    private void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        final var player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // Make sure to terminate all sessions that might be active from a player when leaving
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        PDAMod.LOGGER.debug("Terminating all sessions for player {}", serverPlayer.getUUID());
        sessionHandler.findByPlayer(serverPlayer).forEach(sessionHandler::terminateSession);
    }

    private void onLivingDamage(final LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        player.getEntityData().set(GLITCH_TICK, GLITCH_TICKS, true);
    }

    private void onLivingTick(final LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        final var data = player.getEntityData();
        final int glitchTick = data.get(GLITCH_TICK);
        if (glitchTick > 0) {
            data.set(GLITCH_TICK, glitchTick - 1);
        }
    }

    private void onNewRegistry(final NewRegistryEvent event) {
        PDAMod.LOGGER.info("Creating registries");
        event.create(RegistryBuilder.of(Constants.COMPONENT_REGISTRY_NAME));
        event.create(RegistryBuilder.of(Constants.APP_REGISTRY_NAME));
        event.create(RegistryBuilder.of(Constants.THEME_REGISTRY_NAME));
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

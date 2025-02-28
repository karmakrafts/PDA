/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.karma.pda.api.util.Constants;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.init.ModItems;
import io.karma.pda.mod.item.MemoryCardItem;
import io.karma.pda.mod.network.cb.CPacketCancelInteraction;
import io.karma.pda.mod.network.cb.CPacketTerminateSession;
import io.karma.pda.mod.session.DefaultSessionHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 06/06/2024
 */
public final class CommandHandler {
    public static final CommandHandler INSTANCE = new CommandHandler();

    // @formatter:off
    private CommandHandler() {}
    // @formatter:on

    @Internal
    public void setup() {
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(final RegisterCommandsEvent event) {
        PDAMod.LOGGER.info("Registering commands");
        // @formatter:off
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal(Constants.MODID)
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("session")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("list")
                    .executes(this::onSessionListCommand)
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("terminate")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, UUID>argument("id", UuidArgument.uuid())
                        .executes(this::onSessionTerminateCommand)
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("terminateFor")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, EntitySelector>argument("player", EntityArgument.player())
                        .executes(this::onSessionTerminateForCommand)
                    )
                )
            )
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("card")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("lock")
                    .executes(this::onCardLockCommand)
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("lockWithName")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("ownerName", StringArgumentType.string())
                        .executes(this::onCardLockWithNameCommand)
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("unlock")
                    .executes(this::onCardUnlockCommand)
                )
            )
        );
        // @formatter:on
    }

    private int onSessionListCommand(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        final var sessions = DefaultSessionHandler.INSTANCE.getActiveSessions();
        final var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy hh:mm:ss").withZone(ZoneId.systemDefault());
        serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.active_sessions",
            Constants.MODID), sessions.size()));
        for (final var entry : sessions.entrySet()) {
            final var session = entry.getValue();
            serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.session",
                Constants.MODID), session.getId()));
            serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.session_owner",
                Constants.MODID), session.getContext().getPlayer().getName().getString()));
            serverPlayer.sendSystemMessage(Component.translatable(String.format(
                "message.%s.command.session_creation_time",
                Constants.MODID), dateFormatter.format(session.getCreationTime())));
            final var currentApp = session.getLauncher().getCurrentApp();
            final var currentAppName = currentApp == null ? "n/a" : currentApp.getType().getName();
            serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.session_app",
                Constants.MODID), currentAppName));
        }
        return Command.SINGLE_SUCCESS;
    }

    private int onSessionTerminateCommand(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        final var sessionId = context.getArgument("id", UUID.class);
        final var sessionHandler = DefaultSessionHandler.INSTANCE;
        final var session = sessionHandler.findById(sessionId);
        if (session == null) {
            return Command.SINGLE_SUCCESS;
        }
        // Broadcast terminate packet to all clients and terminate on server
        final var sessionPlayer = session.getContext().getPlayer();
        if (!(sessionPlayer instanceof ServerPlayer sessServerPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        final var playerId = sessionPlayer.getUUID();
        PDAMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sessServerPlayer),
            new CPacketCancelInteraction(serverPlayer.getUUID()));
        PDAMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new CPacketTerminateSession(sessionId, playerId, false));
        sessionHandler.terminateSession(session);

        return Command.SINGLE_SUCCESS;
    }

    private int onSessionTerminateForCommand(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        try {
            final var sessionPlayer = context.getArgument("player",
                EntitySelector.class).findSinglePlayer(context.getSource());
            final var playerId = sessionPlayer.getUUID();
            final var sessionHandler = DefaultSessionHandler.INSTANCE;
            // Broadcast terminate packet to all clients and terminate on server
            for (final var session : sessionHandler.findByPlayer(sessionPlayer)) {
                PDAMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sessionPlayer),
                    new CPacketCancelInteraction(serverPlayer.getUUID()));
                PDAMod.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new CPacketTerminateSession(session.getId(), playerId, false));
                sessionHandler.terminateSession(session);
            }
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not terminate session for player: {}", Exceptions.toFancyString(error));
        }
        return Command.SINGLE_SUCCESS;
    }

    private int onCardLockCommand(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        final var inventory = serverPlayer.getInventory();
        final var heldStack = inventory.getItem(inventory.selected);
        if (heldStack.isEmpty() || heldStack.getItem() != ModItems.memoryCard.get()) {
            serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.no_memory_card",
                Constants.MODID)));
            return Command.SINGLE_SUCCESS;
        }
        final var tag = heldStack.getOrCreateTag();
        tag.putUUID(MemoryCardItem.TAG_OWNER_ID, serverPlayer.getUUID());
        tag.putString(MemoryCardItem.TAG_OWNER_DISPLAY_NAME, serverPlayer.getName().getString());
        return Command.SINGLE_SUCCESS;
    }

    private int onCardLockWithNameCommand(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        final var inventory = serverPlayer.getInventory();
        final var heldStack = inventory.getItem(inventory.selected);
        if (heldStack.isEmpty() || heldStack.getItem() != ModItems.memoryCard.get()) {
            serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.no_memory_card",
                Constants.MODID)));
            return Command.SINGLE_SUCCESS;
        }
        final var tag = heldStack.getOrCreateTag();
        tag.putUUID(MemoryCardItem.TAG_OWNER_ID, serverPlayer.getUUID());
        tag.putString(MemoryCardItem.TAG_OWNER_DISPLAY_NAME, context.getArgument("ownerName", String.class));
        return Command.SINGLE_SUCCESS;
    }

    private int onCardUnlockCommand(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return Command.SINGLE_SUCCESS;
        }
        final var inventory = serverPlayer.getInventory();
        final var heldStack = inventory.getItem(inventory.selected);
        if (heldStack.isEmpty() || heldStack.getItem() != ModItems.memoryCard.get()) {
            serverPlayer.sendSystemMessage(Component.translatable(String.format("message.%s.command.no_memory_card",
                Constants.MODID)));
            return Command.SINGLE_SUCCESS;
        }
        final var tag = heldStack.getTag();
        if (tag == null) {
            return Command.SINGLE_SUCCESS; // Just return and ignore
        }
        tag.remove(MemoryCardItem.TAG_OWNER_ID);
        tag.remove(MemoryCardItem.TAG_OWNER_DISPLAY_NAME);
        return Command.SINGLE_SUCCESS;
    }
}

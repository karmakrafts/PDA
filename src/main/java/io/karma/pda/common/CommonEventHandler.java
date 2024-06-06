/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common;

import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.app.theme.font.FontFamily;
import io.karma.pda.api.app.theme.font.FontStyle;
import io.karma.pda.api.app.theme.font.FontVariant;
import io.karma.pda.api.display.DisplayModeSpec;
import io.karma.pda.api.util.Constants;
import io.karma.pda.client.render.display.DefaultDisplayRenderer;
import io.karma.pda.client.render.graphics.font.DefaultFontRenderer;
import io.karma.pda.common.init.ModBlocks;
import io.karma.pda.common.network.cb.CPacketCreateSession;
import io.karma.pda.common.network.cb.CPacketOpenApp;
import io.karma.pda.common.session.DefaultSessionHandler;
import io.karma.pda.common.util.TreeGraph;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
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
                            Component::getId).flatten()))
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

    private void onBakeFontFamilies(final IForgeRegistryInternal<FontFamily> registry, final RegistryManager manager) {
        // Preload fonts on client when baking font family registry
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft.getInstance().execute(() -> {
                PDAMod.LOGGER.info("Pre-generating font atlases for all registered families");
                // @formatter:off
                registry.getValues().stream()
                    .flatMap(family -> Arrays.stream(FontStyle.values())
                        .map(style -> family.getFont(style, FontVariant.DEFAULT_SIZE)))
                    .forEach(DefaultFontRenderer.INSTANCE::getFontAtlas);
                // @formatter:on
            });
        });
    }

    private void onBakeDisplayModes(final IForgeRegistryInternal<DisplayModeSpec> registry,
                                    final RegistryManager manager) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft.getInstance().execute(() -> {
                PDAMod.LOGGER.info("Creating display modes");
                DefaultDisplayRenderer.INSTANCE.createDisplayModes(registry.getValues());
            });
        });
    }

    private void onNewRegistry(final NewRegistryEvent event) {
        PDAMod.LOGGER.info("Creating registries");
        event.create(RegistryBuilder.of(Constants.COMPONENT_REGISTRY_NAME));
        event.create(RegistryBuilder.of(Constants.APP_REGISTRY_NAME));
        event.create(RegistryBuilder.of(Constants.THEME_REGISTRY_NAME));
        event.create(RegistryBuilder.<FontFamily>of(Constants.FONT_FAMILY_REGISTRY_NAME).onBake(this::onBakeFontFamilies));
        event.create(RegistryBuilder.of(Constants.GRADIENT_FUNCTION_REGISTRY_NAME));
        event.create(RegistryBuilder.<DisplayModeSpec>of(Constants.DISPLAY_MODE_REGISTRY_NAME).onBake(this::onBakeDisplayModes));
    }

    private void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
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

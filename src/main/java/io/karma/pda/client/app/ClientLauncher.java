/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.app;

import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.app.DefaultLauncher;
import io.karma.pda.common.network.sb.SPacketCloseApp;
import io.karma.pda.common.network.sb.SPacketOpenApp;
import io.karma.pda.common.util.BlockingHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
@OnlyIn(Dist.CLIENT)
public class ClientLauncher extends DefaultLauncher {
    private final BlockingHashMap<ResourceLocation, App> pendingApps = new BlockingHashMap<>();

    public ClientLauncher(final Session session) {
        super(session);
    }

    @ApiStatus.Internal
    public void addPendingApp(final App app) {
        final var name = app.getType().getName();
        if (pendingApps.containsKey(name)) {
            return;
        }
        pendingApps.put(name, app);
    }

    private CompletableFuture<@Nullable App> getPendingApp(final ResourceLocation name) {
        return pendingApps.removeLater(name, 200, TimeUnit.MILLISECONDS, PDAMod.EXECUTOR_SERVICE);
    }

    @Override
    public <A extends App> @Nullable A closeApp(final AppType<A> type) {
        Minecraft.getInstance().execute(() -> {
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug("Requesting topmost app to close for session {}", sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketCloseApp(sessionId, type.getName()));
        });
        return super.closeApp(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type) {
        Minecraft.getInstance().execute(() -> {
            final var name = type.getName();
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug("Requesting app {} to open for session {}", name, sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketOpenApp(sessionId, name));
        });
        return getPendingApp(type.getName()).thenApply(app -> (A) app);
    }
}

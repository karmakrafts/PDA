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
import io.karma.sliced.slice.Slice;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
@OnlyIn(Dist.CLIENT)
public class ClientLauncher extends DefaultLauncher {
    private final Session session;
    private final Stack<App> activeApps = new Stack<>();
    private final Object appStackLock = new Object();

    public ClientLauncher(final Session session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends App> @Nullable A closeApp(final AppType<A> type) {
        synchronized (appStackLock) {
            if (activeApps.size() <= 1) {
                return null;
            }
        }
        Minecraft.getInstance().execute(() -> {
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug("Requesting topmost app to close for session {}", sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketCloseApp(sessionId, type.getName()));
        });
        synchronized (appStackLock) {
            A result = null;
            for (final App app : activeApps) {
                if (app.getType() != type) {
                    continue;
                }
                result = (A) app;
                break;
            }
            if (result != null) {
                activeApps.remove(result);
            }
            return result;
        }
    }

    @Override
    public <A extends App> CompletableFuture<@Nullable A> openApp(final AppType<A> type) {
        Minecraft.getInstance().execute(() -> {
            final var name = type.getName();
            final var sessionId = session.getId();
            PDAMod.LOGGER.debug("Requesting app {} to open for session {}", name, sessionId);
            PDAMod.CHANNEL.sendToServer(new SPacketOpenApp(sessionId, name));
        });
        return CompletableFuture.supplyAsync(() -> {
            return null; // TODO: ...
        }, PDAMod.EXECUTOR_SERVICE);
    }

    @Override
    public Slice<App> getActiveApps() {
        synchronized (appStackLock) {
            return Slice.of(activeApps);
        }
    }
}

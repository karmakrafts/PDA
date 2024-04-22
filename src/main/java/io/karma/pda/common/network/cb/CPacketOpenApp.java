/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.util.JSONUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class CPacketOpenApp {
    private final UUID sessionId;
    private final UUID playerId;
    private final ResourceLocation name;
    private final List<AppView> views;

    public CPacketOpenApp(final UUID sessionId, final @Nullable UUID playerId, final ResourceLocation name,
                          final List<AppView> views) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.name = name;
        this.views = views;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public List<AppView> getViews() {
        return views;
    }

    public static void encode(final CPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.sessionId);
        final var playerId = packet.playerId;
        if (playerId != null) {
            buffer.writeBoolean(true);
            buffer.writeUUID(playerId);
        }
        else {
            buffer.writeBoolean(false);
        }
        buffer.writeResourceLocation(packet.getName());
        final var compressedViews = packet.views.stream().map(JSONUtils::compressRaw).toList();
        buffer.writeInt(compressedViews.size());
        compressedViews.forEach(buffer::writeByteArray);
    }

    public static CPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var playerId = buffer.readBoolean() ? buffer.readUUID() : null;
        final var name = buffer.readResourceLocation();
        final var numViews = buffer.readInt();
        final var views = new ArrayList<AppView>(numViews);
        for (var i = 0; i < numViews; i++) {
            views.add(JSONUtils.decompressRaw(buffer.readByteArray(), AppView.class));
        }
        return new CPacketOpenApp(sessionId, playerId, name, views);
    }
}

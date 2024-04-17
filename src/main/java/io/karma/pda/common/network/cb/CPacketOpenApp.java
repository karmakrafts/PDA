/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network.cb;

import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.common.network.AppViewCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class CPacketOpenApp {
    private final UUID sessionId;
    private final ResourceLocation name;
    private final List<AppView> views;

    public CPacketOpenApp(final UUID sessionId, final ResourceLocation name, final List<AppView> views) {
        this.sessionId = sessionId;
        this.name = name;
        this.views = views;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public ResourceLocation getName() {
        return name;
    }

    public List<AppView> getViews() {
        return views;
    }

    public static void encode(final CPacketOpenApp packet, final FriendlyByteBuf buffer) {
        final var viewDataArrays = new ArrayList<byte[]>();
        for (final var view : packet.views) {
            final var viewData = AppViewCodec.encode(view);
            if (viewData.length == 0) {
                continue; // TODO: warn?
            }
            viewDataArrays.add(viewData);
        }
        buffer.writeUUID(packet.sessionId);
        buffer.writeResourceLocation(packet.getName());
        buffer.writeInt(viewDataArrays.size());
        for (final var viewData : viewDataArrays) {
            buffer.writeByteArray(viewData);
        }
    }

    public static CPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var sessionId = buffer.readUUID();
        final var name = buffer.readResourceLocation();
        final var numViews = buffer.readInt();
        final var views = new ArrayList<AppView>();
        for (var i = 0; i < numViews; i++) {
            final var view = AppViewCodec.decode(buffer.readByteArray());
            if (view == null) {
                continue; // TODO: warn?
            }
            views.add(view);
        }
        return new CPacketOpenApp(sessionId, name, views);
    }
}

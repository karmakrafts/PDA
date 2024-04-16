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

/**
 * @author Alexander Hinze
 * @since 14/04/2024
 */
public final class CPacketOpenApp {
    private final ResourceLocation name;
    private final List<AppView> views;

    public CPacketOpenApp(final ResourceLocation name, final List<AppView> views) {
        this.name = name;
        this.views = views;
    }

    public ResourceLocation getName() {
        return name;
    }

    public List<AppView> getViews() {
        return views;
    }

    public static void encode(final CPacketOpenApp packet, final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.getName());
        var numViewDataArrays = 0;
        final var viewDataArrays = new ArrayList<byte[]>();
        for (final var view : packet.views) {
            final var viewData = AppViewCodec.encode(view);
            if (viewData.length == 0) {
                continue; // TODO: warn?
            }
            viewDataArrays.add(viewData);
            numViewDataArrays++;
        }
        buffer.writeVarInt(numViewDataArrays);
        for (final var viewData : viewDataArrays) {
            buffer.writeByteArray(viewData);
        }
    }

    public static CPacketOpenApp decode(final FriendlyByteBuf buffer) {
        final var name = buffer.readResourceLocation();
        final var numViews = buffer.readVarInt();
        final var views = new ArrayList<AppView>();
        for (var i = 0; i < numViews; i++) {
            final var viewData = buffer.readByteArray();
            final var view = AppViewCodec.decode(viewData);
            if (view == null) {
                continue; // TODO: warn?
            }
            views.add(view);
        }
        return new CPacketOpenApp(name, views);
    }
}

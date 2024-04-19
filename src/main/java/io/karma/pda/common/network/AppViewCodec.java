/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.app.view.DefaultView;
import io.karma.pda.api.common.util.JSONUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class AppViewCodec {
    // @formatter:off
    private AppViewCodec() {}
    // @formatter:on

    public static void encode(final ObjectNode node, final @Nullable AppView view) {
        if (view == null) {
            return;
        }
        node.put("name", view.getName());
        final var containerNode = JSONUtils.MAPPER.createObjectNode();
        ComponentCodec.encode(containerNode, view.getContainer());
        node.set("container", containerNode);
    }

    public static byte[] encode(final @Nullable AppView view) {
        if (view == null) {
            return new byte[0];
        }
        final var node = JSONUtils.MAPPER.createObjectNode();
        encode(node, view);
        return JSONUtils.compressRaw(node);
    }

    public static @Nullable AppView decode(final ObjectNode node) {
        final var name = Objects.requireNonNull(node.get("name")).asText();
        final var containerNode = Objects.requireNonNull(node.get("container"));
        if (!(containerNode instanceof ObjectNode componentsObjectNode)) {
            return null;
        }
        final var component = ComponentCodec.decode(componentsObjectNode);
        if (!(component instanceof Container container)) {
            return null;
        }
        return new DefaultView(name, container);
    }

    public static @Nullable AppView decode(final byte[] data) {
        return decode(Objects.requireNonNull(JSONUtils.decompressRaw(data, ObjectNode.class)));
    }
}

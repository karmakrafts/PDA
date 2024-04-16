/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.network;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.karma.pda.api.common.app.component.Container;
import io.karma.pda.api.common.app.view.AppView;
import io.karma.pda.api.common.app.view.DefaultView;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
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
        final var componentsNode = JSONUtils.MAPPER.createObjectNode();
        ComponentCodec.encode(componentsNode, view.getContainer());
        node.set("components", componentsNode);
        PDAMod.LOGGER.debug("Encoded view:\n{}\n", node.toPrettyString());
    }

    public static byte[] encode(final @Nullable AppView view) {
        if (view == null) {
            return new byte[0];
        }
        final var node = JSONUtils.MAPPER.createObjectNode();
        encode(node, view);
        return JSONUtils.compress(node);
    }

    public static @Nullable AppView decode(final ObjectNode node) {
        final var name = Objects.requireNonNull(node.get("name")).asText();
        final var componentsNode = Objects.requireNonNull(node.get("components"));
        if (!(componentsNode instanceof ObjectNode componentsObjectNode)) {
            return null;
        }
        final var component = ComponentCodec.decode(componentsObjectNode);
        if (!(component instanceof Container container)) {
            return null;
        }
        return new DefaultView(name, container);
    }

    public static @Nullable AppView decode(final byte[] data) {
        return decode(Objects.requireNonNull(JSONUtils.decompress(data, ObjectNode.class)));
    }
}

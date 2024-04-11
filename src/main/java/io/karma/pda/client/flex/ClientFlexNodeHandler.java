/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.flex;

import io.karma.pda.api.client.flex.FlexNodeHandler;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.api.common.flex.FlexNode;
import io.karma.sliced.view.View;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientFlexNodeHandler implements FlexNodeHandler {
    public static final ClientFlexNodeHandler INSTANCE = new ClientFlexNodeHandler();
    private final HashMap<UUID, FlexNode> nodes = new HashMap<>();

    // @formatter:off
    private ClientFlexNodeHandler() {}
    // @formatter:on

    @Override
    public void removeNode(final UUID uuid) {
        if (!nodes.containsKey(uuid)) {
            return;
        }
        if (nodes.remove(uuid) instanceof Disposable disposable) {
            disposable.dispose();
        }
    }

    @Override
    public void removeNode(final Component component) {
        removeNode(component.getUUID());
    }

    @Override
    public FlexNode getOrCreateNode(final UUID uuid) {
        return nodes.computeIfAbsent(uuid, id -> new DefaultFlexNode());
    }

    @Override
    public FlexNode getOrCreateNode(final Component component) {
        return nodes.computeIfAbsent(component.getUUID(), id -> DefaultFlexNode.copyOf(component.getLayoutSpec()));
    }

    @Override
    public View<FlexNode> getNodes() {
        return View.of(nodes.values());
    }
}

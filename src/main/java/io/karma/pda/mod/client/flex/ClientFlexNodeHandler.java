/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.flex;

import io.karma.pda.mod.PDAMod;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.Container;
import io.karma.pda.api.client.flex.FlexNodeHandler;
import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientFlexNodeHandler implements FlexNodeHandler {
    public static final ClientFlexNodeHandler INSTANCE = new ClientFlexNodeHandler();
    private final ConcurrentHashMap<UUID, FlexNode> nodes = new ConcurrentHashMap<>();

    // @formatter:off
    private ClientFlexNodeHandler() {}
    // @formatter:on

    @Override
    public @Nullable FlexNode getNode(final UUID id) {
        return nodes.get(id);
    }

    @Override
    public @Nullable FlexNode getNode(final Component component) {
        return nodes.get(component.getId());
    }

    @Override
    public void removeNode(final UUID id) {
        if (!nodes.containsKey(id)) {
            return;
        }
        PDAMod.LOGGER.debug("Removing flex node {}", id);
        if (nodes.remove(id) instanceof Disposable disposable) {
            disposable.dispose();
        }
    }

    @Override
    public void removeNode(final Component component) {
        removeNode(component.getId());
    }

    @Override
    public FlexNode getOrCreateNode(final UUID id) {
        return nodes.computeIfAbsent(id, i -> {
            PDAMod.LOGGER.debug("Creating flex node {}", i);
            return new ClientFlexNode();
        });
    }

    @Override
    public FlexNode getOrCreateNode(final Component component) {
        final var result = getOrCreateNode(component.getId());
        result.setFrom(component.getFlexNode());
        return result;
    }

    @Override
    public FlexNode getOrCreateNodeRecursive(final Component component) {
        final var result = getOrCreateNode(component); // Create shallow copy
        if (component instanceof Container container) { // Reconstruct children recursively if we are a container
            final var children = container.getChildren();
            for (final var child : children) {
                result.addChild(getOrCreateNodeRecursive(child));
            }
        }
        return result;
    }

    @Override
    public void removeNodeRecursively(final Component component) {
        if (component instanceof Container container) {
            final var children = container.getChildren();
            for (final var child : children) {
                removeNodeRecursively(child);
            }
        }
        removeNode(component);
    }

    @Override
    public Collection<FlexNode> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }
}

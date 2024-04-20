/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.flex;

import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * A client-side interface for creating, caching and managing
 * flex layout node instances. <b>Note that nodes created by
 * this interface need to be removed manually in order to
 * prevent a memory leak!</b>
 *
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface FlexNodeHandler {
    /**
     * Remove the flex node associated with
     * the given unique ID if present.
     *
     * @param id The unique ID of the node to remove.
     */
    void removeNode(final UUID id);

    /**
     * Remove the flex node associated with
     * the given component if present.
     *
     * @param component The component the node to be removed
     *                  is associated with.
     */
    void removeNode(final Component component);

    /**
     * Get or create a new flex node with the given
     * unique ID.
     *
     * @param id The unique ID of the new node to create.
     * @return A new flex node instance with the given unique ID,
     * or the instance of a previously created flex node with the
     * same ID.
     */
    FlexNode getOrCreateNode(final UUID id);

    /**
     * Get or create a shallow copy of the flex node
     * of the given component.
     *
     * @param component The component to derive the flex node from.
     * @return A new flex node instance with the properties of
     * the given component's flex node, or the instance of a previously
     * created flex node with the same (component) ID.
     */
    FlexNode getOrCreateNode(final Component component);

    /**
     * Remove all flex nodes associated with the given
     * component (or container) recursively.
     *
     * @param component The component of which to remove all
     *                  associated flex nodes.
     */
    void removeNodeRecursively(final Component component);

    /**
     * Get or create a deep (recursive) copy of the flex node
     * of the given component. Acts the same as {@link #getOrCreateNode(Component)},
     * except that it recursively copies all child nodes too.
     *
     * @param component The component to derive the flex node and its children from.
     * @return A new flex node instance with the properties and the children of
     * the given component's flex node, or the instance of a previously
     * created flex node with the same (component) ID.
     */
    FlexNode getOrCreateNodeRecursive(final Component component);

    /**
     * Retrieves a flex node with the given ID if present.
     *
     * @param id The ID of the node to retrieve.
     * @return A flex node with the given ID,
     * null if the given ID is not present.
     */
    @Nullable
    FlexNode getNode(final UUID id);

    /**
     * Retrieves a flex node associated with the given component
     * or container if present.
     *
     * @param component The component of which to retrieve the underlying flex node.
     * @return A flex node associated with the given component
     * or container, null if no such node is present.
     */
    @Nullable
    FlexNode getNode(final Component component);

    /**
     * Retrieves a collection of all currently cached flex nodes
     * held by this handler instance.
     *
     * @return A collection of all currently cached flex nodes.
     */
    Collection<FlexNode> getNodes();
}

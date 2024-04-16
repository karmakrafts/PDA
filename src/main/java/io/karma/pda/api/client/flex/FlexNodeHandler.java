/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.flex;

import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public interface FlexNodeHandler {
    void removeNode(final UUID uuid);

    void removeNode(final Component component);

    FlexNode getOrCreateNode(final UUID uuid);

    FlexNode getOrCreateNode(final Component component);

    Collection<FlexNode> getNodes();
}

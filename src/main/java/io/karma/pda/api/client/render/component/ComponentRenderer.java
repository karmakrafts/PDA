/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.component;

import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ComponentRenderer<C extends Component> {
    void render(final C component, final FlexNode flexNode, final GFX graphics);

    default void cleanup(final C component, final FlexNode flexNode, final GFX graphics) {
        ClientAPI.getFlexNodeHandler().removeNode(component);
    }
}

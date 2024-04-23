/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.component;

import io.karma.pda.api.client.render.graphics.Graphics;
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
    void render(final C component, final FlexNode flexNode, final Graphics graphics);
}

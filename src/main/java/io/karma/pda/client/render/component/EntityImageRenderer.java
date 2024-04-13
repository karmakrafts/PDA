/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import io.karma.pda.api.client.render.component.ComponentRenderer;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.common.app.component.EntityImage;
import io.karma.pda.api.common.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class EntityImageRenderer implements ComponentRenderer<EntityImage> {
    @Override
    public void render(final EntityImage component, final FlexNode flexNode, final GFX graphics) {

    }
}

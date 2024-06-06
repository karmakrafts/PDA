/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.graphics.Graphics;
import io.karma.pda.api.flex.FlexNode;
import io.karma.pda.foundation.component.RecipeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RecipeImageRenderer extends AbstractComponentRenderer<RecipeImage> {
    @Override
    public void render(final RecipeImage component, final FlexNode flexNode, final Graphics graphics) {
        super.render(component, flexNode, graphics);
    }
}

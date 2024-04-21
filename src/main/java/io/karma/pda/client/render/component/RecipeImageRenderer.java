/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.component;

import io.karma.pda.api.client.render.component.AbstractComponentRenderer;
import io.karma.pda.api.client.render.gfx.GFX;
import io.karma.pda.api.common.app.component.RecipeImage;
import io.karma.pda.api.common.flex.FlexNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RecipeImageRenderer extends AbstractComponentRenderer<RecipeImage> {
    @Override
    public void render(final RecipeImage component, final FlexNode flexNode, final GFX graphics) {
        super.render(component, flexNode, graphics);
    }
}

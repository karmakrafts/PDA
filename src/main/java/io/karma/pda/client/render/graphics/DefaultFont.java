/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.common.app.theme.font.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFont implements Font {
    @Override
    public ResourceLocation getName() {
        return null;
    }
}

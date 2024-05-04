/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.api.client.render.graphics.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 04/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFontRenderer implements FontRenderer {
    public static final DefaultFontRenderer INSTANCE = new DefaultFontRenderer();

    // @formatter:off
    private DefaultFontRenderer() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setupEarly() {

    }
}

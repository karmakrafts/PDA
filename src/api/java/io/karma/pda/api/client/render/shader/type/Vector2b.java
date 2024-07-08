/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 17/06/2024
 */
@OnlyIn(Dist.CLIENT)
public record Vector2b(boolean x, boolean y) {
    public Vector2b() {
        this(false, false);
    }

    public Vector2b(final boolean value) {
        this(value, value);
    }
}

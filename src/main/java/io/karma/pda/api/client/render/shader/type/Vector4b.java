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
public record Vector4b(boolean x, boolean y, boolean z, boolean w) {
    public Vector4b() {
        this(false, false, false, false);
    }

    public Vector4b(final boolean value) {
        this(value, value, value, value);
    }
}

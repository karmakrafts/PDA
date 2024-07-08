/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 15/06/2024
 */
@OnlyIn(Dist.CLIENT)
public enum ScalarType implements Type {
    // @formatter:off
    INT   (0,     Integer.BYTES),
    FLOAT (0F,    Float.BYTES),
    DOUBLE(0D,    Double.BYTES),
    BOOL  (false, Integer.BYTES); // Bools are ints in GLSL
    // @formatter:on

    private final Object defaultValue;
    private final int componentSize;

    ScalarType(final Object defaultValue, final int componentSize) {
        this.defaultValue = defaultValue;
        this.componentSize = componentSize;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public int getComponentSize() {
        return componentSize;
    }

    @Override
    public int getComponentCount() {
        return 1;
    }
}

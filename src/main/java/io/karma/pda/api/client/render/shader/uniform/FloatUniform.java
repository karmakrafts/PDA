/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface FloatUniform extends GenericUniform<Float> {
    float getFloat();

    void setFloat(final float value);

    @Override
    default void set(final Float value) {
        setFloat(value);
    }

    @Override
    default Float get() {
        return getFloat();
    }
}

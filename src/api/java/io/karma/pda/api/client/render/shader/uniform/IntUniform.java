/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface IntUniform extends GenericUniform<Integer> {
    int getInt();

    void setInt(final int value);

    @Override
    default void set(final Integer value) {
        setInt(value);
    }

    @Override
    default Integer get() {
        return getInt();
    }
}

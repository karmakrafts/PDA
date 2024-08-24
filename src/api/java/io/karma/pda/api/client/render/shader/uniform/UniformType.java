/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.util.MemoryUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 14/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface UniformType {
    int DEFAULT_ALIGNMENT = 16;

    int getHash();

    Uniform create(final String name, final Object defaultValue);

    default Uniform create(final String name) {
        return create(name, getDefaultValue());
    }

    int getComponentSize();

    int getComponentCount();

    default int getAlignment() {
        return getComponentSize();
    }

    default int getSize() {
        return getComponentSize() * getComponentCount();
    }

    default int getAlignedSize() {
        return MemoryUtils.align(getSize(), getAlignment());
    }

    Object getDefaultValue();

    default boolean isSupported() {
        return true;
    }

    default UniformType derive(final Object defaultValue) {
        return new DerivedUniformType(this, defaultValue);
    }
}

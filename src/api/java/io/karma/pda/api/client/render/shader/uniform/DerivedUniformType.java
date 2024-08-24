/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 14/06/2024
 */
@OnlyIn(Dist.CLIENT)
final class DerivedUniformType implements UniformType {
    private final UniformType delegate;
    private final Object defaultValue;

    DerivedUniformType(final UniformType delegate, final Object defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    @Override
    public int getComponentSize() {
        return delegate.getComponentSize();
    }

    @Override
    public int getComponentCount() {
        return delegate.getComponentCount();
    }

    @Override
    public Uniform create(final String name, final Object defaultValue) {
        return delegate.create(name, defaultValue);
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public UniformType derive(final Object defaultValue) {
        return new DerivedUniformType(delegate, defaultValue);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", delegate, defaultValue);
    }

    @Override
    public int hashCode() {
        return delegate.getHash();
    }

    @Override
    public int getHash() {
        return delegate.getHash();
    }
}

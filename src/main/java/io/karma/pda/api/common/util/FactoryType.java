/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public class FactoryType<T> {
    protected final ResourceLocation name;
    protected final Supplier<T> factory;

    protected FactoryType(final ResourceLocation name, final Supplier<T> factory) {
        this.name = name;
        this.factory = factory;
    }

    public ResourceLocation getName() {
        return name;
    }

    public T create() {
        return factory.get();
    }
}

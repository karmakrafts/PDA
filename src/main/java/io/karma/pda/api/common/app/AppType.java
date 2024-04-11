/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class AppType<A extends App> {
    private static final AppType<?> NULL = new AppType<>(null, null);

    private final ResourceLocation name;
    private final Function<AppType<A>, A> factory;

    public AppType(final ResourceLocation name, final Function<AppType<A>, A> factory) {
        this.name = name;
        this.factory = factory;
    }

    public ResourceLocation getName() {
        return name;
    }

    public A create() {
        return factory.apply(this);
    }

    @SuppressWarnings("unchecked")
    public static <A extends App> AppType<A> nullType() {
        return (AppType<A>) NULL;
    }
}

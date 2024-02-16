package io.karma.pda.api.app;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class AppType<A extends App> {
    private final ResourceLocation name;
    private final Supplier<A> factory;

    public AppType(final ResourceLocation name, final Supplier<A> factory) {
        this.name = name;
        this.factory = factory;
    }

    public ResourceLocation getName() {
        return name;
    }

    public A create() {
        return factory.get();
    }
}

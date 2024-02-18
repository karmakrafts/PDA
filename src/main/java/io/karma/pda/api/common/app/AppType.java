/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.util.FactoryType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 13/02/2024
 */
public final class AppType<A extends App> extends FactoryType<A> {
    public AppType(final ResourceLocation name, final Supplier<A> factory) {
        super(name, factory);
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package mock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class MockResourceKey<V> extends ResourceKey<V> {
    public MockResourceKey(final ResourceLocation registryName, final ResourceLocation name) {
        super(registryName, name);
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Sampler {
    int getId();

    String getName();

    void setup(final ShaderProgram program);

    void bind(final ShaderProgram program);

    void unbind(final ShaderProgram program);

    default boolean isDynamic() {
        return true;
    }

    default void invalidate() {}
}

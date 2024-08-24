/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface Uniform {
    UniformType getType();

    String getName();

    void apply(final ShaderProgram program);

    void upload(final UniformBuffer buffer, final long address);

    void notifyUpdate();

    boolean requiresUpdate();
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 23/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface UniformBuffer {
    int getId();

    UniformCache getCache();

    int getFieldOffset(final String name);

    int getSize();

    void setup(final String name, final ShaderProgram program, final int bindingPoint);

    void bind(final String name, final ShaderProgram program, final int bindingPoint);

    void unbind(final String name, final ShaderProgram program, final int bindingPoint);
}

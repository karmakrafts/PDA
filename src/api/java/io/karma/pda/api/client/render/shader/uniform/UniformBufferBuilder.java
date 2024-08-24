/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.uniform;

import io.karma.pda.api.client.render.shader.ShaderProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;

/**
 * @author Alexander Hinze
 * @since 23/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface UniformBufferBuilder {
    UniformBufferBuilder uniform(final String name, final UniformType type);

    UniformBufferBuilder onBind(final BiConsumer<ShaderProgram, UniformBuffer> callback);

    UniformBufferBuilder onUnbind(final BiConsumer<ShaderProgram, UniformBuffer> callback);
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderFactory;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.client.render.shader.ShaderProgramBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderFactory implements ShaderFactory {
    public static final DefaultShaderFactory INSTANCE = new DefaultShaderFactory();

    // @formatter:off
    private DefaultShaderFactory() {}
    // @formatter:on

    @Override
    public ShaderProgram create(final Consumer<ShaderProgramBuilder> callback) {
        final var builder = new DefaultShaderProgramBuilder();
        callback.accept(builder);
        return builder.build();
    }
}

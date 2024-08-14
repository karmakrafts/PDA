/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.client.render.shader.uniform.DefaultUniformType;
import io.karma.pda.api.client.render.shader.uniform.UniformType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderProgramBuilder {
    ShaderProgramBuilder format(final VertexFormat format);

    ShaderProgramBuilder shader(final Consumer<ShaderObjectBuilder> callback);

    ShaderProgramBuilder uniform(final String name, final UniformType type);

    ShaderProgramBuilder defaultUniforms();

    ShaderProgramBuilder sampler(final String name);

    ShaderProgramBuilder sampler(final String name, final IntSupplier textureId);

    ShaderProgramBuilder sampler(final String name, final ResourceLocation location);

    ShaderProgramBuilder constant(final String name, final int value);

    ShaderProgramBuilder constant(final String name, final float value);

    ShaderProgramBuilder constant(final String name, final boolean value);

    ShaderProgramBuilder define(final String name);

    ShaderProgramBuilder define(final String name, final boolean value);

    ShaderProgramBuilder define(final String name, final int value);

    ShaderProgramBuilder onBind(final Consumer<ShaderProgram> bindCallback);

    ShaderProgramBuilder onUnbind(final Consumer<ShaderProgram> unbindCallback);

    default ShaderProgramBuilder uniformTime() { // @formatter:off
        return uniform("Time", DefaultUniformType.FLOAT)
            .onBind(program -> program.getUniformCache().getFloat("Time").setFloat(ClientAPI.getShaderTime()));
    } // @formatter:on
}

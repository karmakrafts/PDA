/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObjectBuilder;
import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.client.render.shader.ShaderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 17/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderObjectBuilder implements ShaderObjectBuilder {
    private ShaderType type;
    private ResourceLocation location;
    private Supplier<ShaderPreProcessor> shaderPreProcessorSupplier;

    // @formatter:off
    DefaultShaderObjectBuilder() {}
    // @formatter:on

    @Override
    public ShaderObjectBuilder type(final ShaderType type) {
        this.type = type;
        return this;
    }

    @Override
    public ShaderObjectBuilder location(final ResourceLocation location) {
        this.location = location;
        return this;
    }

    @Override
    public ShaderObjectBuilder preProcessor(final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier) {
        this.shaderPreProcessorSupplier = shaderPreProcessorSupplier;
        return this;
    }

    DefaultShaderObject build() {
        return new DefaultShaderObject(type, location, shaderPreProcessorSupplier);
    }
}

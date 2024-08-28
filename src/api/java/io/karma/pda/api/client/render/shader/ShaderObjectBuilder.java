/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import io.karma.pda.api.client.ClientAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 17/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderObjectBuilder {
    ShaderObjectBuilder type(final ShaderType type);

    ShaderObjectBuilder location(final ResourceLocation location);

    default ShaderObjectBuilder location(final String modId, final String path) {
        return location(new ResourceLocation(modId, path));
    }

    ShaderObjectBuilder preProcessor(final Supplier<ShaderPreProcessor> shaderPreProcessorSupplier);

    default ShaderObjectBuilder defaultPreProcessor() {
        return preProcessor(ClientAPI.getShaderHandler()::getPreProcessor);
    }
}

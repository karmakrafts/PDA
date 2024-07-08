/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface ShaderFactory {
    ShaderProgram create(final Consumer<ShaderProgramBuilder> callback);
}

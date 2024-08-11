/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.hook;

import com.mojang.blaze3d.shaders.Shader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ExtendedShader extends Shader {
    void apply();

    void clear();

    void setSampler(final String name, final Object id);
}

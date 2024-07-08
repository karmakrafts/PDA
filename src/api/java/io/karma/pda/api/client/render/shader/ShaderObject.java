/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderObject {
    ResourceLocation getLocation();

    ShaderType getType();

    int getId();

    boolean isCompiled();

    void requestRecompile();

    boolean isRecompileRequested();

    void onBindProgram(final ShaderProgram program);

    void onUnbindProgram(final ShaderProgram program);
}

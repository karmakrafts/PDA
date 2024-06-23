/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.shader.uniform.UniformCache;
import io.karma.pda.api.dispose.Disposable;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 13/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ShaderProgram extends Disposable {
    void bind();

    void unbind();

    int getId();

    UniformCache getUniformCache();

    int getSampler(final String name);

    VertexFormat getVertexFormat();

    RenderStateShard.ShaderStateShard asStateShard();

    boolean isLinked();

    void requestRelink();

    boolean isRelinkRequested();
}

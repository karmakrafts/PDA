/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.display;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 05/06/2024
 */
@OnlyIn(Dist.CLIENT)
public interface DisplayBlitter {
    DisplayMode getMode();

    RenderType getRenderType();

    ShaderInstance getShader();

    void blit(final Matrix4f matrix, final VertexConsumer consumer);
}

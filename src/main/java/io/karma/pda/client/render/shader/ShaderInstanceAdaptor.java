/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ShaderInstanceAdaptor extends ShaderInstance {
    private final ShaderProgram delegate;

    public ShaderInstanceAdaptor(final ShaderProgram delegate, final VertexFormat format) throws IOException {
        super(Minecraft.getInstance().getResourceManager(), new ResourceLocation(""), format);
        this.delegate = delegate;
    }
}

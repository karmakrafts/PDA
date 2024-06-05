/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.display.DisplayBlitter;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.client.render.shader.ShaderInstanceAdaptor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultDisplayBlitter implements DisplayBlitter {
    public static final float MIN_X = 0.25F;
    public static final float MIN_Y = 0.125F;
    public static final float OFFSET_Z = 0.609375F;
    public static final float SIZE_X = 0.5F;
    public static final float MAX_X = MIN_X + SIZE_X;
    public static final float SIZE_Y = 0.5625F;
    public static final float MAX_Y = MIN_Y + SIZE_Y;
    private static final ResourceLocation PIXEL_TEXTURE = new ResourceLocation(Constants.MODID, "textures/pixel.png");
    private final DisplayMode mode;
    private final RenderType renderType;
    private ShaderInstanceAdaptor shader;

    DefaultDisplayBlitter(final DisplayMode mode) {
        this.mode = mode;
        // @formatter:off
        renderType = RenderType.create(String.format("%s:display_blit_%s", Constants.MODID, mode),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 6, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.CULL)
                .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                    () -> {
                        RenderSystem.setShaderTexture(0, mode.getFramebuffer().getColorTexture());
                        RenderSystem.setShaderTexture(1, PIXEL_TEXTURE);
                    },
                    () -> {
                        RenderSystem.setShaderTexture(0, 0);
                        RenderSystem.setShaderTexture(1, 0);
                    }
                ))
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> shader))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false));
        // @formatter:on
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {

    }

    @Override
    public void blit(final Matrix4f matrix, final VertexConsumer consumer) {
        consumer.vertex(matrix, MIN_X, MIN_Y, OFFSET_Z).uv(0F, 0F).color(-1).endVertex();
        consumer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z).uv(1F, 0F).color(-1).endVertex();
        consumer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z).uv(0F, 1F).color(-1).endVertex();
        consumer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z).uv(1F, 0F).color(-1).endVertex();
        consumer.vertex(matrix, MAX_X, MAX_Y, OFFSET_Z).uv(1F, 1F).color(-1).endVertex();
        consumer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z).uv(0F, 1F).color(-1).endVertex();
    }

    @Override
    public DisplayMode getMode() {
        return mode;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public ShaderInstanceAdaptor getShader() {
        return shader;
    }
}

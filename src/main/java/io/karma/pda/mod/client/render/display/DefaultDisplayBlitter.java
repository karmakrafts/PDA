/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.display;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.display.DisplayBlitter;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.util.Constants;
import io.karma.pda.api.util.FloatSupplier;
import io.karma.peregrine.api.framebuffer.AttachmentType;
import io.karma.peregrine.api.shader.ShaderProgram;
import io.karma.peregrine.api.shader.ShaderType;
import io.karma.peregrine.api.uniform.ScalarType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.Objects;

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

    DefaultDisplayBlitter(final DisplayMode mode, final FloatSupplier glitchFactorSupplier) {
        this.mode = mode;
        // @formatter:off
        renderType = RenderType.create(String.format("%s:display_blit_%s", Constants.MODID, mode.getSpec().name()),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 6, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(ShaderProgram.create(it -> it
                    .format(DefaultVertexFormat.NEW_ENTITY)
                    .stage(it2 -> it2
                        .type(ShaderType.VERTEX)
                        .location(Constants.MODID, "shaders/display_blit.vert.glsl")
                    )
                    .stage(it2 -> it2
                        .type(ShaderType.FRAGMENT)
                        .location(Constants.MODID, "shaders/display_blit.frag.glsl")
                    )
                    .define("DISPLAY_TYPE", mode.getSpec().type().getIndex())
                    .constant("DISPLAY_WIDTH", mode.getResolution().getWidth())
                    .constant("DISPLAY_HEIGHT", mode.getResolution().getHeight())
                    .constant("GLITCH_RATE", mode.getSpec().type().getGlitchRate())
                    .constant("GLITCH_FACTOR", mode.getSpec().type().getGlitchFactor())
                    .constant("GLITCH_BLOCKS", mode.getSpec().type().getGlitchBlocks())
                    .constant("PIXEL_FACTOR", mode.getSpec().type().getPixelationFactor())
                    .globalUniforms()
                    .uniform("GlitchFactor", ScalarType.FLOAT)
                    .sampler("Sampler0", Objects.requireNonNull(mode.getFramebuffer().getAttachment(AttachmentType.COLOR)).getTexture())
                    .sampler("Sampler1", PIXEL_TEXTURE) // Pixel filter texture
                    .onBind(program -> {
                        program.getUniforms().getFloat("GlitchFactor").setFloat(glitchFactorSupplier.get());
                    })).asStateShard()
                ).createCompositeState(false)
        );
        // @formatter:on
    }

    @Override
    public void blit(final Matrix4f matrix,
                     final VertexConsumer consumer,
                     final int packedLight,
                     final int packedOverlay) {
        // @formatter:off
        consumer.vertex(matrix, MIN_X, MIN_Y, OFFSET_Z)
            .color(-1)
            .uv(0F, 0F)
            .overlayCoords(packedOverlay)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0F, 0F, -1F)
            .endVertex();
        consumer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z)
            .color(-1)
            .uv(1F, 0F)
            .overlayCoords(packedOverlay)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0F, 0F, -1F)
            .endVertex();
        consumer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z)
            .color(-1)
            .uv(0F, 1F)
            .overlayCoords(packedOverlay)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0F, 0F, -1F)
            .endVertex();
        consumer.vertex(matrix, MAX_X, MIN_Y, OFFSET_Z)
            .color(-1)
            .uv(1F, 0F)
            .overlayCoords(packedOverlay)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0F, 0F, -1F)
            .endVertex();
        consumer.vertex(matrix, MAX_X, MAX_Y, OFFSET_Z)
            .color(-1)
            .uv(1F, 1F)
            .overlayCoords(packedOverlay)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0F, 0F, -1F)
            .endVertex();
        consumer.vertex(matrix, MIN_X, MAX_Y, OFFSET_Z)
            .color(-1)
            .uv(0F, 1F)
            .overlayCoords(packedOverlay)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0F, 0F, -1F)
            .endVertex();
        // @formatter:on
    }

    @Override
    public DisplayMode getMode() {
        return mode;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.display;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.client.render.display.DisplayBlitter;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.client.render.shader.ShaderType;
import io.karma.pda.api.client.render.shader.uniform.DefaultUniformType;
import io.karma.pda.api.util.Constants;
import io.karma.pda.api.util.FloatSupplier;
import io.karma.pda.mod.client.ClientEventHandler;
import io.karma.pda.mod.client.render.shader.DefaultShaderFactory;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

    DefaultDisplayBlitter(final DisplayMode mode, final FloatSupplier glitchFactorSupplier) {
        this.mode = mode;
        // @formatter:off
        renderType = RenderType.create(String.format("%s:display_blit_%s", Constants.MODID, mode.getSpec().name()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 6, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.CULL)
                .setShaderState(DefaultShaderFactory.INSTANCE.create(builder -> builder
                    .format(DefaultVertexFormat.POSITION_TEX_COLOR)
                    .shader(object -> object
                        .type(ShaderType.VERTEX)
                        .location(Constants.MODID, "shaders/display_blit.vert.glsl")
                        .defaultPreProcessor())
                    .shader(object -> object
                        .type(ShaderType.FRAGMENT)
                        .location(Constants.MODID, "shaders/display_blit.frag.glsl")
                        .defaultPreProcessor())
                    .define("DISPLAY_TYPE", mode.getSpec().type().getIndex())
                    .constant("DISPLAY_WIDTH", mode.getResolution().getWidth())
                    .constant("DISPLAY_HEIGHT", mode.getResolution().getHeight())
                    .constant("GLITCH_RATE", mode.getSpec().type().getGlitchRate())
                    .constant("GLITCH_FACTOR", mode.getSpec().type().getGlitchFactor())
                    .constant("GLITCH_BLOCKS", mode.getSpec().type().getGlitchBlocks())
                    .constant("PIXEL_FACTOR", mode.getSpec().type().getPixelationFactor())
                    .defaultUniforms() // ProjMat, ModelViewMat, ColorModulator
                    .uniform("Time", DefaultUniformType.FLOAT)
                    .uniform("GlitchFactor", DefaultUniformType.FLOAT)
                    .sampler("Sampler0", 0) // Actual framebuffer texture
                    .sampler("Sampler1", 1) // Pixel filter texture
                    .onBind(program -> {
                        program.setSampler("Sampler0", mode.getFramebuffer().getColorTexture());
                        program.setSampler("Sampler1", PIXEL_TEXTURE);
                        final var uniformCache = program.getUniformCache();
                        uniformCache.getFloat("Time").setFloat(ClientEventHandler.INSTANCE.getShaderTime());
                        uniformCache.getFloat("GlitchFactor").setFloat(glitchFactorSupplier.get());
                    })).asStateShard())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false));
        // @formatter:on
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
}

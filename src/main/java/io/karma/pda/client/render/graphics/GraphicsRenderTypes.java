/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.client.render.display.DisplayRenderer;
import io.karma.pda.common.PDAMod;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class GraphicsRenderTypes {
    private static final HashMap<ResourceLocation, RenderType> COLOR_TEXTURE_CACHE = new HashMap<>();
    public static final GraphicsRenderTypes INSTANCE = new GraphicsRenderTypes();
    private ShaderInstance colorShader;
    private ShaderInstance colorTextureShader;

    // @formatter:off
    public static final RenderType COLOR_TRIS = RenderType.create("pda_display_color_tris",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
        RenderType.CompositeState.builder()
            .setCullState(RenderStateShard.NO_CULL)
            .setShaderState(new RenderStateShard.ShaderStateShard(INSTANCE::getColorShader))
            .setOutputState(DisplayRenderer.DISPLAY_OUTPUT)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));

    private GraphicsRenderTypes() {}
    // @formatter:on

    public static RenderType createColorTextureTris(final ResourceLocation texture) {
        // @formatter:off
        return COLOR_TEXTURE_CACHE.computeIfAbsent(texture, t -> RenderType.create(String.format("pda_display_color_tex_tris__%s_%s", texture.getNamespace(), texture.getPath()),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(INSTANCE::getColorTextureShader))
                .setOutputState(DisplayRenderer.DISPLAY_OUTPUT)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
                .setTextureState(new RenderStateShard.EmptyTextureStateShard(
                    () -> RenderSystem.setShaderTexture(0, texture),
                    () -> {}
                ))
                .createCompositeState(false)));
        // @formatter:on
    }

    @ApiStatus.Internal
    public void setupEarly() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_color"),
                DefaultVertexFormat.POSITION_COLOR), shader -> colorShader = shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_color_tex"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> colorTextureShader = shader);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not register default GFX render type shaders: {}", error.getMessage());
        }
    }

    private ShaderInstance getColorShader() {
        return colorShader;
    }

    private ShaderInstance getColorTextureShader() {
        return colorTextureShader;
    }
}

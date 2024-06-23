/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.foundation.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.karma.pda.api.API;
import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.client.render.display.DisplayMode;
import io.karma.pda.api.util.Constants;
import io.karma.pda.api.util.Exceptions;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 06/06/2024
 */
@ApiStatus.Internal
@OnlyIn(Dist.CLIENT)
public final class ComponentRenderTypes {
    public static final ComponentRenderTypes INSTANCE = new ComponentRenderTypes();
    // @formatter:off
    public static final Function<DisplayMode, RenderType> SPINNER = Util.memoize(displayMode ->
        RenderType.create(String.format("pda_display_spinner__%s", displayMode),
            DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false,
            RenderType.CompositeState.builder()
                .setCullState(RenderStateShard.NO_CULL)
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> INSTANCE.getSpinnerShader(displayMode)))
                .setOutputState(displayMode.getOutputState())
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .createCompositeState(false)));
    private ShaderInstance spinnerShader;

    private ComponentRenderTypes() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setupEarly() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterShaders);
    }

    private void onRegisterShaders(final RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(Constants.MODID, "display_spinner"),
                DefaultVertexFormat.POSITION_TEX_COLOR), shader -> spinnerShader = shader);
        }
        catch (Throwable error) {
            API.getLogger().error("Could not register component shader: {}", Exceptions.toFancyString(error));
        }
    }

    private ShaderInstance getSpinnerShader(final DisplayMode displayMode) {
        spinnerShader.safeGetUniform("Time").set(ClientAPI.getShaderTime());
        return spinnerShader;
    }
}

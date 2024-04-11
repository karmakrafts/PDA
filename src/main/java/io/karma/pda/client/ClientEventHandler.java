/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client;

import io.karma.pda.api.client.event.RegisterAppRenderersEvent;
import io.karma.pda.api.client.event.RegisterComponentRenderersEvent;
import io.karma.pda.api.client.render.AppRenderer;
import io.karma.pda.api.client.render.AppRenderers;
import io.karma.pda.api.client.render.ComponentRenderer;
import io.karma.pda.api.client.render.ComponentRenderers;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.DefaultApps;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.DefaultComponents;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.client.render.app.DefaultAppRenderer;
import io.karma.pda.client.render.component.ContainerRenderer;
import io.karma.pda.client.render.component.LabelRenderer;
import io.karma.pda.client.render.entity.DockBlockEntityRenderer;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.init.ModBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();
    public static final ResourceLocation PDA_MODEL_V = new ResourceLocation(Constants.MODID, "item/pda_v");
    public static final ResourceLocation PDA_MODEL_H = new ResourceLocation(Constants.MODID, "item/pda_h");

    private float frameTime;
    private int clientTick;

    // @formatter:off
    private ClientEventHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final var forgeBus = MinecraftForge.EVENT_BUS;
        modBus.addListener(this::onRegisterEntityRenderers);
        modBus.addListener(this::onRegisterAdditionalModels);
        forgeBus.addListener(this::onRenderTick);
        forgeBus.addListener(this::onClientTick);
    }

    @SuppressWarnings("all")
    @ApiStatus.Internal
    public void fireRegisterEvents() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        // @formatter:off
        forgeBus.post(new RegisterComponentRenderersEvent(
            (type, renderer) -> ComponentRenderers.register((ComponentType<Component>)type, (ComponentRenderer<Component>) renderer)));
        forgeBus.post(new RegisterAppRenderersEvent(
            (type, renderer) -> AppRenderers.register((AppType<App>)type, (AppRenderer<App>) renderer)));
        // @formatter:on

        // Components
        ComponentRenderers.register(DefaultComponents.CONTAINER, new ContainerRenderer());
        ComponentRenderers.register(DefaultComponents.LABEL, new LabelRenderer());
        // Apps
        AppRenderers.register(DefaultApps.LAUNCHER, new DefaultAppRenderer<>());
        AppRenderers.register(DefaultApps.SETTINGS, new DefaultAppRenderer<>());
    }

    private void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            clientTick++;
        }
    }

    private void onRenderTick(final TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            frameTime = event.renderTickTime;
        }
    }

    public float getShaderTime() {
        return clientTick + frameTime;
    }

    public float getFrameTime() {
        return frameTime;
    }

    private void onRegisterEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        PDAMod.LOGGER.info("Registering block entity renderers");
        event.registerBlockEntityRenderer(ModBlockEntities.dock.get(), DockBlockEntityRenderer::new);
    }

    // Make sure our actual baked models get loaded by the game
    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        PDAMod.LOGGER.info("Registering models");
        event.register(PDA_MODEL_V);
        event.register(PDA_MODEL_H);
    }
}

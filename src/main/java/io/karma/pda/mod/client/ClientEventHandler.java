/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client;

import io.karma.pda.api.app.App;
import io.karma.pda.api.app.AppType;
import io.karma.pda.api.app.DefaultApps;
import io.karma.pda.api.app.component.Component;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.api.client.event.RegisterAppRenderersEvent;
import io.karma.pda.api.client.event.RegisterComponentRenderersEvent;
import io.karma.pda.api.client.render.app.AppRenderer;
import io.karma.pda.api.client.render.app.AppRenderers;
import io.karma.pda.api.client.render.component.ComponentRenderer;
import io.karma.pda.api.client.render.component.ComponentRenderers;
import io.karma.pda.api.util.Constants;
import io.karma.pda.foundation.client.render.component.*;
import io.karma.pda.foundation.component.DefaultComponents;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.render.app.DefaultAppRenderer;
import io.karma.pda.mod.client.render.entity.DockBlockEntityRenderer;
import io.karma.pda.mod.client.render.model.BakedDockModel;
import io.karma.pda.mod.client.util.BakedQuadUtils;
import io.karma.pda.mod.init.ModBlockEntities;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.ModifyBakingResult;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();
    public static final ResourceLocation PDA_V = new ResourceLocation(Constants.MODID, "item/pda_v");
    public static final ResourceLocation PDA_H = new ResourceLocation(Constants.MODID, "item/pda_h");
    public static final ResourceLocation PDA_FULLBRIGHT = new ResourceLocation(Constants.MODID, "item/pda_fullbright");
    public static final ResourceLocation DOCK_FULLBRIGHT = new ResourceLocation(Constants.MODID,
        "block/dock_fullbright");

    private float frameTime;
    private int clientTick;

    // @formatter:off
    private ClientEventHandler() {}
    // @formatter:on

    private static BakedQuad transformDockButtonQuad(final BakedQuad quad, final Direction orientation) {
        // @formatter:off
        final var rotation = orientation.getAxis() == Axis.Z
            ? orientation.getOpposite()
            : orientation;
        return QuadTransformers.applyingLightmap(LightTexture.FULL_BRIGHT)
            .andThen(BakedQuadUtils.applyRotation(rotation))
            // Stolen from item models to save space, need to offset by 3 pixels up
            .andThen(BakedQuadUtils.applyTranslation(new Vector3f(0F, 3F / 16F, 0F)))
            .process(quad);
        // @formatter:on
    }

    private static BakedQuad transformDockQuad(final BakedQuad quad, final Direction orientation) {
        // @formatter:off
        return QuadTransformers.applyingLightmap(LightTexture.FULL_BRIGHT)
            .andThen(BakedQuadUtils.applyRotation(orientation))
            .process(quad);
        // @formatter:on
    }

    @Internal
    public void setup() {
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final var forgeBus = MinecraftForge.EVENT_BUS;
        modBus.addListener(this::onRegisterEntityRenderers);
        modBus.addListener(this::onRegisterAdditionalModels);
        modBus.addListener(this::onModifyBakingResult);
        forgeBus.addListener(this::onRenderTick);
        forgeBus.addListener(this::onClientTick);
    }

    @SuppressWarnings("all")
    @Internal
    public void fireRegisterEvents() {
        // Components
        PDAMod.LOGGER.info("Registering component renderers");
        ComponentRenderers.register(DefaultComponents.CONTAINER, new ContainerRenderer());
        ComponentRenderers.register(DefaultComponents.TEXT, new TextRenderer());
        ComponentRenderers.register(DefaultComponents.BUTTON, new ButtonRenderer());
        ComponentRenderers.register(DefaultComponents.IMAGE, new ImageRenderer());
        ComponentRenderers.register(DefaultComponents.SPACER, new SpacerRenderer());
        ComponentRenderers.register(DefaultComponents.ITEM_IMAGE, new ItemImageRenderer());
        ComponentRenderers.register(DefaultComponents.BLOCK_IMAGE, new BlockImageRenderer());
        ComponentRenderers.register(DefaultComponents.ENTITY_IMAGE, new EntityImageRenderer());
        ComponentRenderers.register(DefaultComponents.PLAYER_IMAGE, new PlayerImageRenderer());
        ComponentRenderers.register(DefaultComponents.RECIPE_IMAGE, new RecipeImageRenderer());
        ComponentRenderers.register(DefaultComponents.SPINNER, new SpinnerRenderer());
        ComponentRenderers.register(DefaultComponents.BOX, new BoxRenderer());
        // Apps
        PDAMod.LOGGER.info("Registering app renderers");
        AppRenderers.register(DefaultApps.LAUNCHER, new DefaultAppRenderer<>());
        AppRenderers.register(DefaultApps.SETTINGS, new DefaultAppRenderer<>());

        final var forgeBus = MinecraftForge.EVENT_BUS;
        // @formatter:off
        forgeBus.post(new RegisterComponentRenderersEvent(
            (type, renderer) -> ComponentRenderers.register((ComponentType<Component>)type, (ComponentRenderer<Component>) renderer)));
        forgeBus.post(new RegisterAppRenderersEvent(
            (type, renderer) -> AppRenderers.register((AppType<App>)type, (AppRenderer<App>) renderer)));
        // @formatter:on
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
        event.register(PDA_V);
        event.register(PDA_H);
        event.register(PDA_FULLBRIGHT);
        event.register(DOCK_FULLBRIGHT);
    }

    // Splice together multipart models after baking
    private void onModifyBakingResult(final ModifyBakingResult event) {
        PDAMod.LOGGER.debug("Splicing baked multipart models");
        final var models = event.getModels();
        spliceDockModels(models);
    }

    private void spliceDockModels(final Map<ResourceLocation, BakedModel> models) {
        final var pdaFullBright = models.get(PDA_FULLBRIGHT);
        for (final var orientation : Direction.values()) {
            if (orientation.getAxis().isVertical()) {
                continue;
            }
            final var orientationName = orientation.getName();
            // @formatter:off
            // Compute empty models
            models.compute(new ModelResourceLocation(Constants.MODID,
                    "dock",
                    String.format("has_item=false,orientation=%s", orientationName)),
                (k, model) -> new BakedDockModel(List.of(
                    Pair.of(model, q -> q),
                    Pair.of(models.get(DOCK_FULLBRIGHT), q -> BakedQuadUtils.applyRotation(orientation).process(q))
                )));
            // Compute populated models
            models.compute(new ModelResourceLocation(Constants.MODID,
                    "dock",
                    String.format("has_item=true,orientation=%s", orientationName)),
                (k, model) -> new BakedDockModel(List.of(
                    Pair.of(model, q -> q),
                    Pair.of(pdaFullBright, q -> transformDockButtonQuad(q, orientation)),
                    Pair.of(models.get(DOCK_FULLBRIGHT), q -> transformDockQuad(q, orientation))
                )));
            // @formatter:on
        }
    }
}

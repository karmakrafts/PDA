/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.client;

import com.mojang.blaze3d.vertex.Tesselator;
import io.karma.pda.api.client.event.RegisterAppRenderersEvent;
import io.karma.pda.api.client.event.RegisterComponentRenderersEvent;
import io.karma.pda.api.client.render.AppRenderer;
import io.karma.pda.api.client.render.AppRenderers;
import io.karma.pda.api.client.render.ComponentRenderer;
import io.karma.pda.api.client.render.ComponentRenderers;
import io.karma.pda.api.common.app.App;
import io.karma.pda.api.common.app.AppType;
import io.karma.pda.api.common.app.component.Component;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.client.render.entity.DockBlockEntityRenderer;
import io.karma.pda.client.screen.DockScreen;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.block.DockBlock;
import io.karma.pda.common.init.ModBlockEntities;
import io.karma.pda.common.util.BezierCurve;
import io.karma.pda.common.util.Easings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();
    public static final ResourceLocation PDA_MODEL_V = new ResourceLocation(Constants.MODID, "item/pda_v");
    public static final ResourceLocation PDA_MODEL_H = new ResourceLocation(Constants.MODID, "item/pda_h");
    private static final int ANIMATION_TICKS = 12;
    private static final HashSet<ResourceLocation> INVISIBLE_OVERLAYS = new HashSet<>();

    private float frameTime;
    private int clientTick;

    private boolean isDockEngaged;
    private boolean isAnimating;
    private int animationTick;
    private final Vector3f srcCameraPos = new Vector3f();
    private final Quaternionf srcCameraRotation = new Quaternionf();
    private final Vector3f dstCameraPos = new Vector3f();
    private final Quaternionf dstCameraRotation = new Quaternionf();
    private final BezierCurve cameraCurve = new BezierCurve(ANIMATION_TICKS);
    private boolean usesCameraCurve;
    // Frame interpolation variables
    private final Vector3f cameraPos = new Vector3f();
    private final Vector3f lastCameraPos = new Vector3f();
    private final Quaternionf cameraRotation = new Quaternionf();
    private final Quaternionf lastCameraRotation = new Quaternionf();
    // For debug lines
    private static final int LINE_TICKS = 10 * 20;
    private int lineTick = 0;

    static {
        INVISIBLE_OVERLAYS.add(VanillaGuiOverlay.HOTBAR.id());
        INVISIBLE_OVERLAYS.add(VanillaGuiOverlay.PLAYER_HEALTH.id());
        INVISIBLE_OVERLAYS.add(VanillaGuiOverlay.FOOD_LEVEL.id());
        INVISIBLE_OVERLAYS.add(VanillaGuiOverlay.CROSSHAIR.id());
    }

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
        forgeBus.addListener(this::onRenderGuiOverlay);
        forgeBus.addListener(this::onRenderArm);
        forgeBus.addListener(this::onComputeCameraAngles);
        if (PDAMod.IS_DEV_ENV) {
            forgeBus.addListener(this::onRenderLevelStage);
        }
    }

    public void engageDock(final BlockPos pos) {
        final var game = Minecraft.getInstance();
        final var world = game.level;
        if (world == null) {
            return;
        }

        // Store the current camera transform
        final var camera = game.gameRenderer.getMainCamera();
        srcCameraPos.set(camera.getPosition().toVector3f());
        cameraPos.set(srcCameraPos);
        lastCameraPos.set(cameraPos);

        // Calculate the destination position
        final var state = world.getBlockState(pos);
        final var direction = state.getValue(DockBlock.ORIENTATION);
        final var actualDirection = direction.getDirection();
        final var normal = actualDirection.getNormal();

        // Find out which camera curve to use
        final var fNormal = new Vector3f(normal.getX(), normal.getY(), normal.getZ());
        final var displayToCameraAngle = (float) Math.toDegrees(camera.forwards.angleSigned(fNormal,
            new Vector3f(0F, 1F, 0F)));
        final var isPositiveCameraAngle = displayToCameraAngle > 90F;
        usesCameraCurve = isPositiveCameraAngle || displayToCameraAngle < -90F;

        // Compute the destination point
        final var xOffset = (float) normal.getX() * 0.75F;
        final var zOffset = (float) normal.getZ() * 0.75F;
        final var x = (float) pos.getX() + 0.5F - xOffset;
        final var y = (float) pos.getY() + 0.5F;
        final var z = (float) pos.getZ() + 0.5F - zOffset;
        dstCameraPos.set(x, y, z);

        // Only recalculate the bezier curve when needed
        if (usesCameraCurve) {
            final var points = new ArrayList<Vector3f>();
            points.add(new Vector3f());

            // Shared constants
            final var sourceOffset = srcCameraPos.sub(dstCameraPos, new Vector3f());
            final var cpY = sourceOffset.y * 0.5F;

            // First control point
            var cpX = -(float) normal.getX() * 3F;
            var cpZ = -(float) normal.getZ() * 3F;
            points.add(new Vector3f(cpX, cpY, cpZ));

            // Second control point
            // @formatter:off
            final var rotatedDir = isPositiveCameraAngle
                ? actualDirection.getCounterClockWise()
                : actualDirection.getClockWise();
            // @formatter:on
            final var rotatedNormal = rotatedDir.getNormal();
            cpX = (float) rotatedNormal.getX() * 3F;
            cpZ = (float) rotatedNormal.getZ() * 3F;
            points.add(new Vector3f(cpX, cpY, cpZ));

            // Add the source camera pos offset
            points.add(sourceOffset);
            cameraCurve.setPoints(points.toArray(Vector3f[]::new));
        }

        // Update the source rotation..
        srcCameraRotation.identity();
        srcCameraRotation.rotationYXZ((float) Math.toRadians(camera.yRot), (float) Math.toRadians(camera.xRot), 0F);
        final var yAngle = switch (direction) {
            case NORTH -> 180F;
            case EAST -> 270F;
            case WEST -> 90F;
            default -> 0F;
        };
        cameraRotation.set(srcCameraRotation);
        lastCameraRotation.set(cameraRotation);
        // ..and the destination rotation
        dstCameraRotation.identity();
        dstCameraRotation.rotationYXZ((float) Math.toRadians(yAngle), 0F, 0F);

        isDockEngaged = true;
    }

    public void disengageDock() {
        isDockEngaged = false;
        // For debug lines
        lineTick = LINE_TICKS;
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
    }

    private void onRenderLevelStage(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || !Minecraft.getInstance().options.renderDebug) {
            return;
        }
        if (lineTick > 0) {
            final var poseStack = event.getPoseStack();
            final var cameraPos = event.getCamera().getPosition();
            final var source = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            var matrix = poseStack.last().pose();
            final var normalMatrix = poseStack.last().normal();
            final var alpha = (int) (Easings.easeOutQuart((float) lineTick / LINE_TICKS) * 255F);

            if (usesCameraCurve) {
                poseStack.pushPose();
                poseStack.translate(dstCameraPos.x, dstCameraPos.y, dstCameraPos.z);
                matrix = poseStack.last().pose();
                final var buffer = source.getBuffer(RenderType.LINE_STRIP);
                for (var i = 0; i < cameraCurve.getSampleCount(); i++) {
                    final var point = cameraCurve.getSample(i);
                    buffer.vertex(matrix,
                        point.x,
                        point.y,
                        point.z).color(0x00FF00 | (alpha << 24)).normal(normalMatrix, 0F, 1F, 0F).endVertex();
                }
                poseStack.popPose();
            }
            else {
                final var buffer = source.getBuffer(RenderType.LINES);
                buffer.vertex(matrix,
                    srcCameraPos.x,
                    srcCameraPos.y,
                    srcCameraPos.z).color(0xFFFF00 | (alpha << 24)).normal(normalMatrix, 0F, 1F, 0F).endVertex();
                buffer.vertex(matrix,
                    dstCameraPos.x,
                    dstCameraPos.y,
                    dstCameraPos.z).color(0xFFFF00 | (alpha << 24)).normal(normalMatrix, 0F, 1F, 0F).endVertex();
            }

            source.endLastBatch();
            poseStack.popPose();
        }
    }

    private boolean isDockAnimationActive() {
        return isDockEngaged || isAnimating;
    }

    private void onRenderArm(final RenderArmEvent event) {
        event.setCanceled(isDockAnimationActive());
    }

    private void onRenderGuiOverlay(final RenderGuiOverlayEvent.Pre event) {
        event.setCanceled(isDockAnimationActive() && INVISIBLE_OVERLAYS.contains(event.getOverlay().id()));
    }

    private Vector3f getCameraPos(final float frameTime) {
        if (!isAnimating) {
            return cameraPos;
        }
        return lastCameraPos.lerp(cameraPos, frameTime, new Vector3f());
    }

    private Quaternionf getCameraRotation(final float frameTime) {
        if (!isAnimating) {
            return cameraRotation;
        }
        return lastCameraRotation.nlerp(cameraRotation, frameTime, new Quaternionf());
    }

    private void onComputeCameraAngles(final ViewportEvent.ComputeCameraAngles event) {
        if (isDockAnimationActive()) {
            final var frameTime = (float) event.getPartialTick();
            event.getCamera().setPosition(new Vec3(getCameraPos(frameTime)));
            final var rot = getCameraRotation(frameTime).getEulerAnglesYXZ(new Vector3f());
            event.setYaw((float) Math.toDegrees(rot.y));
            event.setPitch((float) Math.toDegrees(rot.x));
        }
    }

    private void updateAnimationParameters() {
        // Update the actual animation parameters
        final var factor = (float) animationTick / (ANIMATION_TICKS - 1);
        // If we want to use the bezier, calculate forward vector of each segment to adjust camera along the curve
        if (usesCameraCurve) {
            final var pos = cameraCurve.getSample(ANIMATION_TICKS - 1 - animationTick);
            lastCameraPos.set(cameraPos);
            cameraPos.set(pos.add(dstCameraPos, new Vector3f()));
        }
        else {
            // Otherwise we just linearly interpolate our position
            lastCameraPos.set(cameraPos);
            cameraPos.set(0F, 0F, 0F);
            srcCameraPos.lerp(dstCameraPos, factor, cameraPos);
        }
        lastCameraRotation.set(cameraRotation);
        cameraRotation.identity();
        srcCameraRotation.nlerp(dstCameraRotation, factor, cameraRotation);
    }

    private void updateCameraAnimation() {
        final var game = Minecraft.getInstance();
        if (isDockEngaged) {
            if (animationTick < (ANIMATION_TICKS - 1)) {
                isAnimating = true;
                animationTick++;
                updateAnimationParameters();
            }
            else {
                game.pauseGame(false); // Unpause game after animation is done playing
                isAnimating = false;
            }
        }
        else {
            if (animationTick > 0) {
                isAnimating = true;
                animationTick--;
                updateAnimationParameters();
            }
            else {
                if (game.screen instanceof DockScreen) {
                    game.popGuiLayer();
                }
                isAnimating = false;
            }
        }
    }

    private void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateCameraAnimation();
            if (lineTick > 0) {
                lineTick--;
            }
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

package io.karma.pda.client;

import codechicken.lib.math.MathHelper;
import com.mojang.blaze3d.vertex.Tesselator;
import io.karma.pda.client.screen.DockScreen;
import io.karma.pda.common.block.DockBlock;
import io.karma.pda.common.util.BezierCurve;
import io.karma.pda.common.util.Easings;
import io.karma.pda.common.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 13/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DockInteractionHandler {
    public static final DockInteractionHandler INSTANCE = new DockInteractionHandler();
    private static final HashSet<ResourceLocation> OVERLAY_WHITELIST = new HashSet<>();
    private static final int ANIMATION_TICKS = 12;

    private boolean isDockEngaged;
    private boolean isAnimating;
    private int animationTick;
    private float yAngle;
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
        OVERLAY_WHITELIST.add(VanillaGuiOverlay.VIGNETTE.id());
        OVERLAY_WHITELIST.add(VanillaGuiOverlay.CROSSHAIR.id());
    }

    // @formatter:off
    private DockInteractionHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        final var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRenderGuiOverlay);
        forgeBus.addListener(this::onRenderArm);
        forgeBus.addListener(this::onRenderHand);
        forgeBus.addListener(this::onComputeCameraAngles);
        forgeBus.addListener(this::onClientTick);
        forgeBus.addListener(this::onEntityTeleport);
        forgeBus.addListener(this::onLivingDeath);
        forgeBus.addListener(this::onLivingDamage);
        forgeBus.addListener(this::onPlayerChangeDimension);
        forgeBus.addListener(this::onRenderLevelStage);
    }

    public void engage(final BlockPos pos) {
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
        final var normalFactor = 0.4F + ((80F - ((float) game.options.fov().get() - 30F)) / 80F);
        final var xOffset = (float) normal.getX() * normalFactor;
        final var zOffset = (float) normal.getZ() * normalFactor;
        final var x = (float) pos.getX() + 0.5F - xOffset;
        final var y = (float) pos.getY() + 0.56F;
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
        yAngle = switch (direction) {
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

    public void disengage() {
        isDockEngaged = false;
        // For debug lines
        lineTick = LINE_TICKS;
    }

    private void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            updateAnimation();
            if (lineTick > 0) {
                lineTick--;
            }
        }
    }

    private void onRenderLevelStage(final RenderLevelStageEvent event) {
        final var stage = event.getStage();
        if (stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        final var poseStack = event.getPoseStack();
        final var cameraPos = event.getCamera().getPosition();
        final var source = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        var matrix = poseStack.last().pose();
        var normalMatrix = poseStack.last().normal();

        if (Minecraft.getInstance().options.renderDebug && lineTick > 0) {
            final var alpha = (int) (Easings.easeOutQuart((float) lineTick / LINE_TICKS) * 255F);

            if (usesCameraCurve) {
                poseStack.pushPose();
                poseStack.translate(dstCameraPos.x, dstCameraPos.y, dstCameraPos.z);
                matrix = poseStack.last().pose();
                normalMatrix = poseStack.last().normal();
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
        }

        source.endBatch();
        poseStack.popPose();
    }

    private void onLivingDeath(final LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void onLivingDamage(final LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (PlayerUtils.isSame(event.getEntity(), Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void onEntityTeleport(final EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PlayerUtils.isSame(player, Objects.requireNonNull(Minecraft.getInstance().player))) {
            resetAnimation();
        }
    }

    private void resetAnimation() {
        final var game = Minecraft.getInstance();
        game.execute(() -> {
            isDockEngaged = false;
            isAnimating = false;
            animationTick = 0;
            if (game.screen instanceof DockScreen) {
                game.popGuiLayer();
            }
        });
    }

    private boolean isSequenceActive() {
        return isDockEngaged || isAnimating;
    }

    private void onRenderArm(final RenderArmEvent event) {
        event.setCanceled(isSequenceActive());
    }

    private void onRenderHand(final RenderHandEvent event) {
        event.setCanceled(isSequenceActive());
    }

    private void onRenderGuiOverlay(final RenderGuiOverlayEvent.Pre event) {
        event.setCanceled(isSequenceActive() && !OVERLAY_WHITELIST.contains(event.getOverlay().id()));
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
        if (isSequenceActive()) {
            final var frameTime = (float) event.getPartialTick();
            event.getCamera().setPosition(new Vec3(getCameraPos(frameTime)));
            final var rot = getCameraRotation(frameTime).getEulerAnglesYXZ(new Vector3f());
            event.setYaw((float) Math.toDegrees(rot.y));
            event.setPitch((float) Math.toDegrees(rot.x));
        }
    }

    private void updateAnimationParameters() {
        final var factor = (float) animationTick / (ANIMATION_TICKS - 1);
        if (usesCameraCurve) {
            final var pos = cameraCurve.getSample(ANIMATION_TICKS - 1 - animationTick);
            lastCameraPos.set(cameraPos);
            cameraPos.set(pos.add(dstCameraPos, new Vector3f()));
        }
        else {
            lastCameraPos.set(cameraPos);
            cameraPos.set(0F, 0F, 0F);
            srcCameraPos.lerp(dstCameraPos, factor, cameraPos);
        }
        lastCameraRotation.set(cameraRotation);
        cameraRotation.identity();
        srcCameraRotation.nlerp(dstCameraRotation, factor, cameraRotation);
    }

    private void updateHeadTilt() {
        final var game = Minecraft.getInstance();
        final var player = game.player;
        if (player == null) {
            return;
        }
        final var window = game.getWindow();
        try (final var stack = MemoryStack.stackPush()) {
            final var mouseX = stack.mallocDouble(1);
            final var mouseY = stack.mallocDouble(1);
            GLFW.glfwGetCursorPos(window.getWindow(), mouseX, mouseY);
            final var normalizedMouseX = (float) (mouseX.get() / window.getWidth());
            final var normalizedMouseY = (float) (mouseY.get() / window.getHeight());
            final var ndcMouseX = (normalizedMouseX * 2F - 1F);
            final var yaw = yAngle + MathHelper.clip(ndcMouseX * 90F, -25F, 25F);
            final var ndcMouseY = (normalizedMouseY * 2F - 1F);
            final var pitch = MathHelper.clip(ndcMouseY * 90F, -25F, 25F);
            lastCameraRotation.set(cameraRotation);
            cameraRotation.rotationYXZ((float) Math.toRadians(yaw), (float) Math.toRadians(pitch), 0F);
        }
    }

    private void updateAnimation() {
        if (isDockEngaged) {
            if (animationTick < (ANIMATION_TICKS - 1)) {
                isAnimating = true;
                animationTick++;
                updateAnimationParameters();
            }
            else {
                isAnimating = false;
                updateHeadTilt();
            }
        }
        else {
            if (animationTick > 0) {
                isAnimating = true;
                animationTick--;
                updateAnimationParameters();
            }
            else {
                resetAnimation();
            }
        }
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.mixin;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.hook.ExtendedRenderSystem;
import io.karma.pda.mod.client.hook.ExtendedShader;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin implements ExtendedRenderSystem {
    private static RenderSystem pda$instance; // This is non-unique on purpose since we reflect this
    @Unique
    private ExtendedShader pda$extendedShader;

    @Shadow
    public static boolean isOnRenderThread() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public static void recordRenderCall(final RenderCall call) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("all")
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClInit(final CallbackInfo cbi) {
        new RenderSystem();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(final CallbackInfo cbi) {
        pda$instance = RenderSystem.class.cast(this);
        PDAMod.LOGGER.debug("Created extended RenderSystem instance");
    }

    @Inject(method = "setShader", at = @At("HEAD"))
    private static void onSetShader(final Supplier<ShaderInstance> shader, final CallbackInfo cbi) {
        if (isOnRenderThread()) {
            RenderSystemMixin.class.cast(pda$instance).pda$extendedShader = (ExtendedShader) shader.get();
        }
        else {
            recordRenderCall(() -> {
                RenderSystemMixin.class.cast(pda$instance).pda$extendedShader = (ExtendedShader) shader.get();
            });
        }
    }

    @Override
    public void setExtendedShader(final Supplier<ExtendedShader> shader) {
        if (isOnRenderThread()) {
            pda$extendedShader = shader.get();
            return;
        }
        recordRenderCall(() -> {
            pda$extendedShader = shader.get();
        });
    }

    @Override
    public ExtendedShader getExtendedShader() {
        return pda$extendedShader;
    }
}

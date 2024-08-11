/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;
import io.karma.pda.mod.client.hook.ExtendedRenderSystem;
import io.karma.pda.mod.client.hook.ExtendedVertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@Mixin(BufferUploader.class)
public abstract class BufferUploaderMixin {
    @Shadow
    private static @Nullable VertexBuffer upload(RenderedBuffer pBuffer) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "_drawWithShader", at = @At("HEAD"), cancellable = true)
    private static void onDrawWithShader(final RenderedBuffer buffer, final CallbackInfo cbi) {
        final var uploadedBuffer = upload(buffer);
        if (uploadedBuffer == null) {
            cbi.cancel();
            return;
        }
        final var shader = ExtendedRenderSystem.getInstance().getExtendedShader();
        if (shader instanceof ShaderInstance) {
            uploadedBuffer.drawWithShader(RenderSystem.getModelViewMatrix(),
                RenderSystem.getProjectionMatrix(),
                (ShaderInstance) shader);
            cbi.cancel();
            return;
        }
        ((ExtendedVertexBuffer) uploadedBuffer).drawWithExtendedShader(RenderSystem.getProjectionMatrix(),
            RenderSystem.getProjectionMatrix(),
            shader);
        cbi.cancel();
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import io.karma.pda.mod.client.hook.ExtendedShader;
import io.karma.pda.mod.client.hook.ExtendedVertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin implements ExtendedVertexBuffer {
    @Shadow
    public abstract void drawWithShader(Matrix4f pModelViewMatrix, Matrix4f pProjectionMatrix, ShaderInstance pShader);

    @Shadow
    public abstract void draw();

    @Unique
    private void _drawWithExtendedShader(final Matrix4f mvm, final Matrix4f pm, final ExtendedShader shader) {
        shader.apply();
        draw();
        shader.clear();
    }

    @Override
    public void drawWithExtendedShader(final Matrix4f mvm, final Matrix4f pm, final ExtendedShader shader) {
        if (shader instanceof ShaderInstance) {
            // Delegate to vanilla method for ShaderInstances to retain mod compat
            drawWithShader(mvm, pm, (ShaderInstance) shader);
            return;
        }
        if (RenderSystem.isOnRenderThread()) {
            _drawWithExtendedShader(mvm, pm, shader);
            return;
        }
        RenderSystem.recordRenderCall(() -> _drawWithExtendedShader(mvm, pm, shader));
    }
}

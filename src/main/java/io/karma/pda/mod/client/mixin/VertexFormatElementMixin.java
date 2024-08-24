/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.mixin;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormatElement.Type;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;
import io.karma.pda.api.util.HashUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Alexander Hinze
 * @since 25/08/2024
 */
@Mixin(VertexFormatElement.class)
public final class VertexFormatElementMixin {
    @Shadow
    @Final
    private Type type;
    @Shadow
    @Final
    private Usage usage;
    @Shadow
    @Final
    private int index;
    @Shadow
    @Final
    private int count;

    // Make the hashCode of VFEs reproducible, so we can use it for FS caching
    @Inject(method = "hashCode", at = @At("HEAD"), cancellable = true)
    private void onHashCode(final CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(HashUtils.combine(type.ordinal(), usage.ordinal(), index, count));
        cir.cancel();
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.mixin;

import io.karma.pda.mod.client.hook.ExtendedShader;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Alexander Hinze
 * @since 11/08/2024
 */
@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin implements ExtendedShader {
    // This only injects the interface, nothing to see here
}

package io.karma.pda.client.mixin;

import io.karma.pda.client.event.RunTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@Mixin(Minecraft.class)
public final class MinecraftMixin {
    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    private void onRunTickPre(final boolean renderLevel, final CallbackInfo cbi) {
        final var event = new RunTickEvent.Pre(renderLevel);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cbi.cancel();
        }
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onRunTickPost(final boolean renderLevel, final CallbackInfo cbi) {
        MinecraftForge.EVENT_BUS.post(new RunTickEvent.Post(renderLevel));
    }
}

package io.karma.pda.common.mixin;

import io.karma.pda.common.hook.MutableClipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Alexander Hinze
 * @since 18/03/2024
 */
@Mixin(ClipContext.class)
public final class ClipContextMixin implements MutableClipContext {
    @Shadow
    public Vec3 from;
    @Shadow
    public Vec3 to;
    @Shadow
    public ClipContext.Block block;
    @Shadow
    public ClipContext.Fluid fluid;
    @Shadow
    public CollisionContext collisionContext;

    @Override
    public void setFrom(final float x, final float y, final float z) {
        from = new Vec3(x, y, z);
    }

    @Override
    public void setTo(final float x, final float y, final float z) {
        to = new Vec3(x, y, z);
    }

    @Override
    public void setBlock(final ClipContext.Block block) {
        this.block = block;
    }

    @Override
    public void setFluid(final ClipContext.Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public void setCollisionContext(final CollisionContext collisionContext) {
        this.collisionContext = collisionContext;
    }
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.hook;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * @author Alexander Hinze
 * @since 18/03/2024
 */
public interface MutableClipContext {
    void setFrom(final float x, final float y, final float z);

    void setTo(final float x, final float y, final float z);

    void setBlock(final ClipContext.Block block);

    void setFluid(final ClipContext.Fluid fluid);

    void setCollisionContext(final CollisionContext collisionContext);
}

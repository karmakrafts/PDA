/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * @author Alexander Hinze
 * @since 17/03/2024
 */
public final class MathUtils {
    public static final Vector3f X_POS = new Vector3f(1F, 0F, 0F);
    public static final Vector3f Y_POS = new Vector3f(0F, 1F, 0F);
    public static final Vector3f Z_POS = new Vector3f(0F, 0F, 1F);
    public static final Vector3f X_NEG = new Vector3f(-1F, 0F, 0F);
    public static final Vector3f Y_NEG = new Vector3f(0F, -1F, 0F);
    public static final Vector3f Z_NEG = new Vector3f(0F, 0F, -1F);
    public static final Vector3f CENTER = new Vector3f(0.5F);
    public static final Vector3f CENTER_NEG = new Vector3f(-0.5F);

    // @formatter:off
    private MathUtils() {}
    // @formatter:on

    public static Vector3f toVector3f(final Vec3 value) {
        return new Vector3f((float) value.x, (float) value.y, (float) value.z);
    }
}

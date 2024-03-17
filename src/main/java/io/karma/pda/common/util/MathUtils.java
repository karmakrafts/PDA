package io.karma.pda.common.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * @author Alexander Hinze
 * @since 17/03/2024
 */
public final class MathUtils {
    // @formatter:off
    private MathUtils() {}
    // @formatter:on

    public static Vector3f toVector3f(final Vec3 value) {
        return new Vector3f((float) value.x, (float) value.y, (float) value.z);
    }

    public static Vec3 toVec3(final Vector3f value) {
        return new Vec3(value.x, value.y, value.z);
    }
}

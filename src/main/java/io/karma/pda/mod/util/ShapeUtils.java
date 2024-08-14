/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class ShapeUtils {
    // @formatter:off
    private ShapeUtils() {}
    // @formatter:on

    public static AABB makeAABB(final Vector3f min, final Vector3f max) {
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static VoxelShape rotate(final VoxelShape shape,
                                    final Vector3f axis,
                                    final Vector3f origin,
                                    final float angle) {
        final var matrix = new Matrix4f().rotate((float) Math.toRadians(angle), axis);
        final var boxes = new ArrayList<AABB>();
        shape.forAllBoxes((double minX, double minY, double minZ, double maxX, double maxY, double maxZ) -> {
            final var min = new Vector3f((float) minX, (float) minY, (float) minZ).sub(origin);
            matrix.transformPosition(min);
            min.add(origin);
            final var max = new Vector3f((float) maxX, (float) maxY, (float) maxZ).sub(origin);
            matrix.transformPosition(max);
            max.add(origin);
            boxes.add(makeAABB(min, max));
        });
        return boxes.stream().map(Shapes::create).reduce(Shapes::or).orElseThrow().optimize();
    }
}

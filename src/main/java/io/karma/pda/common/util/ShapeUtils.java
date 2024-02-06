package io.karma.pda.common.util;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class ShapeUtils {
    // @formatter:off
    private ShapeUtils() {}
    // @formatter:on

    public static VoxelShape rotate(final VoxelShape shape, final Vector3 axis, final Vector3 origin,
                                    final float angle) {
        final var boxes = new ArrayList<Cuboid6>();
        shape.forAllBoxes((double minX, double minY, double minZ, double maxX, double maxY, double maxZ) -> {
            final var cuboid = new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);
            cuboid.apply(new Rotation(angle * MathHelper.torad, axis).at(origin));
            boxes.add(cuboid);
        });
        var result = Shapes.empty();
        for (final var box : boxes) {
            result = Shapes.or(result, box.shape());
        }
        return result;
    }
}

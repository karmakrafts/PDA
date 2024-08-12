/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.util;

import com.mojang.math.Transformation;
import io.karma.pda.api.util.MathUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Alexander Hinze
 * @since 12/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class BakedQuadUtils {
    // @formatter:off
    private BakedQuadUtils() {}
    // @formatter:on

    public static void transformQuads(final List<BakedQuad> source, final List<BakedQuad> destination,
                                      final Function<BakedQuad, BakedQuad> transform) {
        for (final var quad : source) {
            destination.add(transform.apply(quad));
        }
    }

    public static IQuadTransformer applyTransform(final Consumer<Matrix4f> callback) {
        final var matrix = new Matrix4f().identity();
        callback.accept(matrix);
        return QuadTransformers.applying(new Transformation(matrix));
    }

    public static IQuadTransformer applyRotation(final float angle, final Vector3f axis, final Vector3f origin) {
        return applyTransform(matrix -> {
            matrix.translate(origin);
            matrix.rotate((float) Math.toRadians(angle), axis);
            matrix.translate(origin.negate(new Vector3f()));
        });
    }

    public static IQuadTransformer applyRotation(final Direction direction) {
        return applyRotation(direction.toYRot(), MathUtils.Y_POS, MathUtils.CENTER);
    }

    public static IQuadTransformer applyTranslation(final Vector3f translation) {
        return applyTransform(matrix -> matrix.translate(translation));
    }
}

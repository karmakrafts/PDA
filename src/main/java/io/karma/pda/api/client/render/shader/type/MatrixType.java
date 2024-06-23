/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * @author Alexander Hinze
 * @since 15/06/2024
 */
@OnlyIn(Dist.CLIENT)
public enum MatrixType implements Type {
    // @formatter:off
    MAT2(new Matrix2f(), ScalarType.FLOAT, 2, 2),
    MAT3(new Matrix3f(), ScalarType.FLOAT, 3, 3),
    MAT4(new Matrix4f(), ScalarType.FLOAT, 4, 4);
    // @formatter:on

    private final Object defaultValue;
    private final Type type;
    private final int componentCount;

    MatrixType(final Object defaultValue, final Type type, final int width, final int height) {
        this.defaultValue = defaultValue;
        this.type = type;
        componentCount = width * height;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public int getComponentSize() {
        return type.getComponentSize();
    }

    @Override
    public int getComponentCount() {
        return componentCount;
    }
}

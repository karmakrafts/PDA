/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.client.render.shader.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

/**
 * @author Alexander Hinze
 * @since 15/06/2024
 */
@OnlyIn(Dist.CLIENT)
public enum VectorType implements Type {
    // @formatter:off
    VEC2 (new Vector2f(), ScalarType.FLOAT, 2),
    VEC3 (new Vector3f(), ScalarType.FLOAT, 3),
    VEC4 (new Vector4f(), ScalarType.FLOAT, 4),
    IVEC2(new Vector2i(), ScalarType.INT,   2),
    IVEC3(new Vector3i(), ScalarType.INT,   3),
    IVEC4(new Vector4i(), ScalarType.INT,   4),
    BVEC2(new Vector2b(), ScalarType.BOOL,  2),
    BVEC3(new Vector3b(), ScalarType.BOOL,  3),
    BVEC4(new Vector4b(), ScalarType.BOOL,  4);
    // @formatter:on

    private final Object defaultValue;
    private final Type type;
    private final int componentCount;

    VectorType(final Object defaultValue, final Type type, final int componentCount) {
        this.defaultValue = defaultValue;
        this.type = type;
        this.componentCount = componentCount;
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

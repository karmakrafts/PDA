/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
final class ZeroFlexValue implements FlexValue {
    static final ZeroFlexValue INSTANCE = new ZeroFlexValue(FlexValueType.PIXEL);
    static final ZeroFlexValue AUTO = new ZeroFlexValue(FlexValueType.AUTO);

    private final FlexValueType type;

    private ZeroFlexValue(final FlexValueType type) {
        this.type = type;
    }

    @Override
    public FlexValueType getType() {
        return type;
    }

    @Override
    public float get() {
        return 0F;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ZeroFlexValue zeroValue)) {
            if (!(obj instanceof FlexValue value)) {
                return false;
            }
            return type == value.getType() && value.get() == 0F;
        }
        return type == zeroValue.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}

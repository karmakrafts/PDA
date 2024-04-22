/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
final class DefaultFlexValue implements FlexValue {
    private final FlexValueType type;
    private final float value;

    DefaultFlexValue(final FlexValueType type, final float value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public FlexValueType getType() {
        return type;
    }

    @Override
    public float get() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FlexValue value)) {
            return false;
        }
        return type == value.getType() && this.value == value.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}

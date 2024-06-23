/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.app.theme.font;

/**
 * @author Alexander Hinze
 * @since 20/05/2024
 */
public interface FontVariant extends Font {
    float DEFAULT_SIZE = 16F;

    FontStyle getStyle();

    float getSize();

    FontVariant withStyle(final FontStyle style);

    FontVariant withSize(final float size);

    FontVariant withVar(final String name, final float value);

    default FontVariant derive(final FontStyle style, final float size) {
        return withStyle(style).withSize(size);
    }

    default String getVariantString() { // @formatter:off
        final var builder = new StringBuilder();
        builder.append(getLocation())
            .append('$')
            .append(getStyle())
            .append('$')
            .append(getSize());
        for(final var var : getVariationAxes().object2FloatEntrySet()) {
            builder.append('$')
                .append(var.getKey())
                .append(':')
                .append(var.getFloatValue());
        }
        return builder.toString();
    } // @formatter:on
}

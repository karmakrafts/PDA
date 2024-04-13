/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Alexander Hinze
 * @since 11/04/2024
 */
public final class Color {
    // @formatter:off
    public static final Color BLACK      = new Color(0F, 0F, 0F);
    public static final Color WHITE      = new Color(1F, 1F, 1F);
    public static final Color RED        = new Color(1F, 0F, 0F);
    public static final Color GREEN      = new Color(0F, 1F, 0F);
    public static final Color BLUE       = new Color(0F, 0F, 1F);
    public static final Color LIGHT_BLUE = new Color(0F, 0.4F, 1F);
    public static final Color YELLOW     = new Color(1F, 1F, 0F);
    public static final Color ORANGE     = new Color(1F, 0.6F, 0F);
    public static final Color CYAN       = new Color(0F, 1F, 1F);
    public static final Color MAGENTA    = new Color(1F, 0F, 1F);
    public static final Color PINK       = new Color(1F, 0.7F, 0.7F);
    // @formatter:on

    @JsonProperty
    public final float r;
    @JsonProperty
    public final float g;
    @JsonProperty
    public final float b;
    @JsonProperty
    public final float a;

    @JsonCreator
    public Color(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @JsonIgnore
    public Color(final float r, final float g, final float b) {
        this(r, g, b, 1F);
    }

    @JsonIgnore
    public Color(final byte r, final byte g, final byte b, final byte a) {
        this.r = (float) ((r & 0xFF) / 255.0);
        this.g = (float) ((g & 0xFF) / 255.0);
        this.b = (float) ((b & 0xFF) / 255.0);
        this.a = (float) ((a & 0xFF) / 255.0);
    }

    @JsonIgnore
    public Color(final byte r, final byte g, final byte b) {
        this(r, g, b, (byte) 0xFF);
    }

    public static Color unpackRGB(final int value) {
        return new Color((byte) ((value >> 16) & 0xFF), (byte) ((value >> 8) & 0xFF), (byte) (value & 0xFF), 255);
    }

    public static Color unpackRGBA(final int value) {
        return new Color((byte) ((value >> 24) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) (value & 0xFF));
    }

    public static Color unpackARGB(final int value) {
        return new Color((byte) (value & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 24) & 0xFF));
    }

    @JsonIgnore
    public int packRGB() {
        return (int) (r * 255F) << 16 | (int) (g * 255F) << 8 | (int) (b * 255F);
    }

    @JsonIgnore
    public int packRGBA() {
        return (int) (r * 255F) << 24 | (int) (g * 255F) << 16 | (int) (b * 255F) << 8 | (int) (a * 255F);
    }

    @JsonIgnore
    public int packARGB() {
        return (int) (a * 255F) | (int) (g * 255F) << 16 | (int) (b * 255F) << 8 | (int) (r * 255F) << 24;
    }
}

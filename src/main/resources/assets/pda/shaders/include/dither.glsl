/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

// @formatter:off
const mat4 DEFAULT_DITHER_KERNEL = mat4(
    -4.0, 0.0, -3.0, 1.0,
    2.0, -2.0, 3.0, -1.0,
    -3.0, 1.0, -4.0, 0.0,
    3.0, -1.0, 2.0, -2.0
);
// @formatter:on

const float DEFAULT_DITHER_COLOR_FACTOR = 16.0;
const float DEFAULT_DITHER_FACTOR = 0.005;
const int DITHER_KERNEL_SIZE = 4;

vec4 dither(mat4 kernel, vec4 color, vec2 coord, float colorFactor, float factor) {
    int xIndex = int(coord.x) % DITHER_KERNEL_SIZE;
    int yIndex = int(coord.y) % DITHER_KERNEL_SIZE;
    vec3 result = color.rgb + kernel[xIndex][yIndex] * factor;
    return vec4(floor(result * colorFactor) / colorFactor, color.a);
}

vec4 dither(vec4 color, vec2 coord) {
    return dither(DEFAULT_DITHER_KERNEL, color, coord, DEFAULT_DITHER_COLOR_FACTOR, DEFAULT_DITHER_FACTOR);
}
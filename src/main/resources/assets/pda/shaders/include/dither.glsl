/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

// @formatter:off
const mat4 DITHER_KERNEL = mat4(
    -4.0, 0.0, -3.0, 1.0,
    2.0, -2.0, 3.0, -1.0,
    -3.0, 1.0, -4.0, 0.0,
    3.0, -1.0, 2.0, -2.0
);
// @formatter:on

const float DITHER_COLOR_FACTOR = 16.0;
const float DITHER_FACTOR = 0.005;
const int DITHER_KERNEL_SIZE = 4;

vec4 dither(const vec4 color, const vec2 coord) {
    const int xIndex = int(coord.x) % DITHER_KERNEL_SIZE;
    const int yIndex = int(coord.y) % DITHER_KERNEL_SIZE;
    const vec3 result = color.rgb + DITHER_KERNEL[xIndex][yIndex] * DITHER_FACTOR;
    return vec4(floor(result * DITHER_COLOR_FACTOR) / DITHER_COLOR_FACTOR, color.a);
}
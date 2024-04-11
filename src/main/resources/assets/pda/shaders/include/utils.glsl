/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

// Gold noise taken from https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
float goldNoise(vec2 coord){
    return fract(sin(dot(coord.xy, vec2(12.9898, 78.233))) * 43758.5453) * 2.0 - 1.0;
}

vec4 convertBw(vec4 color){
    float luminance = color.r * 0.21 + color.g * 0.71 + color.b * 0.07;
    luminance = clamp(luminance + 0.1, 0.1, 1.0);// Old LCDs never turn off completely
    return vec4(vec3(luminance), color.a);
}

const float SRGB_EXPONENT = 1.0 / 2.4;
const float SRGB_THRESHOLD = 0.0031308;
const float SRGB_FACTOR = 12.92;
const float SRGB_CURVE_BASE = 1.055;
const float SRGB_CURVE_REMAINDER = 0.055;

vec4 convertSrgb(vec4 color){
    bvec3 mask = lessThan(color.rgb, vec3(SRGB_THRESHOLD));
    vec3 rgb = color.rgb;
    rgb = mix(SRGB_CURVE_BASE * pow(rgb, vec3(SRGB_EXPONENT)) - SRGB_CURVE_REMAINDER, SRGB_FACTOR * rgb, mask);
    return vec4(rgb, color.a);
}
#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "include/utils.glsl"
#include "include/dither.glsl"
#include "include/display_type.glsl"
#include "include/global_uniforms.glsl"
#include "include/print.glsl"

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float GlitchFactor;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

special const int DISPLAY_WIDTH = 256;
special const int DISPLAY_HEIGHT = 288;
special const float GLITCH_RATE = 0.05;
special const float GLITCH_FACTOR = 0.025;
special const int GLITCH_BLOCKS = 64;
special const float PIXEL_FACTOR = 0.06;

const vec2 DISPLAY_RES = vec2(DISPLAY_WIDTH, DISPLAY_HEIGHT);

float getSampleOffset(vec2 coord, float offset, float glitchMultiplier) {
    return goldNoise(vec2(Time * (GLITCH_RATE * 0.1 * glitchMultiplier) + offset,
    floor(coord.y * GLITCH_BLOCKS) - (offset * 0.5)));
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    float glitchMultiplier = 1.0 + (GlitchFactor * 120.0);
    float glitchFactor = GLITCH_FACTOR * glitchMultiplier;
    inputColor.r = (texture(Sampler0, texCoord0
        + vec2(getSampleOffset(texCoord0, 0.0, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0
        + vec2(getSampleOffset(texCoord0, 0.1, glitchMultiplier) * 0.03 * 0.166, 0.0) * glitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0
        + vec2(getSampleOffset(texCoord0, 0.2, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).b;
    vec2 scaledCoord = texCoord0 * DISPLAY_RES;

#if (DISPLAY_TYPE == DISPLAY_TYPE_BW_LCD)
    inputColor = dither(convertBw(inputColor), scaledCoord);
#elif (DISPLAY_TYPE == DISPLAY_TYPE_SRGB_LCD)
    inputColor = convertSrgb(inputColor);
#endif

    vec4 color = texture(Sampler1, scaledCoord);
    color = mix(inputColor, color * inputColor, PIXEL_FACTOR);
    fragColor = color * ColorModulator;
}

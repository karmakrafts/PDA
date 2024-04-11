#version 150

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#moj_import <pda:utils.glsl>
#moj_import <pda:display.glsl>
#moj_import <pda:dither.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform float Time;
uniform float GlitchFactor;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

const float GLITCH_RATE = 0.05;
const float GLITCH_FACTOR = 0.2;
const int GLITCH_BLOCKS = 16;
const float PIXEL_FACTOR = 0.06;

float getSampleOffset(vec2 coord, float offset, float glitchMultiplier) {
    return goldNoise(vec2(Time * (GLITCH_RATE * 0.1 * glitchMultiplier) + offset, floor(coord.y * GLITCH_BLOCKS) - (offset * 0.5)));
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    float glitchMultiplier = 1F + (GlitchFactor * 120.0);
    float glitchFactor = GLITCH_FACTOR * glitchMultiplier;
    inputColor.r = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.0, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.1, glitchMultiplier) * 0.03 * 0.16666666, 0.0) * glitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.2, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).b;

    vec2 scaledCoord = texCoord0 * DISPLAY_RES;
    inputColor = dither(convertBw(inputColor), scaledCoord);

    vec4 color = texture(Sampler1, scaledCoord);
    color = mix(inputColor, color * inputColor, PIXEL_FACTOR);

    fragColor = color * ColorModulator;
}

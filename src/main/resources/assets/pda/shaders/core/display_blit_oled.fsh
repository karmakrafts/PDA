#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform float Time;
uniform float GlitchFactor;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

const vec2 DISPLAY_RES = vec2(256.0, 288.0);
const float GLITCH_RATE = 0.03;
const float GLITCH_FACTOR = 0.025;
const int GLITCH_BLOCKS = 32;
const float PIXEL_FACTOR = 0.03;

// Gold noise taken from https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
float goldNoise(vec2 coord){
    return fract(sin(dot(coord.xy, vec2(12.9898, 78.233))) * 43758.5453) * 2.0 - 1.0;
}

float getSampleOffset(vec2 coord, float offset, float glitchMultiplier) {
    return goldNoise(vec2(Time * (GLITCH_RATE * 0.1 * glitchMultiplier) + offset, floor(coord.y * GLITCH_BLOCKS) - (offset * 0.5)));
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    float glitchMultiplier = 1F + (GlitchFactor * 80.0);
    float glitchFactor = GLITCH_FACTOR * glitchMultiplier;
    inputColor.r = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.0, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.1, glitchMultiplier) * 0.03 * 0.16666666, 0.0) * glitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.2, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).b;
    vec4 color = texture(Sampler1, texCoord0 * DISPLAY_RES);
    color = mix(inputColor, color * inputColor, PIXEL_FACTOR);
    fragColor = color * ColorModulator;
}

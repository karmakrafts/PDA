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
const float GLITCH_RATE = 0.05;
const float GLITCH_FACTOR = 0.1;
const int GLITCH_BLOCKS = 16;
const float PIXEL_FACTOR = 0.06;

const float SRGB_EXPONENT = 1.0 / 2.4;
const float SRGB_THRESHOLD = 0.0031308;
const float SRGB_FACTOR = 12.92;
const float SRGB_CURVE_BASE = 1.055;
const float SRGB_CURVE_REMAINDER = 0.055;

// Gold noise taken from https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
float goldNoise(vec2 coord){
    return fract(sin(dot(coord.xy, vec2(12.9898, 78.233))) * 43758.5453) * 2.0 - 1.0;
}

vec4 convertSrgb(vec4 color){
    bvec3 mask = lessThan(color.rgb, vec3(SRGB_THRESHOLD));
    vec3 rgb = color.rgb;
    rgb = mix(SRGB_CURVE_BASE * pow(rgb, vec3(SRGB_EXPONENT)) - SRGB_CURVE_REMAINDER, SRGB_FACTOR * rgb, mask);
    return vec4(rgb, color.a);
}

float getSampleOffset(vec2 coord, float offset, float glitchMultiplier) {
    return goldNoise(vec2(Time * (GLITCH_RATE * 0.1 * getGlitchMultiplier()) + offset, floor(coord.y * GLITCH_BLOCKS) - (offset * 0.5)));
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    float glitchMultiplier = 1F + (GlitchFactor * 100.0);
    float glitchFactor = GLITCH_FACTOR * glitchMultiplier;
    inputColor.r = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.0, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.1, glitchMultiplier) * 0.03 * 0.16666666, 0.0) * glitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.2, glitchMultiplier) * 0.03, 0.0) * glitchFactor) * vertexColor).b;
    inputColor = convertSrgb(inputColor);
    vec4 color = texture(Sampler1, texCoord0 * DISPLAY_RES);
    color = mix(inputColor, color * inputColor, PIXEL_FACTOR);
    fragColor = color * ColorModulator;
}

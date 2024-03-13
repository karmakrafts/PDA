#version 150

uniform sampler2D Sampler0;// Texture atlas
uniform sampler2D Sampler1;// Pixel sprite

uniform vec4 ColorModulator;
uniform float Time;// The current render time

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

const vec2 DisplayResolution = vec2(256.0, 288.0);
const float GlitchRate = 0.05;
const float GlitchFactor = 0.2;
const int GlitchBlocks = 16;
const float PixelFactor = 0.03;

const float SRGB_EXPONENT = 1.0 / 2.4;
const float SRGB_THRESHOLD = 0.0031308;
const float SRGB_FACTOR = 12.92;
const float SRGB_CURVE_BASE = 1.055;
const float SRGB_CURVE_REMAINDER = 0.055;

// Gold noise taken from https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
float goldNoise(vec2 coord){
    return fract(sin(dot(coord.xy, vec2(12.9898, 78.233))) * 43758.5453) * 2.0 - 1.0;
}

float getSampleOffset(vec2 coord, float offset) {
    return goldNoise(vec2(Time * (GlitchRate * 0.1) + offset, floor(coord.y * GlitchBlocks) - (offset * 0.5)));
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    inputColor.r = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.0) * 0.03, 0.0) * GlitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.1) * 0.03 * 0.16666666, 0.0) * GlitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.2) * 0.03, 0.0) * GlitchFactor) * vertexColor).b;
    vec4 color = texture(Sampler1, texCoord0 * DisplayResolution);
    color = mix(inputColor, color * inputColor, PixelFactor);
    fragColor = color * ColorModulator;
}

#version 150

uniform sampler2D Sampler0;// Texture atlas
uniform sampler2D Sampler1;// Pixel sprite

uniform vec4 ColorModulator;
uniform vec2 DisplayResolution;// Resolution of virtual screen in pixels
uniform float GlitchRate;// The rate at which glitches occur
uniform float GlitchFactor;// The absolute amount of glitches applied to the display
uniform int GlitchBlocks;// The number of blocks the blit is split into vertically
uniform float PixelFactor;// The amount to mix in pixels into the blitted display output
uniform float Time;// The current render time

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

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

float getSampleOffset(vec2 coord, float offset) {
    return goldNoise(vec2(Time * (GlitchRate * 0.1) + offset, floor(coord.y * GlitchBlocks) - (offset * 0.5)));
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    inputColor.r = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.0) * 0.03, 0.0) * GlitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.1) * 0.03 * 0.16666666, 0.0) * GlitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.2) * 0.03, 0.0) * GlitchFactor) * vertexColor).b;
    inputColor = convertSrgb(inputColor);
    vec4 color = texture(Sampler1, texCoord0 * DisplayResolution);
    color = mix(inputColor, color * inputColor, PixelFactor);
    fragColor = color * ColorModulator;
}

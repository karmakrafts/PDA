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
const float GlitchFactor = 0.8;
const int GlitchBlocks = 16;
const float PixelFactor = 0.06;

const float DitherColorFactor = 32.0;
const float DitherFactor = 0.005;
const int DitherKernelSize = 4;
// @formatter:off
const mat4 DitherKernel = mat4(
    -4.0, 0.0, -3.0, 1.0,
    2.0, -2.0, 3.0, -1.0,
    -3.0, 1.0, -4.0, 0.0,
    3.0, -1.0, 2.0, -2.0
);
// @formatter:on

// Gold noise taken from https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
float goldNoise(vec2 coord){
    return fract(sin(dot(coord.xy, vec2(12.9898, 78.233))) * 43758.5453) * 2.0 - 1.0;
}

vec4 convertBw(vec4 color){
    float luminance = color.r * 0.21 + color.g * 0.71 + color.b * 0.07;
    return vec4(vec3(luminance), color.a);
}

float getSampleOffset(vec2 coord, float offset) {
    return goldNoise(vec2(Time * (GlitchRate * 0.1) + offset, floor(coord.y * GlitchBlocks) - (offset * 0.5)));
}

vec4 dither(vec4 color, vec2 coord) {
    int xIndex = int(coord.x) % DitherKernelSize;
    int yIndex = int(coord.y) % DitherKernelSize;
    vec3 result = color.rgb + DitherKernel[xIndex][yIndex] * DitherFactor;
    return vec4(floor(result * DitherColorFactor) / DitherColorFactor, color.a);
}

void main() {
    vec4 inputColor = texture(Sampler0, texCoord0) * vertexColor;
    inputColor.r = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.0) * 0.03, 0.0) * GlitchFactor) * vertexColor).r;
    inputColor.g = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.1) * 0.03 * 0.16666666, 0.0) * GlitchFactor) * vertexColor).g;
    inputColor.b = (texture(Sampler0, texCoord0 + vec2(getSampleOffset(texCoord0, 0.2) * 0.03, 0.0) * GlitchFactor) * vertexColor).b;

    vec2 scaledCoord = texCoord0 * DisplayResolution;
    inputColor = dither(convertBw(inputColor), scaledCoord);

    vec4 color = texture(Sampler1, scaledCoord);
    color = mix(inputColor, color * inputColor, PixelFactor);

    fragColor = color * ColorModulator;
}

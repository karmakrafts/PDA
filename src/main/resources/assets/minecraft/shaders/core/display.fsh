#version 150

uniform sampler2D Sampler0; // Texture atlas
uniform sampler2D Sampler1; // Pixel sprite

uniform vec4 ColorModulator;
uniform vec2 DisplayResolution; // Resolution of virtual screen in pixels

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec4 convertSrgb(vec4 color){
    float r = color.r < 0.0031308 ? 12.92 * color.r : 1.055 * pow(color.r, 1.0/2.4) - 0.055;
    float g = color.g < 0.0031308 ? 12.92 * color.g : 1.055 * pow(color.g, 1.0/2.4) - 0.055;
    float b = color.b < 0.0031308 ? 12.92 * color.b : 1.055 * pow(color.b, 1.0/2.4) - 0.055;
    return vec4(r, g, b, color.a);
}

void main() {
    vec4 spriteColor = convertSrgb(texture(Sampler0, texCoord0) * vertexColor);
    vec4 color = texture(Sampler1, texCoord0 * DisplayResolution);
    vec4 multipliedColor = color * spriteColor;
    color = mix(spriteColor, multipliedColor, 0.1);
    fragColor = color * ColorModulator;
}

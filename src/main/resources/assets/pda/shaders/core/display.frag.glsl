#version 150

uniform sampler2D Sampler0; // Texture atlas
uniform sampler2D Sampler1; // Pixel sprite

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = vec4(1.0);
    color = color * texture(Sampler0, texCoord0) * vertexColor;
    fragColor = color * ColorModulator;
}

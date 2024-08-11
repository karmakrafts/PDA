#version 330 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float PxRange;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float median(vec3 rgb) {
    return max(min(rgb.r, rgb.g), min(max(rgb.r, rgb.g), rgb.b));
}

void main() {
    float sd = PxRange * (median(texture(Sampler0, texCoord0).rgb) - 0.5);
    float opacity = clamp(sd + 0.5, 0.0, 1.0);
    fragColor = mix(vec4(0.0), vertexColor * ColorModulator, opacity);
}

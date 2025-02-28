#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "include/global_uniforms.glsl"

uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    fragColor = color * ColorModulator;
}

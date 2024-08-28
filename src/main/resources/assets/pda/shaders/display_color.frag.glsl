#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "include/global_uniforms.glsl"

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = vertexColor * ColorModulator;
}

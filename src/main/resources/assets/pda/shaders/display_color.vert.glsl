#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "include/global_uniforms.glsl"

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
}

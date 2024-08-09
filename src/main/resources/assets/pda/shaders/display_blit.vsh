#version 150

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "utils/utils.glsl"

in vec3 Position;// position
in vec2 UV0;// uv
in vec4 Color;// color

uniform mat4 ModelViewMat;// model_view_matrix
uniform mat4 ProjMat;// projection_matrix

out vec2 texCoord0;// out_uv
out vec4 vertexColor;// out_color

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
}

#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "include/utils.glsl"
#include "include/global_uniforms.glsl"

in vec3 Position;// position
in vec4 Color;// color
in vec2 UV0;// uv
in vec2 UV1;// overlay
in vec2 UV2;// lightmap
in vec3 Normal;// normal

out vec2 texCoord0;// out_uv
out vec4 vertexColor;// out_color

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
}

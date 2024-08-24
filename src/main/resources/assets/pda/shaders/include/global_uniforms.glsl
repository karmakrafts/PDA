/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

layout(std140) uniform Globals {
    mat4 ProjMat;
    mat4 ModelViewMat;
    vec4 ColorModulator;
    float Time;
};
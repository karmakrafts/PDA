#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 * Source: https://www.shadertoy.com/view/Mc3Xzf
 */

in vec4 vertexColor;
in vec2 texCoord0;

uniform float Time;

out vec4 fragColor;

const float PI = 3.14159265359;
const float TPI = 6.28318530718;
const float HPI = 1.57079632679;

void main() {
    float time = Time * 0.125;

    float angle = -(time - sin(time + PI) * cos(time)) - time * 0.95;
    float aSin = sin(angle);
    float aCos = cos(angle);
    vec2 p = mat2(aCos, aSin, -aSin, aCos) * (texCoord0 - 0.5) * 0.725;

    float L = length(p);
    float a = atan(p.y, p.x);
    // @formatter:off
    float f = (smoothstep(L - 0.005, L, 0.35) - smoothstep(L, L + 0.005, 0.27))
        * step(a, sin(mod(time, TPI) - PI) * (PI - 0.25)) * (1.0 - step(a, -PI));
    // @formatter:on

    fragColor = vertexColor * vec4(vec3(1.0), f);
}

#version 410 core

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

in vec4 vertexColor;

uniform vec4 ColorModulator;

out vec4 fragColor;

void main() {
    fragColor = vertexColor * ColorModulator;
}

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

vec4 crtFadeOut(const vec2 uv, const vec2 size, const sampler2D tex, const float time, const float bufferFactor,
                const float scaleSpeed, const vec4 fadeColor) {
    const float pixelScale = (size.y - 1.0) / size.y;
    const float s = 1.0 / scaleSpeed + 1.0;
    const float t = clamp(mod(time * 4.0, s + bufferFactor * 2.0) - bufferFactor, 0.0, s);
    const float st = clamp(t * scaleSpeed, 0.0, pixelScale);
    const float ft = clamp(t - pixelScale / scaleSpeed, 0.0, 1.0);
    // @formatter:off
    const vec4 texColor = texture(tex, vec2(
        (uv.x - 0.5) * (1.0 - st) + 0.5,
        (uv.y - 0.5) / (1.0 - st) + 0.5
    ));
    return mix(fadeColor, mix(fadeColor, texColor, 1.0 - ft), min(
        clamp(sign(abs(st * 0.5 - 0.5) - abs(uv.y - 0.5)), 0.0, 1.0),
        clamp(sign(1.0 - ft - abs(uv.x - 0.5)), 0.0, 1.0)
    ));
    // @formatter:on
}

vec4 crtFadeIn(const vec2 uv, const vec2 size, const sampler2D tex, const float time, const float bufferFactor,
               const float scaleSpeed, const vec4 fadeColor) {
    return crtFadeOut(uv, size, tex, -time, bufferFactor, scaleSpeed, fadeColor);
}
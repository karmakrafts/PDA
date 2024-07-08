/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.color;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.karma.pda.api.util.RectangleCorner;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class Gradient implements ColorProvider {
    @JsonIgnore
    private Color startColor;
    @JsonIgnore
    private Color endColor;
    @JsonIgnore
    private GradientFunction gradientFunction;

    @ApiStatus.Internal
    @JsonCreator
    public Gradient() {
    }

    @JsonIgnore
    public Gradient(final Color startColor, final Color endColor, final GradientFunction gradientFunction) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.gradientFunction = gradientFunction;
    }

    @Override
    public int getColor(final RectangleCorner corner) {
        return gradientFunction.remap(startColor, endColor, corner).packARGB();
    }

    @JsonSetter("function")
    public void setGradientFunction(final GradientFunction gradientFunction) {
        this.gradientFunction = gradientFunction;
    }

    @JsonGetter("start")
    public Color getStartColor() {
        return startColor;
    }

    @JsonSetter("start")
    public void setStartColor(final Color startColor) {
        this.startColor = startColor;
    }

    @JsonGetter("end")
    public Color getEndColor() {
        return endColor;
    }

    @JsonSetter("end")
    public void setEndColor(final Color endColor) {
        this.endColor = endColor;
    }

    @JsonGetter("function")
    public GradientFunction getFunction() {
        return gradientFunction;
    }
}

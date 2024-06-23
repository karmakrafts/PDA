/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import org.joml.Vector3f;

import java.util.Arrays;

/**
 * A simple 3D bezier-curve implementation with a variable number of points and samples.
 *
 * @author Alexander Hinze
 * @since 12/03/2024
 */
public final class BezierCurve {
    private final Vector3f[] samples;
    private Vector3f[] points;

    public BezierCurve(final int sampleCount, final Vector3f... points) {
        this.points = points;
        this.samples = new Vector3f[sampleCount];
        Arrays.fill(samples, new Vector3f());
        // Pre-compute the curve when its constructed
        compute();
    }

    private float computeBinomial(final int n, final int k) {
        if (k == 0 || n == k) {
            return 1F;
        }
        float result = 1F;
        for (var i = 1; i <= k; i++) {
            result = result * (((float) n + 1F - (float) i) / (float) i);
        }
        return result;
    }

    private void compute() {
        final var numPoints = points.length - 1;
        final var numSamples = samples.length - 1;
        for (var sampleIndex = 0; sampleIndex <= numSamples; sampleIndex++) {
            var sample = new Vector3f();
            final var t = (float) sampleIndex / numSamples;
            for (var i = 0; i <= numPoints; i++) {
                final var point = points[i];
                final var coeff = computeBinomial(numPoints, i) * (float) Math.pow(1F - t,
                    numPoints - i) * (float) Math.pow(t, i);
                sample.add(point.mul(coeff, new Vector3f()));
            }
            samples[sampleIndex] = sample;
        }
    }

    public Vector3f getSample(final int index) {
        return samples[index];
    }

    public int getSampleCount() {
        return samples.length;
    }

    public Vector3f[] getPoints() {
        return points;
    }

    public void setPoints(final Vector3f... points) {
        this.points = points;
        compute();
    }
}

package io.karma.pda.common.util;

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

    public void setPoints(final Vector3f... points) {
        this.points = points;
        compute();
    }

    private float computeBinomial(final int n, final int k) {
        float result = 1F;
        if (n == k) {
            return 1F;
        }
        for (var i = 1; i <= k; i++) {
            result = result * (((float) n + 1F - (float) i) / (float) i);
        }
        return result;
    }

    private void compute() {
        final var numPoints = points.length;
        final var step = 1F / samples.length;
        final var n = numPoints - 1;
        var sampleIndex = 0;
        for (var t = step; t <= 1F; t += step) {
            var sample = new Vector3f();
            for (var i = 0; i <= n; i++) {
                final var point = points[i];
                final var binomial = computeBinomial(n, i);
                final var f1 = Math.pow(1F - t, n - i);
                final var f2 = Math.pow(t, i);
                sample.x += (float) (binomial * f1 * f2 * point.x);
                sample.y += (float) (binomial * f1 * f2 * point.y);
                sample.z += (float) (binomial * f1 * f2 * point.z);
            }
            samples[sampleIndex++] = sample;
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
}

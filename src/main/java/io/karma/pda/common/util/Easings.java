/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.util;

/**
 * Simple Java implementation of easing-functions found at
 * <b><a href="https://easings.net/en" target="_blank">https://easings.net/en</a></b>.
 * Functions defined in this class are expecting absolute values, and as such,
 * clamp the input and output in a range between 0.0 and 1.0.
 * <p>
 * Taken from <a href="https://git.karmakrafts.dev/kk/kommons/-/blob/master/src/main/java/io/karma/kommons/math/Easings.java?ref_type=heads" target="_blank">here</a>.
 *
 * @author Alexander Hinze
 * @since 14/03/2022
 */

public final class Easings {
    // Magic constants used across easings.
    private static final double TAU = Math.PI * 2.0D;
    private static final double TAU_OVER_THREE = TAU / 3D;
    private static final double TAU_OVER_FOUR_POINT_FIVE = TAU / 4.5D;
    private static final double BACK_CONSTANT = 1.70158D;
    private static final double BACK_CONSTANT_ONE = BACK_CONSTANT + 1D;
    private static final double IN_OUT_BACK_CONSTANT = BACK_CONSTANT * 1.525D;
    private static final double IN_OUT_BACK_CONSTANT_ONE = IN_OUT_BACK_CONSTANT + 1D;
    private static final double BOUNCE_MULTIPLIER = 7.5625D;
    private static final double BOUNCE_DIVIDEND = 2.75D;

    // @formatter:off
    private Easings() {}
    // @formatter:on

    // Ease ins

    public static float easeInSine(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D - Math.cos((x * Math.PI) / 2D));
    }

    public static float easeInCubic(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return x * x * x;
    }

    public static float easeInQuint(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return x * x * x * x * x;
    }

    public static float easeInCirc(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D - Math.sqrt(1D - (x * x)));
    }

    public static float easeInElastic(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x == 0F) {
            return 0F;
        }
        else if (x == 1F) {
            return 1F;
        }
        else {
            return (float) (-Math.pow(2D, 10D * x - 10D) * Math.sin((x * 10D - 10.75D) * TAU_OVER_THREE));
        }
    }

    public static float easeInQuad(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return x * x;
    }

    public static float easeInQuart(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return x * x * x * x;
    }

    public static float easeInExpo(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return x == 0F ? 0F : (float) Math.pow(2D, 10D * x - 10D);
    }

    public static float easeInBack(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (BACK_CONSTANT_ONE * x * x * x - BACK_CONSTANT * x * x);
    }

    public static float easeInBounce(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return 1F - easeOutBounce(1F - x);
    }

    // Ease outs

    public static float easeOutSine(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) Math.sin((x * Math.PI) / 2D);
    }

    public static float easeOutCubic(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D - Math.pow(1D - x, 3D));
    }

    public static float easeOutQuint(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D - Math.pow(1D - x, 5D));
    }

    public static float easeOutCirc(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) Math.sqrt(1D - Math.pow(x - 1D, 2D));
    }

    public static float easeOutElastic(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x == 0F) {
            return 0F;
        }
        else if (x == 1F) {
            return 1F;
        }
        else {
            return (float) ((Math.pow(2D, -10D * x) * Math.sin(x * 10D - 0.75D) * TAU_OVER_THREE) + 1D);
        }
    }

    public static float easeOutQuad(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D - (1D - x) * (1D - x));
    }

    public static float easeOutQuart(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D - Math.pow(1D - x, 4D));
    }

    public static float easeOutExpo(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return x == 1F ? 1F : (float) (1D - Math.pow(2D, -10D * x));
    }

    public static float easeOutBack(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (1D + BACK_CONSTANT_ONE * Math.pow(x - 1D, 3D) + BACK_CONSTANT * Math.pow(x - 1D, 2D));
    }

    public static float easeOutBounce(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < (1D / BOUNCE_DIVIDEND)) {
            return (float) (BOUNCE_MULTIPLIER * x * x);
        }
        else if (x < (2D / BOUNCE_DIVIDEND)) {
            return (float) (BOUNCE_MULTIPLIER * (x -= 1.5D / BOUNCE_DIVIDEND) * x + 0.75D);
        }
        else if (x < (2.5D / BOUNCE_DIVIDEND)) {
            return (float) (BOUNCE_MULTIPLIER * (x -= 2.25D / BOUNCE_DIVIDEND) * x + 0.9375D);
        }
        else {
            return (float) (BOUNCE_MULTIPLIER * (x -= 2.625D / BOUNCE_DIVIDEND) * x + 0.984375D);
        }
    }

    // Ease in-outs

    public static float easeInOutSine(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }
        return (float) (-(Math.cos(Math.PI * x) - 1D) / 2D);
    }

    public static float easeInOutCubic(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return 4F * x * x * x;
        }
        else {
            return (float) (1D - Math.pow(-2D * x + 2D, 3D) / 2D);
        }
    }

    public static float easeInOutQuint(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return 16F * x * x * x * x * x;
        }
        else {
            return (float) (1D - Math.pow(-2D * x + 2D, 5D) / 2D);
        }
    }

    public static float easeInOutCirc(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return (float) ((1D - Math.sqrt(1D - Math.pow(2D * x, 2D))) / 2D);
        }
        else {
            return (float) ((Math.sqrt(1D - Math.pow(-2D * x + 2D, 2D)) + 1D) / 2D);
        }
    }

    public static float easeInOutElastic(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x == 0F) {
            return 0F;
        }
        else if (x == 1F) {
            return 1F;
        }
        else {
            if (x < 0.5F) {
                return (float) (-(Math.pow(2D,
                    20D * x - 10D) * Math.sin((20D * x - 11.125D) * TAU_OVER_FOUR_POINT_FIVE)) / 2D);
            }
            else {
                return (float) ((Math.pow(2D,
                    -20D * x + 10D) * Math.sin(20D * x - 11.125D) * TAU_OVER_FOUR_POINT_FIVE) / 2D + 1D);
            }
        }
    }

    public static float easeInOutQuad(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return 2F * x * x;
        }
        else {
            return (float) (1D - Math.pow(-2D * x + 2D, 2D) / 2D);
        }
    }

    public static float easeInOutQuart(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return 8F * x * x * x * x;
        }
        else {
            return (float) (1D - Math.pow(-2D * x + 2D, 4D) / 2D);
        }
    }

    public static float easeInOutExpo(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x == 0F) {
            return 0F;
        }
        else if (x == 1F) {
            return 1F;
        }
        else {
            if (x < 0.5F) {
                return (float) (Math.pow(2D, 20D * x - 10D) / 2D);
            }
            else {
                return (float) ((2D - Math.pow(2D, -20D * x + 10D)) / 2D);
            }
        }
    }

    public static float easeInOutBack(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return (float) ((Math.pow(2D * x, 2D) * (IN_OUT_BACK_CONSTANT_ONE * 2D * x - IN_OUT_BACK_CONSTANT)) / 2D);
        }
        else {
            return (float) ((Math.pow(2D * x - 2D,
                2D) * (IN_OUT_BACK_CONSTANT_ONE * (x * 2D - 2D) + IN_OUT_BACK_CONSTANT) + 2D) / 2D);
        }
    }

    public static float easeInOutBounce(float x) {
        if (x < 0F) {
            x = 0F;
        }
        if (x > 1F) {
            x = 1F;
        }

        if (x < 0.5F) {
            return (float) ((1D - easeOutBounce((float) (1D - 2D * x))) / 2D);
        }
        else {
            return (float) ((1D + easeOutBounce((float) (2D * x - 1D))) / 2D);
        }
    }
}


package de.zonlykroks.asmplayground.math;

@SuppressWarnings("unused")
public class PiecewiseSinImplementation {

    public static double fastPiecewiseSin(float x) {
        return fastPiecewiseSin((double) x);
    }

    public static double fastPiecewiseSin(double x) {
        final float TWO_PI = 6.28318530f;
        final float PI = 3.14159265f;
        final float HALF_PI = 1.57079632f;

        float xFloat = (float) x;

        float recipTwoPI = 1.0f / TWO_PI;
        int n = (int)(xFloat * recipTwoPI + (xFloat >= 0 ? 0.5f : -0.5f));
        float xNormalized = xFloat - n * TWO_PI;

        if (Math.abs(xNormalized) < 1e-5f) {
            return xNormalized;
        }

        boolean negate = false;
        if (xNormalized < 0.0f) {
            xNormalized = -xNormalized;
            negate = true;
        }

        if (xNormalized > PI) {
            xNormalized = TWO_PI - xNormalized;
            negate = !negate;
        }

        if (xNormalized > HALF_PI) {
            xNormalized = PI - xNormalized;
        }

        final float xSquared = xNormalized * xNormalized;

        float result;
        if (xNormalized < 0.5f) {
            result = xNormalized * (
                    1.0f - xSquared * (
                            0.16666666f - xSquared * (
                                    0.00833333f - xSquared * 0.00019841f
                            )
                    )
            );
        } else if (xNormalized < 1.3f) {
            result = xNormalized * (
                    1.0f - xSquared * (
                            0.16666667f - xSquared * (
                                    0.00833333f - xSquared * (
                                            0.00019841f - xSquared * 0.00000276f
                                    )
                            )
                    )
            );
        } else {
            result = xNormalized * (
                    1.0f - xSquared * (
                            0.16666667f - xSquared * (
                                    0.00833333f - xSquared * (
                                            0.00019841f - xSquared * (
                                                    0.00000276f - xSquared * 0.00000002f
                                            )
                                    )
                            )
                    )
            );
        }

        return negate ? -result : result;
    };
}

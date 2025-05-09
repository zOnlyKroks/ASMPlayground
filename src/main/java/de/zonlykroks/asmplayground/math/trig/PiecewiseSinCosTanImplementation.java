package de.zonlykroks.asmplayground.math.trig;

@SuppressWarnings("unused")
public class PiecewiseSinCosTanImplementation {

    // extracted constants to avoid reallocation per-call
    private static final float TWO_PI        = 6.28318530f;
    private static final float PI            = 3.14159265f;
    private static final float HALF_PI       = 1.57079632f;
    private static final float RECIP_TWO_PI  = 1.0f / TWO_PI;
    private static final double HALF_PI_D    = HALF_PI;

    /**
     * Fast piecewise sine approximation (double input).
     */
    public static double fastPiecewisesin(double x) {
        // range-reduce into [-PI, PI]
        float xf = (float) x;
        int n = (int) (xf * RECIP_TWO_PI + (xf >= 0 ? 0.5f : -0.5f));
        float xNorm = xf - n * TWO_PI;

        // mirror into [0, PI/2]
        boolean negate = false;
        if (xNorm < 0.0f) {
            xNorm = -xNorm;
            negate = true;
        }
        if (xNorm > PI) {
            xNorm = TWO_PI - xNorm;
            negate = !negate;
        }
        if (xNorm > HALF_PI) {
            xNorm = PI - xNorm;
        }

        // piecewise polynomial segments
        float x2 = xNorm * xNorm;
        float result;
        if (xNorm < 0.5f) {
            result = xNorm * (
                    1.0f - x2 * (
                            0.16666666f - x2 * (
                                    0.00833333f - x2 * 0.00019841f
                            )
                    )
            );
        } else if (xNorm < 1.3f) {
            result = xNorm * (
                    1.0f - x2 * (
                            0.16666667f - x2 * (
                                    0.00833333f - x2 * (
                                            0.00019841f - x2 * 0.00000276f
                                    )
                            )
                    )
            );
        } else {
            result = xNorm * (
                    1.0f - x2 * (
                            0.16666667f - x2 * (
                                    0.00833333f - x2 * (
                                            0.00019841f - x2 * (
                                                    0.00000276f - x2 * 0.00000002f
                                            )
                                    )
                            )
                    )
            );
        }

        return negate ? -result : result;
    }

    /**
     * Fast piecewise cosine approximation (double input).
     */
    public static double fastPiecewisecos(double x) {
        // cos(x) = sin(x + PI/2)
        return fastPiecewisesin(x + HALF_PI_D);
    }

    /**
     * Fast piecewise tangent approximation (double input).
     */
    public static double fastPiecewiseTan(double x) {
        double s = fastPiecewisesin(x);
        double c = fastPiecewisecos(x);
        return s / c;
    }
}

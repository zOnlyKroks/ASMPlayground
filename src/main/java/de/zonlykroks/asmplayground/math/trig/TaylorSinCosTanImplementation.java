package de.zonlykroks.asmplayground.math.trig;

@SuppressWarnings("unused")
public class TaylorSinCosTanImplementation {
    private static final double TWO_PI    = 2 * Math.PI;
    private static final double INV_TWO_PI = 1.0 / TWO_PI;
    private static final double HALF_PI   = Math.PI / 2;

    /**
     * Reduce angle to the range [-π, π].
     */
    private static double normalize(double x) {
        // fast modulo into [0, 2π)
        x = x - TWO_PI * Math.floor(x * INV_TWO_PI);
        // map to (-π, π]
        if (x > Math.PI)       x -= TWO_PI;
        else if (x <= -Math.PI) x += TWO_PI;
        return x;
    }

    /**
     * Core Taylor series for sin(x), evaluated via Horner’s method,
     * accurate up to the x^17 term.
     * Assumes |x| ≤ π/2.
     */
    private static double sinCore(double x) {
        double x2 = x * x;
        return x * (
                1
                        - x2 * (
                        1.0/6
                                - x2 * (
                                1.0/120
                                        - x2 * (
                                        1.0/5040
                                                - x2 * (
                                                1.0/362880
                                                        - x2 * (
                                                        1.0/39916800
                                                                - x2 * (
                                                                1.0/6227020800.0
                                                                        - x2 * (1.0/355687428096000.0)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    /**
     * Core Taylor series for cos(x), evaluated via Horner’s method,
     * accurate up to the x^16 term.
     * Assumes |x| ≤ π/2.
     */
    private static double cosCore(double x) {
        double x2 = x * x;
        return 1 - x2 * (
                0.5
                        - x2 * (
                        1.0/24
                                - x2 * (
                                1.0/720
                                        - x2 * (
                                        1.0/40320
                                                - x2 * (
                                                1.0/3628800
                                                        - x2 * (
                                                        1.0/479001600
                                                                - x2 * (1.0/20922789888000.0)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    /**
     * Computes sin(x) with argument reduction and Taylor series.
     */
    public static double taylorsin(double rad) {
        // 1) normalize to [-π, π]
        double x = normalize(rad);
        // 2) reduce to [-π/2, π/2] using symmetry
        if (x > HALF_PI) {
            // sin(x) =  sin(π - x)
            x = Math.PI - x;
        } else if (x < -HALF_PI) {
            // sin(x) =  sin(-π - x) = -sin(π + x), but mapping to [-π/2, π/2]:
            x = -Math.PI - x;
        }
        // now |x| ≤ π/2
        return sinCore(x);
    }

    /**
     * Computes cos(x) with argument reduction and Taylor series.
     */
    public static double taylorcos(double rad) {
        // 1) normalize to [-π, π]
        double x = normalize(rad);
        // cos is even, fold into [0, π]
        x = Math.abs(x);
        // reduce to [0, π/2] using symmetry
        boolean negate = false;
        if (x > HALF_PI) {
            // cos(x) = −cos(π - x)
            x = Math.PI - x;
            negate = true;
        }
        // now 0 ≤ x ≤ π/2
        double c = cosCore(x);
        return negate ? -c : c;
    }

    /**
     * Computes tan(x) = sin(x) / cos(x).
     */
    public static double taylortan(double rad) {
        return taylorsin(rad) / taylorcos(rad);
    }
}

package de.zonlykroks.asmplayground.math.trig.fast;

@SuppressWarnings("unused")
public class FastAsinImplementation {
    private static final int LUT_RESOLUTION = 1024;
    private static final double[] asinLUT = new double[LUT_RESOLUTION + 1];
    static {
        for (int i = 0; i <= LUT_RESOLUTION; i++) {
            double x = -1.0 + 2.0 * i / LUT_RESOLUTION;
            asinLUT[i] = Math.asin(x);
        }
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * Minimax 5th-degree polynomial approximation for asin(x)
     * Domain: [-1, 1]
     */
    public static double fastAsinPolynomial(double x) {
        if (x < -1.0 || x > 1.0) return Double.NaN;
        if (x == 1.0) return Math.PI / 2;
        if (x == -1.0) return -Math.PI / 2;

        boolean negate = x < 0;
        x = Math.abs(x);

        double root = Math.sqrt(1.0 - x);
        double result = root * (1.5707288
                + -0.2121144 * x
                + 0.0742610 * x * x
                + -0.0187293 * x * x * x);
        return negate ? -result : result;
    }

    /**
     * Lookup table + linear interpolation for asin(x)
     * Domain: [-1, 1]
     */
    public static double fastAsinLUT(double x) {
        if (x < -1.0 || x > 1.0) return Double.NaN;
        double norm = (x + 1.0) * 0.5;
        double idx = norm * LUT_RESOLUTION;
        int i = (int) Math.floor(idx);
        if (i < 0) i = 0;
        if (i >= LUT_RESOLUTION) i = LUT_RESOLUTION - 1;
        double t = idx - i;
        return lerp(asinLUT[i], asinLUT[i + 1], t);
    }
}

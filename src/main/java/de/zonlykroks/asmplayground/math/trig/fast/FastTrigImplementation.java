package de.zonlykroks.asmplayground.math.trig.fast;

@SuppressWarnings("unused")
public class FastTrigImplementation {
    private static final double TWO_PI = Math.PI * 2;
    private static final int LUT_RESOLUTION = 1024;

    // Sine LUT for [0, 2π]
    private static final double[] sinLUT = new double[LUT_RESOLUTION + 1];
    static {
        for (int i = 0; i <= LUT_RESOLUTION; i++) {
            sinLUT[i] = Math.sin((double) i / LUT_RESOLUTION * TWO_PI);
        }
    }

    // Arctan LUT for [-1, 1]
    private static final double[] atanLUT = new double[LUT_RESOLUTION + 1];
    static {
        for (int i = 0; i <= LUT_RESOLUTION; i++) {
            double x = -1.0 + 2.0 * i / LUT_RESOLUTION;
            atanLUT[i] = Math.atan(x);
        }
    }

    private static double normalizeRadians(double x) {
        x %= TWO_PI;
        if (x < 0) x += TWO_PI;
        return x;
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static double fastSinPolynomial(double x) {
        // Taylor series for sin(x) around 0: x - x^3/6 + x^5/120 - x^7/5040
        x = normalizeRadians(x);
        if (x > Math.PI) x -= TWO_PI;  // map to [-π, π]
        double x2 = x * x;
        return x - x * x2 / 6.0 + x * x2 * x2 / 120.0 - x * x2 * x2 * x2 / 5040.0;
    }

    public static double fastSinLUT(double x) {
        x = normalizeRadians(x);
        double idx = x / TWO_PI * LUT_RESOLUTION;
        int i = (int) Math.floor(idx);
        if (i >= LUT_RESOLUTION) return sinLUT[LUT_RESOLUTION];
        double t = idx - i;
        return lerp(sinLUT[i], sinLUT[i + 1], t);
    }

    public static double fastCosPolynomial(double x) {
        // cos(x) = sin(x + π/2)
        return fastSinPolynomial(x + Math.PI / 2);
    }

    public static double fastCosLUT(double x) {
        // cos(x) = sin(x + π/2)
        return fastSinLUT(x + Math.PI / 2);
    }

    public static double fastTanPolynomial(double x) {
        // tan(x) ≈ x + x^3/3 + 2x^5/15
        x = normalizeRadians(x);
        if (x > Math.PI/2) x -= Math.PI;  // map to [-π/2, π/2]
        if (x < -Math.PI/2) x += Math.PI;
        double x2 = x * x;
        return x + x * x2 / 3.0 + 2.0 * x * x2 * x2 / 15.0;
    }

    public static double fastTanLUT(double x) {
        return fastSinLUT(x) / fastCosLUT(x);
    }

    public static double fastAcosPolynomial(double x) {
        // acos(x) = π/2 - asin(x)
        return Math.PI / 2 - FastAsinImplementation.fastAsinPolynomial(x);
    }

    public static double fastAcosLUT(double x) {
        return Math.PI / 2 - FastAsinImplementation.fastAsinLUT(x);
    }

    public static double fastAtanPolynomial(double x) {
        // Arctan polynomial: x - x^3/3 + x^5/5 - x^7/7 for |x| <= 1
        double sign = Math.signum(x);
        x = Math.abs(x);
        if (x > 1.0) {
            return sign * (Math.PI / 2 - fastAtanPolynomial(1.0 / x));
        }
        double x2 = x * x;
        return sign * (x - x * x2 / 3.0 + x * x2 * x2 / 5.0 - x * x2 * x2 * x2 / 7.0);
    }

    public static double fastAtanLUT(double x) {
        double sign = Math.signum(x);
        x = Math.abs(x);
        if (x <= 1.0) {
            double idx = (x) * LUT_RESOLUTION;
            int i = (int) Math.floor(idx);
            if (i >= LUT_RESOLUTION) i = LUT_RESOLUTION;
            double t = idx - i;
            return sign * lerp(atanLUT[i], atanLUT[i + 1], t);
        } else {
            return sign * (Math.PI / 2 - fastAtanLUT(1.0 / x));
        }
    }
}

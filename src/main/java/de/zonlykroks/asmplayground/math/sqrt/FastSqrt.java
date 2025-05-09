package de.zonlykroks.asmplayground.math.sqrt;

/**
 * Fast square root implementation using a lookup table (LUT) for values in the range [0, 256].
 * For values outside this range, it falls back to the standard Math.sqrt() method.
 * <p>
 * This implementation uses linear interpolation between the LUT values for better accuracy.
 * </p>
 */
public class FastSqrt {

    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 256.0;
    private static final double STEP_SIZE = 0.01;
    private static final int LUT_SIZE = (int) ((MAX_VALUE - MIN_VALUE) / STEP_SIZE) + 1;
    private static final double[] sqrtLUT = new double[LUT_SIZE];

    static {
        for (int i = 0; i < LUT_SIZE; i++) {
            double value = MIN_VALUE + i * STEP_SIZE;
            sqrtLUT[i] = Math.sqrt(value);
        }
    }

    public static double sqrt(double x) {
        if (x < MIN_VALUE || x > MAX_VALUE) {
            return Math.sqrt(x);
        }
        int index = (int) ((x - MIN_VALUE) / STEP_SIZE);
        double base = MIN_VALUE + index * STEP_SIZE;
        if (x == base) {
            return sqrtLUT[index];
        }
        double next = sqrtLUT[index + 1];
        double prev = sqrtLUT[index];
        return prev + (x - base) * (next - prev) / STEP_SIZE;
    }
}

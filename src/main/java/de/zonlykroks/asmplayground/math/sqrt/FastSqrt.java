package de.zonlykroks.asmplayground.math.sqrt;

import de.zonlykroks.asmplayground.impl.ModConfig;

/**
 * Fast square root implementation using a lookup table (LUT) for values in the range [0, 256].
 * For values outside this range, it falls back to the standard Math.sqrt() method.
 * <p>
 * This implementation uses linear interpolation between the LUT values for better accuracy.
 * </p>
 */
public class FastSqrt {

    private static final double MIN_VALUE = ModConfig.INSTANCE.sqrtMinLutValue;
    private static final double MAX_VALUE = ModConfig.INSTANCE.sqrtMaxLutValue;
    private static final double STEP_SIZE = ModConfig.INSTANCE.sqrtLutStepSize;
    private static final int LUT_SIZE = ModConfig.INSTANCE.sqrtLutSize;
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

    public static double inversedInvSqrt(double x) {
        return 1.0 / fastInvSqrt(x);
    }

    /**
     * Fast approximation of 1/√x in double precision.
     *
     * @param x the input value (must be positive)
     * @return approximate 1.0 / Math.sqrt(x)
     */
    public static double fastInvSqrt(double x) {
        long bits = Double.doubleToLongBits(x);
        bits = 0x5fe6ec85e7de30daL - (bits >> 1);
        double y = Double.longBitsToDouble(bits);
        // Newton–Raphson refinement
        y = y * (1.5 - 0.5 * x * y * y);
        return y;
    }
}

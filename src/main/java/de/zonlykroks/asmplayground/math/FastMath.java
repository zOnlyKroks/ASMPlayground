package de.zonlykroks.asmplayground.math;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class FastMath {
    // Define the range and step size for the LUT
    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 256.0;  // Adjust this threshold as needed
    private static final double STEP_SIZE = 0.01;  // Appropriate step size (0.01 for 2 decimal precision)
    private static final int LUT_SIZE = (int) ((MAX_VALUE - MIN_VALUE) / STEP_SIZE) + 1;  // Number of entries in LUT

    // The LUT stores precomputed square roots for values within the defined range
    private static final double[] sqrtLUT = new double[LUT_SIZE];

    static {
        // Populate the LUT with precomputed square roots
        for (int i = 0; i < LUT_SIZE; i++) {
            double value = MIN_VALUE + i * STEP_SIZE;
            sqrtLUT[i] = Math.sqrt(value);  // Compute the square root for the current value
        }
    }

    public static double sqrt(double x) {
        if (x < MIN_VALUE) {
            // Handle values below the LUT range. For simplicity, use Math.sqrt() for these cases.
            return Math.sqrt(x);
        }

        // Clamp the value to the maximum value in the LUT
        if (x > MAX_VALUE) {
            return Math.sqrt(x);
        }

        // Find the index of the closest value in the LUT
        int index = (int) ((x - MIN_VALUE) / STEP_SIZE);

        // Check if x is exactly one of the values in the LUT
        if (x == MIN_VALUE + index * STEP_SIZE) {
            return sqrtLUT[index];
        }

        // If not, perform linear interpolation between the two closest values in the LUT
        double lowerValue = MIN_VALUE + index * STEP_SIZE;
        double upperValue = MIN_VALUE + (index + 1) * STEP_SIZE;

        double lowerSqrt = sqrtLUT[index];
        double upperSqrt = sqrtLUT[index + 1];

        // Linear interpolation formula: sqrt(x) ≈ lowerSqrt + (x - lowerValue) * (upperSqrt - lowerSqrt) / (upperValue - lowerValue)
        return lowerSqrt + (x - lowerValue) * (upperSqrt - lowerSqrt) / (upperValue - lowerValue);
    }

    // Method to get cached values (for debugging or monitoring purposes)
    public static Map<Double, Double> getLUTCache() {
        Map<Double, Double> cache = new ConcurrentHashMap<>();
        for (int i = 0; i < LUT_SIZE; i++) {
            double value = MIN_VALUE + i * STEP_SIZE;
            cache.put(value, sqrtLUT[i]);
        }
        return cache;
    }

    /**
     * Fast floor for both positive and negative x
     * (avoids edge‐case bugs around exact integers).
     */
    public static double floor(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return x;

        long lx = (long) x;
        double fx = (double) lx;

        return (x < 0 && x != fx) ? fx - 1.0 : fx;
    }
}

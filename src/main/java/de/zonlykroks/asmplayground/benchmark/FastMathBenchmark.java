// FastMathBenchmark.java
// A plain-Java benchmarking harness for FastMath redirect modes using System.nanoTime and a standalone AWT window

package de.zonlykroks.asmplayground.benchmark;

import de.zonlykroks.asmplayground.impl.ModConfig;
import de.zonlykroks.asmplayground.impl.modes.SinRedirectMode;
import de.zonlykroks.asmplayground.impl.modes.SqrtRedirectMode;
import de.zonlykroks.asmplayground.impl.modes.ArcSinCosTanRedirectMode;
import de.zonlykroks.asmplayground.math.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Benchmarks FastMath operations across all available redirect modes using plain Java timers.
 * Displays progress and results in a standalone AWT Swing window.
 * Selects and writes the average-best-performing modes to the config file,
 * but only updates those originally set to AUTO. Uses random inputs.
 */
public class FastMathBenchmark {
    // Iteration counts
    private static final int WARMUP_ITERATIONS = 1_000_000;
    private static final int MEASURE_ITERATIONS = 1_000_000;
    private static final int REPEAT_MEASUREMENTS = 500;

    // Volatile blackhole to prevent dead-code elimination
    private static volatile double blackhole;

    public static void testFastMathSystem() {
        System.out.println("[Benchmark] Starting FastMath benchmark...");
        SqrtRedirectMode originalSqrt = ModConfig.INSTANCE.sqrtRedirectMode;
        SinRedirectMode originalSin = ModConfig.INSTANCE.sinRedirectMode;
        ArcSinCosTanRedirectMode originalAsin = ModConfig.INSTANCE.arcSinCosTanRedirectMode;

        Map<String, Double> avgTimes = new LinkedHashMap<>();
        Map<String, Enum<?>> bestModes = new LinkedHashMap<>();

        // sqrt
        if (originalSqrt == SqrtRedirectMode.AUTO) {
            System.out.println("[Benchmark] Testing sqrt modes (excluding AUTO)...");
            for (SqrtRedirectMode mode : SqrtRedirectMode.values()) {
                if (mode == SqrtRedirectMode.AUTO) continue;
                System.out.println("Measuring sqrt mode " + mode + "...");
                ModConfig.INSTANCE.sqrtRedirectMode = mode;
                double meanNs = repeatedMeasure(() -> FastMath.sqrt(Math.random()), "sqrt[" + mode + "]");
                System.out.println(String.format("Result: sqrt[%s]: %.2f ns/call", mode, meanNs));
                avgTimes.put("sqrt:" + mode, meanNs);
                recordAverageBest("sqrt", mode, meanNs, bestModes, avgTimes);
            }
        } else {
            System.out.println("[Skip] sqrt mode locked to " + originalSqrt);
        }

        // sin
        if (originalSin == SinRedirectMode.AUTO) {
            System.out.println("[Benchmark] Testing sin modes (excluding AUTO)...");
            for (SinRedirectMode mode : SinRedirectMode.values()) {
                if (mode == SinRedirectMode.AUTO) continue;
                System.out.println("Measuring sin mode " + mode + "...");
                ModConfig.INSTANCE.sinRedirectMode = mode;
                double meanNs = repeatedMeasure(() -> FastMath.sin(Math.random()), "sin[" + mode + "]");
                System.out.println(String.format("Result: sin[%s]: %.2f ns/call", mode, meanNs));
                recordAverageBest("sin", mode, meanNs, bestModes, avgTimes);
            }
        } else {
            System.out.println("[Skip] sin mode locked to " + originalSin);
        }

        // asin
        if (originalAsin == ArcSinCosTanRedirectMode.AUTO) {
            System.out.println("[Benchmark] Testing asin modes (excluding AUTO)...");
            for (ArcSinCosTanRedirectMode mode : ArcSinCosTanRedirectMode.values()) {
                if (mode == ArcSinCosTanRedirectMode.AUTO) continue;
                System.out.println("Measuring asin mode " + mode + "...");
                ModConfig.INSTANCE.arcSinCosTanRedirectMode = mode;
                double meanNs = repeatedMeasure(() -> FastMath.asin(Math.random()), "asin[" + mode + "]");
                System.out.println(String.format("Result: asin[%s]: %.2f ns/call", mode, meanNs));
                recordAverageBest("asin", mode, meanNs, bestModes, avgTimes);
            }
        } else {
            System.out.println("[Skip] asin mode locked to " + originalAsin);
        }

        // Apply and save best modes
        if (bestModes.containsKey("sqrt")) ModConfig.INSTANCE.sqrtRedirectMode = (SqrtRedirectMode) bestModes.get("sqrt");
        if (bestModes.containsKey("sin"))  ModConfig.INSTANCE.sinRedirectMode = (SinRedirectMode) bestModes.get("sin");
        if (bestModes.containsKey("asin")) ModConfig.INSTANCE.arcSinCosTanRedirectMode = (ArcSinCosTanRedirectMode) bestModes.get("asin");
        ModConfig.save();

        System.out.println("[Benchmark] Complete. Applied modes:");
        bestModes.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
        System.out.println("[Benchmark] Config saved successfully.");
    }

    private static long measure(Supplier<Double> op) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) blackhole = op.get();
        long start = System.nanoTime();
        for (int i = 0; i < MEASURE_ITERATIONS; i++) blackhole = op.get();
        return (System.nanoTime() - start) / MEASURE_ITERATIONS;
    }

    private static double repeatedMeasure(Supplier<Double> op, String label) {
        double sum = 0;
        int checkpoint = REPEAT_MEASUREMENTS / 10;
        for (int i = 1; i <= REPEAT_MEASUREMENTS; i++) {
            sum += measure(op);
            if (i % checkpoint == 0) {
                System.out.println(String.format("[Progress] %s: %d%% complete", label, (i * 100) / REPEAT_MEASUREMENTS));
            }
        }
        return sum / REPEAT_MEASUREMENTS;
    }

    private static void recordAverageBest(String key, Enum<?> mode, double avgTime,
                                          Map<String, Enum<?>> bestModes,
                                          Map<String, Double> avgTimes) {
        if (!bestModes.containsKey(key) || avgTime < avgTimes.get(key + ":" + bestModes.get(key))) {
            bestModes.put(key, mode);
        }
        avgTimes.put(key + ":" + mode, avgTime);
    }

    public static double getBlackhole() { return blackhole; }
    public static void setBlackhole(double bh) { blackhole = bh; }
}

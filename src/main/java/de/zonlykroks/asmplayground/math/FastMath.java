package de.zonlykroks.asmplayground.math;

import de.zonlykroks.asmplayground.impl.ModConfig;
import de.zonlykroks.asmplayground.math.sqrt.FastSqrt;
import de.zonlykroks.asmplayground.math.trig.fast.FastAsinImplementation;
import de.zonlykroks.asmplayground.math.trig.fast.FastTrigImplementation;
import de.zonlykroks.asmplayground.math.trig.PiecewiseSinCosTanImplementation;
import de.zonlykroks.asmplayground.math.trig.RivensFullMathSinCosTanImplementation;
import de.zonlykroks.asmplayground.math.trig.TaylorSinCosTanImplementation;
import org.apache.commons.math4.core.jdkmath.AccurateMath;

@SuppressWarnings("unused")
public class FastMath {

    public static double sqrt(double x) {
        return switch (ModConfig.INSTANCE.sqrtRedirectMode) {
            case LUT -> FastSqrt.sqrt(x);
            case INV_SQRT -> FastSqrt.inversedInvSqrt(x);
        };
    }

    public static double floor(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return x;
        long lx = (long) x;
        double fx = (double) lx;
        return (x < 0 && x != fx) ? fx - 1.0 : fx;
    }

    public static double sin(double x) {
        return switch (ModConfig.INSTANCE.sinRedirectMode) {
            case PIECEWISE -> PiecewiseSinCosTanImplementation.fastPiecewisesin(x);
            case RIVENS -> RivensFullMathSinCosTanImplementation.sin(x);
            case TAYLOR -> TaylorSinCosTanImplementation.taylorsin(x);
            case POLY -> FastTrigImplementation.fastSinPolynomial(x);
            case LUT -> FastTrigImplementation.fastSinLUT(x);
            case APACHE -> AccurateMath.sin(x);
        };
    }

    public static double cos(double x) {
        return switch (ModConfig.INSTANCE.sinRedirectMode) {
            case PIECEWISE -> PiecewiseSinCosTanImplementation.fastPiecewisecos(x);
            case RIVENS -> RivensFullMathSinCosTanImplementation.cos(x);
            case TAYLOR -> TaylorSinCosTanImplementation.taylorcos(x);
            case POLY -> FastTrigImplementation.fastCosPolynomial(x);
            case LUT -> FastTrigImplementation.fastCosLUT(x);
            case APACHE -> AccurateMath.cos(x);
        };
    }

    public static double tan(double x) {
        return switch (ModConfig.INSTANCE.sinRedirectMode) {
            case PIECEWISE -> PiecewiseSinCosTanImplementation.fastPiecewiseTan(x);
            case RIVENS -> RivensFullMathSinCosTanImplementation.tan(x);
            case TAYLOR -> TaylorSinCosTanImplementation.taylortan(x);
            case POLY -> FastTrigImplementation.fastTanPolynomial(x);
            case LUT -> FastTrigImplementation.fastTanLUT(x);
            case APACHE -> AccurateMath.tan(x);
        };
    }

    public static double asin(double x) {
        return switch (ModConfig.INSTANCE.arcSinCosTanRedirectMode) {
            case POLY -> FastAsinImplementation.fastAsinPolynomial(x);
            case LUT -> FastAsinImplementation.fastAsinLUT(x);
            case APACHE -> AccurateMath.asin(x);
        };
    }

    public static double acos(double x) {
        return switch (ModConfig.INSTANCE.arcSinCosTanRedirectMode) {
            case POLY -> FastTrigImplementation.fastAcosPolynomial(x);
            case LUT -> FastTrigImplementation.fastAcosLUT(x);
            case APACHE -> AccurateMath.acos(x);
        };
    }

    public static double atan(double x) {
        return switch (ModConfig.INSTANCE.arcSinCosTanRedirectMode) {
            case POLY -> FastTrigImplementation.fastAtanPolynomial(x);
            case LUT -> FastTrigImplementation.fastAtanLUT(x);
            case APACHE -> AccurateMath.atan(x);
        };
    }
}

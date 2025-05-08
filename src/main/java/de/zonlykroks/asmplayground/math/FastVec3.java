package de.zonlykroks.asmplayground.math;

import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public class FastVec3 {

    public static Vec3 normalize(Vec3 in) {
        // Fast normalize
        double x = in.x;
        double y = in.y;
        double z = in.z;
        double len2 = x * x + y * y + z * z;
        Vec3 resultFast;
        if (len2 < 1.0E-10) {
            resultFast = Vec3.ZERO;
        } else {
            double inv = fastInvSqrt(len2);
            resultFast = new Vec3(x * inv, y * inv, z * inv);
        }

        return resultFast;
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
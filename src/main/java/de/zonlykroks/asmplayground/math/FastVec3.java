package de.zonlykroks.asmplayground.math;

import de.zonlykroks.asmplayground.math.sqrt.FastSqrt;
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
            double inv = FastSqrt.fastInvSqrt(len2);
            resultFast = new Vec3(x * inv, y * inv, z * inv);
        }

        return resultFast;
    }
}
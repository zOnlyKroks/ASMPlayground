package de.zonlykroks.asmplayground.math.collision;

@SuppressWarnings("unused")
public class FastCollision {

    /**
     * Fast AABB intersection test optimized for Minecraft's Box class
     * Avoids unnecessary calculations by using early rejection tests
     */
    public static boolean intersects(double minAX, double minAY, double minAZ,
                                     double maxAX, double maxAY, double maxAZ,
                                     double minBX, double minBY, double minBZ,
                                     double maxBX, double maxBY, double maxBZ) {
        // Early rejection tests - checking axis by axis
        if (maxAX < minBX || minAX > maxBX) return false;
        if (maxAY < minBY || minAY > maxBY) return false;
        return !(maxAZ < minBZ) && !(minAZ > maxBZ);// Boxes intersect
    }

    /**
     * Calculates if a ray intersects with an AABB
     * Returns distance to intersection point or -1 if no intersection
     */
    public static double rayIntersectsBox(
            double originX, double originY, double originZ,
            double dirX, double dirY, double dirZ,
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ) {

        // Calculate inverse of direction to avoid divisions
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        double invDirZ = 1.0 / dirZ;

        // Calculate intersections with bounding planes
        double t1 = (minX - originX) * invDirX;
        double t2 = (maxX - originX) * invDirX;
        double t3 = (minY - originY) * invDirY;
        double t4 = (maxY - originY) * invDirY;
        double t5 = (minZ - originZ) * invDirZ;
        double t6 = (maxZ - originZ) * invDirZ;

        // Get min and max t values for each axis
        double tmin = Math.min(t1, t2);
        double tmax = Math.max(t1, t2);
        tmin = Math.max(tmin, Math.min(t3, t4));
        tmax = Math.min(tmax, Math.max(t3, t4));
        tmin = Math.max(tmin, Math.min(t5, t6));
        tmax = Math.min(tmax, Math.max(t5, t6));

        // Check if there is an intersection
        if (tmax >= tmin && tmax >= 0) {
            return tmin >= 0 ? tmin : tmax;
        }

        return -1.0; // No intersection
    }

}

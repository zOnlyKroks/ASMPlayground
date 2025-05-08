package de.zonlykroks.asmplayground.math.explosion;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;

import java.util.*;

@SuppressWarnings("unused")
public class ExplosionHelper {
    // Precomputed, normalized directions for the 1,352 border samples
    private static final Vec3d[] DIRECTIONS;
    static {
        final int SIZE = 16;
        final double STEP = 1.0 / (SIZE - 1);
        Set<Vec3d> dirs = new HashSet<>(SIZE*SIZE*SIZE);
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    if (x == 0 || x == SIZE-1
                            || y == 0 || y == SIZE-1
                            || z == 0 || z == SIZE-1) {
                        double dx = x * STEP * 2.0 - 1.0;
                        double dy = y * STEP * 2.0 - 1.0;
                        double dz = z * STEP * 2.0 - 1.0;
                        double mag = Math.sqrt(dx*dx + dy*dy + dz*dz);
                        dirs.add(new Vec3d(dx/mag, dy/mag, dz/mag));
                    }
                }
            }
        }
        DIRECTIONS = dirs.toArray(new Vec3d[0]);
    }

    /**
     * Exact‑parity, optimized explosion ray‑trace.
     *
     * @param explosion   the vanilla Explosion instance
     * @param center      the center BlockPos of the explosion
     * @param radius      the explosion radius
     * @param level       the Level; used for block lookups and isInWorldBounds
     * @param calculator  the vanilla ExplosionDamageCalculator
     * @param random      the same Random used by the Explosion
     * @return exactly the same List<BlockPos> as vanilla, but faster and with fewer allocations
     */
    public static ObjectArrayList<BlockPos> calculateExplodedPositions(
            Explosion explosion,
            BlockPos center,
            float radius,
            Level level,
            ExplosionDamageCalculator calculator,
            RandomSource random
    ) {
        ObjectArrayList<BlockPos> result = new ObjectArrayList<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        final float STEP_COST = 0.22500001F;
        final double STEP_LEN = 0.3;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (Vec3d dir : DIRECTIONS) {
            double dx = dir.x;
            double dy = dir.y;
            double dz = dir.z;
            float strength = radius * (0.7F + random.nextFloat() * 0.6F);

            double px = center.getX() + 0.5;
            double py = center.getY() + 0.5;
            double pz = center.getZ() + 0.5;

            while (strength > 0.0F) {
                int xi = (int) Math.floor(px);
                int yi = (int) Math.floor(py);
                int zi = (int) Math.floor(pz);
                mutablePos.set(xi, yi, zi);

                if (!level.isInWorldBounds(mutablePos)) {
                    break;
                }

                BlockState bs = level.getBlockState(mutablePos);
                FluidState fs = level.getFluidState(mutablePos);

                // Avoid Optional boxing
                float resistance = calculator.getBlockExplosionResistance(
                        explosion, level, mutablePos, bs, fs
                ).orElse(0.0F);
                strength -= (resistance + 0.3F) * 0.3F;

                if (strength > 0.0F && calculator.shouldBlockExplode(
                        explosion, level, mutablePos, bs, strength)) {
                    long key = mutablePos.asLong();
                    if (visited.add(key)) {
                        result.add(new BlockPos(xi, yi, zi));
                    }
                }

                px += dx * STEP_LEN;
                py += dy * STEP_LEN;
                pz += dz * STEP_LEN;
                strength -= STEP_COST;
            }
        }

        return result;
    }

    public static void customShuffle(List<BlockPos> list, RandomSource random) {
        Map<BlockPos, Integer> keys = new HashMap<>(list.size());
        for (BlockPos pos : list) {
            keys.put(pos, pos.hashCode() ^ random.nextInt());
        }

        list.sort(Comparator.comparingInt(keys::get));
    }


    // Simple value‑object for precomputed directions
    private record Vec3d(double x, double y, double z) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Vec3d(double x1, double y1, double z1))) return false;
            return Double.compare(x1, x) == 0
                    && Double.compare(y1, y) == 0
                    && Double.compare(z1, z) == 0;
        }

        @Override
        public int hashCode() {
            long bits = Double.doubleToLongBits(x);
            bits = 31 * bits + Double.doubleToLongBits(y);
            bits = 31 * bits + Double.doubleToLongBits(z);
            return Long.hashCode(bits);
        }
    }
}
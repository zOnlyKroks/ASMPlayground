package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.impl.ModConfig;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Replaces the entire explode() method in ServerExplosion to use our optimized
 * ExplosionHelper.calculateExplodedPositions(...) implementation and benchmarks both approaches.
 */
public class ExplosionReplaceTransformer extends ClassVisitor {
    private static final String SERVER_EXPLOSION_CLASS = "net/minecraft/world/level/ServerExplosion";
    private static final String EXPLOSION_HELPER_CLASS = "de/zonlykroks/asmplayground/math/explosion/ExplosionHelper";

    private String currentClass;
    private boolean isTargetClass = false;

    public ExplosionReplaceTransformer(int api, ClassVisitor nextVisitor) {
        super(api, nextVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.currentClass = name;
        this.isTargetClass = name.equals(SERVER_EXPLOSION_CLASS);

        if (this.isTargetClass) {
            System.out.println("Found target class: " + name);
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String methodName,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, methodName, descriptor, signature, exceptions);

        if (ModConfig.INSTANCE.optimizeExplosion && methodName.equals("interactWithBlocks") && descriptor.contains("List")) {
            return new AdviceAdapter(api, mv, access, methodName, descriptor) {
                @Override
                protected void onMethodEnter() {
                    // Replace Util.shuffle with ExplosionHelper.customShuffle
                    visitVarInsn(ALOAD, 1); // Load List<BlockPos> toBlow
                    visitVarInsn(ALOAD, 0); // Load this
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "level", "Lnet/minecraft/server/level/ServerLevel;");
                    visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/server/level/ServerLevel", "random", "Lnet/minecraft/util/RandomSource;");
                    visitMethodInsn(INVOKESTATIC,
                            "de/zonlykroks/asmplayground/math/explosion/ExplosionHelper",
                            "customShuffle",
                            "(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V",
                            false);
                }

                @Override
                public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
                    // INVOKESTATIC net/minecraft/Util.shuffle (Ljava/util/List;Lnet/minecraft/util/RandomSource;)V
                    if(owner.equals("net/minecraft/Util") && name.equals("shuffle") && descriptor.equals("(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V")) return;

                    super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
                }
            };
        }

        // Only target the explode() method in ServerExplosion class
        if (ModConfig.INSTANCE.optimizeExplosion &&
                isTargetClass &&
                methodName.equals("explode") &&
                descriptor.equals("()V")) {

            return new AdviceAdapter(api, mv, access, methodName, descriptor) {
                @Override
                protected void onMethodEnter() {
                    // Don't call the original method, instead override it completely

                    // 1. this.level.gameEvent(this.source, GameEvent.EXPLODE, this.center);
                    // Load Level
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "level", "Lnet/minecraft/server/level/ServerLevel;");

                    // Load source
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "source", "Lnet/minecraft/world/entity/Entity;");

                    // Load GameEvent.EXPLODE as Holder$Reference
                    visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/world/level/gameevent/GameEvent", "EXPLODE", "Lnet/minecraft/core/Holder$Reference;");

                    // Load center
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "center", "Lnet/minecraft/world/phys/Vec3;");

                    // Call gameEvent with correct signature for Holder parameter
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            "net/minecraft/server/level/ServerLevel",
                            "gameEvent",
                            "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;)V",
                            false);

                    // ----------- BENCHMARKING START -----------

                    // Create local variables for benchmarking
                    int vanillaStartTimeVar = newLocal(org.objectweb.asm.Type.getType("J")); // long vanillaStartTime
                    int vanillaEndTimeVar = newLocal(org.objectweb.asm.Type.getType("J")); // long vanillaEndTime
                    int optimizedStartTimeVar = newLocal(org.objectweb.asm.Type.getType("J")); // long optimizedStartTime
                    int optimizedEndTimeVar = newLocal(org.objectweb.asm.Type.getType("J")); // long optimizedEndTime
                    int vanillaListVar = newLocal(org.objectweb.asm.Type.getType("Ljava/util/List;")); // List<BlockPos> vanillaList
                    int optimizedListVar = newLocal(org.objectweb.asm.Type.getType("Ljava/util/List;")); // List<BlockPos> optimizedList

                    // ----------- BENCHMARK VANILLA IMPLEMENTATION -----------

                    // Record start time
                    visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    visitVarInsn(Opcodes.LSTORE, vanillaStartTimeVar);

                    // Call original calculateExplodedPositions method
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            SERVER_EXPLOSION_CLASS,
                            "calculateExplodedPositions",
                            "()Ljava/util/List;",
                            false);
                    visitVarInsn(Opcodes.ASTORE, vanillaListVar);

                    // Record end time
                    visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    visitVarInsn(Opcodes.LSTORE, vanillaEndTimeVar);

                    // ----------- BENCHMARK OPTIMIZED IMPLEMENTATION -----------

                    // Record start time
                    visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    visitVarInsn(Opcodes.LSTORE, optimizedStartTimeVar);

                    // Call our optimized implementation
                    // ExplosionHelper.calculateExplodedPositions(this, new BlockPos(int, int, int), this.radius, this.level, this.damageCalculator, this.level.random)

                    // Load explosion (this)
                    visitVarInsn(Opcodes.ALOAD, 0);

                    // Convert this.center (Vec3) → new BlockPos(int, int, int)
                    // Create a new BlockPos instance using the constructor with integer coordinates
                    visitTypeInsn(Opcodes.NEW, "net/minecraft/core/BlockPos");
                    visitInsn(Opcodes.DUP);

                    // Load the center Vec3
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "center", "Lnet/minecraft/world/phys/Vec3;");

                    // Get Vec3.x as double
                    visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/Vec3", "x", "D");
                    // Convert double to int
                    visitInsn(Opcodes.D2I);

                    // Load the center Vec3 again
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "center", "Lnet/minecraft/world/phys/Vec3;");

                    // Get Vec3.y as double
                    visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/Vec3", "y", "D");
                    // Convert double to int
                    visitInsn(Opcodes.D2I);

                    // Load the center Vec3 again
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "center", "Lnet/minecraft/world/phys/Vec3;");

                    // Get Vec3.z as double
                    visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/Vec3", "z", "D");
                    // Convert double to int
                    visitInsn(Opcodes.D2I);

                    // Call the BlockPos constructor with int, int, int parameters
                    visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "net/minecraft/core/BlockPos",
                            "<init>",
                            "(III)V",
                            false);

                    // Push radius
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "radius", "F");

                    // Push level
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "level", "Lnet/minecraft/server/level/ServerLevel;");

                    // Push damageCalculator
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "damageCalculator", "Lnet/minecraft/world/level/ExplosionDamageCalculator;");

                    // Push random: this.level.random
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "level", "Lnet/minecraft/server/level/ServerLevel;");
                    visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/server/level/ServerLevel", "random", "Lnet/minecraft/util/RandomSource;");

                    // Call the optimized method
                    visitMethodInsn(Opcodes.INVOKESTATIC,
                            EXPLOSION_HELPER_CLASS,
                            "calculateExplodedPositions",
                            "(Lnet/minecraft/world/level/Explosion;" +
                                    "Lnet/minecraft/core/BlockPos;" +
                                    "F" +
                                    "Lnet/minecraft/world/level/Level;" +
                                    "Lnet/minecraft/world/level/ExplosionDamageCalculator;" +
                                    "Lnet/minecraft/util/RandomSource;" +
                                    ")Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
                            false);

                    // Store optimized result in local variable
                    visitVarInsn(Opcodes.ASTORE, optimizedListVar);

                    // Record end time
                    visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    visitVarInsn(Opcodes.LSTORE, optimizedEndTimeVar);

                    // ----------- LOG BENCHMARK RESULTS -----------

                    // Log the benchmark results using System.out.println()
                    // First, create the StringBuilder for our message
                    visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                    visitInsn(Opcodes.DUP);
                    visitLdcInsn("Benchmark Results - Vanilla: ");
                    visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);

                    // Append vanilla execution time
                    visitVarInsn(Opcodes.LLOAD, vanillaEndTimeVar);
                    visitVarInsn(Opcodes.LLOAD, vanillaStartTimeVar);
                    visitInsn(Opcodes.LSUB);
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);

                    // Append " ns, Optimized: "
                    visitLdcInsn(" ns, Optimized: ");
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                    // Append optimized execution time
                    visitVarInsn(Opcodes.LLOAD, optimizedEndTimeVar);
                    visitVarInsn(Opcodes.LLOAD, optimizedStartTimeVar);
                    visitInsn(Opcodes.LSUB);
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);

                    // Append " ns, Speed Improvement: "
                    visitLdcInsn(" ns, Speed Improvement: ");
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                    // Calculate speed improvement ratio
                    visitVarInsn(Opcodes.LLOAD, vanillaEndTimeVar);
                    visitVarInsn(Opcodes.LLOAD, vanillaStartTimeVar);
                    visitInsn(Opcodes.LSUB);
                    visitInsn(Opcodes.L2D); // Convert to double for division

                    visitVarInsn(Opcodes.LLOAD, optimizedEndTimeVar);
                    visitVarInsn(Opcodes.LLOAD, optimizedStartTimeVar);
                    visitInsn(Opcodes.LSUB);
                    visitInsn(Opcodes.L2D); // Convert to double

                    visitInsn(Opcodes.DDIV); // Divide to get ratio
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false);

                    // Append "x"
                    visitLdcInsn("x");
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                    // Convert StringBuilder to String
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

                    // Call System.out.println
                    visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    visitInsn(Opcodes.SWAP); // Swap the top two stack values (PrintStream and our string)
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                    // ----------- CONTINUE WITH ACTUAL EXPLOSION USING OPTIMIZED IMPLEMENTATION -----------

                    // 3. this.hurtEntities();
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            SERVER_EXPLOSION_CLASS,
                            "hurtEntities",
                            "()V",
                            false);

                    // 4. if (this.interactsWithBlocks()) {
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            SERVER_EXPLOSION_CLASS,
                            "interactsWithBlocks",
                            "()Z",
                            false);

                    org.objectweb.asm.Label skipInteractLabel = new org.objectweb.asm.Label();
                    visitJumpInsn(Opcodes.IFEQ, skipInteractLabel);

                    // ProfilerFiller profilerFiller = Profiler.get();
                    visitMethodInsn(Opcodes.INVOKESTATIC,
                            "net/minecraft/util/profiling/Profiler",
                            "get",
                            "()Lnet/minecraft/util/profiling/ProfilerFiller;",
                            false);
                    int profilerVar = newLocal(org.objectweb.asm.Type.getType("Lnet/minecraft/util/profiling/ProfilerFiller;"));
                    visitVarInsn(Opcodes.ASTORE, profilerVar);

                    // profilerFiller.push("explosion_blocks");
                    visitVarInsn(Opcodes.ALOAD, profilerVar);
                    visitLdcInsn("explosion_blocks");
                    visitMethodInsn(Opcodes.INVOKEINTERFACE,
                            "net/minecraft/util/profiling/ProfilerFiller",
                            "push",
                            "(Ljava/lang/String;)V",
                            true);

                    // Use the optimized list for actual explosion processing
                    // this.interactWithBlocks(optimizedList);
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitVarInsn(Opcodes.ALOAD, optimizedListVar); // optimized list
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            SERVER_EXPLOSION_CLASS,
                            "interactWithBlocks",
                            "(Ljava/util/List;)V",
                            false);

                    // profilerFiller.pop();
                    visitVarInsn(Opcodes.ALOAD, profilerVar);
                    visitMethodInsn(Opcodes.INVOKEINTERFACE,
                            "net/minecraft/util/profiling/ProfilerFiller",
                            "pop",
                            "()V",
                            true);

                    visitLabel(skipInteractLabel);

                    // 5. if (this.fire) {
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitFieldInsn(Opcodes.GETFIELD, SERVER_EXPLOSION_CLASS, "fire", "Z");

                    org.objectweb.asm.Label skipFireLabel = new org.objectweb.asm.Label();
                    visitJumpInsn(Opcodes.IFEQ, skipFireLabel);

                    // this.createFire(optimizedList);
                    visitVarInsn(Opcodes.ALOAD, 0); // this
                    visitVarInsn(Opcodes.ALOAD, optimizedListVar); // optimized list
                    visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            SERVER_EXPLOSION_CLASS,
                            "createFire",
                            "(Ljava/util/List;)V",
                            false);

                    visitLabel(skipFireLabel);

                    // Return
                    visitInsn(Opcodes.RETURN);
                }
            };
        }

        return mv;
    }
}
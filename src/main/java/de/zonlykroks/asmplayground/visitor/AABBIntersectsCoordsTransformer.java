package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.impl.ModConfig;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AABBIntersectsCoordsTransformer extends ClassVisitor {
    public AABBIntersectsCoordsTransformer(int api, ClassVisitor nextVisitor) {
        super(api, nextVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String methodName,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, methodName, descriptor, signature, exceptions);

        // Target Minecraft's AABB.intersects(double, double, double, double, double, double) method
        if (ModConfig.INSTANCE.optimizeAABBIntersectsCoords &&
                methodName.equals("intersects") &&
                descriptor.equals("(DDDDDD)Z")) {

            return new MethodVisitor(api, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();

                    // Load this AABB's fields
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/AABB", "minX", "D");
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/AABB", "minY", "D");
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/AABB", "minZ", "D");
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/AABB", "maxX", "D");
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/AABB", "maxY", "D");
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/world/phys/AABB", "maxZ", "D");

                    // Load method parameters
                    mv.visitVarInsn(Opcodes.DLOAD, 1); // d - minX
                    mv.visitVarInsn(Opcodes.DLOAD, 3); // e - minY
                    mv.visitVarInsn(Opcodes.DLOAD, 5); // f - minZ
                    mv.visitVarInsn(Opcodes.DLOAD, 7); // g - maxX
                    mv.visitVarInsn(Opcodes.DLOAD, 9); // h - maxY
                    mv.visitVarInsn(Opcodes.DLOAD, 11); // i - maxZ

                    // Call our optimized method
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "de/zonlykroks/asmplayground/math/collision/FastCollision",
                            "intersects",
                            "(DDDDDDDDDDDD)Z",
                            false);

                    mv.visitInsn(Opcodes.IRETURN);
                    mv.visitMaxs(13, 13); // 12 doubles + 1 reference, 13 local variables (this + 6 doubles as params)
                }
            };
        }
        return mv;
    }
}
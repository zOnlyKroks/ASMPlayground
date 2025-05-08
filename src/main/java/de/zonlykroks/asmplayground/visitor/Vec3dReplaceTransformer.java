package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.impl.ModConfig;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Vec3dReplaceTransformer extends ClassVisitor {

    public Vec3dReplaceTransformer(int api, ClassVisitor nextVisitor) {
        super(api, nextVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String methodName,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, methodName, descriptor, signature, exceptions);
        return new MethodVisitor(api, mv) {
            @Override
            public void visitMethodInsn(int opcode,
                                        String owner,
                                        String name,
                                        String desc,
                                        boolean isInterface) {
                if (ModConfig.INSTANCE.optimizeVecNormalize
                        && opcode == Opcodes.INVOKEVIRTUAL
                        && owner.equals("net/minecraft/world/phys/Vec3")
                        && name.equals("normalize")
                        && desc.equals("()Lnet/minecraft/world/phys/Vec3;")) {
                    // instead of invokevirtual, call your static helper:
                    super.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "de/zonlykroks/asmplayground/math/FastVec3",   // your helper class
                            "normalize",                                    // method name
                            "(Lnet/minecraft/world/phys/Vec3;)" +          // takes Vec3
                                    "Lnet/minecraft/world/phys/Vec3;",              // returns Vec3
                            false);
                    return;
                }

                // Default behavior
                super.visitMethodInsn(opcode, owner, name, desc, isInterface);
            }
        };
    }

}

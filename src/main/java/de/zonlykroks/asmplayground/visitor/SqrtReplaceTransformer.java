package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.impl.ModConfig;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SqrtReplaceTransformer extends ClassVisitor {
    public SqrtReplaceTransformer(int api, ClassVisitor nextVisitor) {
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
                // Replace Math.sqrt(double)
                if (ModConfig.INSTANCE.optimizeMathSqrt && opcode == Opcodes.INVOKESTATIC &&
                        owner.equals("java/lang/Math") &&
                        name.equals("sqrt") &&
                        desc.equals("(D)D")) {

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "de/zonlykroks/asmplayground/math/FastMath",
                            "sqrt",
                            "(D)D",
                            false);
                    return;
                }

                // Replace Math.floor(double)
                if (ModConfig.INSTANCE.optimizeMathFloor && opcode == Opcodes.INVOKESTATIC &&
                        owner.equals("java/lang/Math") &&
                        name.equals("floor") &&
                        desc.equals("(D)D")) {

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "de/zonlykroks/asmplayground/math/FastMath",
                            "floor",
                            "(D)D",
                            false);
                    return;
                }

                // Default behavior
                super.visitMethodInsn(opcode, owner, name, desc, isInterface);
            }
        };
    }
}

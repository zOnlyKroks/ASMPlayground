package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.impl.ModConfig;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SinCosTanReplaceTransformer extends ClassVisitor {
    private String currentClass;

    public SinCosTanReplaceTransformer(int api, ClassVisitor nextVisitor) {
        super(api, nextVisitor);
    }

    @Override
    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces) {
        // Record the class name to avoid transforming within Mth itself
        this.currentClass = name;
        super.visit(version, access, name, signature, superName, interfaces);
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
                // Only transform sin, cos, tan and skip inside Mth class itself
                boolean isMthFloat = opcode == Opcodes.INVOKESTATIC
                        && "net/minecraft/util/Mth".equals(owner)
                        && ("sin".equals(name) || "cos".equals(name) || "tan".equals(name))
                        && "(F)F".equals(desc);
                boolean isMathDouble = opcode == Opcodes.INVOKESTATIC
                        && ("java/lang/Math".equals(owner) || "java/lang/StrictMath".equals(owner))
                        && ("sin".equals(name) || "cos".equals(name) || "tan".equals(name))
                        && "(D)D".equals(desc);

                if (!ModConfig.INSTANCE.sinRedirectEnabled
                        || "net/minecraft/util/Mth".equals(currentClass)
                        || (!isMthFloat && !isMathDouble)) {
                    super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                    return;
                }

                // For float sin/cos/tan, widen the argument to double
                if (isMthFloat) {
                    super.visitInsn(Opcodes.F2D);
                }

                // Redirect invocation to FastMath's double methods
                super.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "de/zonlykroks/asmplayground/math/FastMath",
                        name,
                        "(D)D",
                        false
                );

                // For float original, narrow the result back to float
                if (isMthFloat) {
                    super.visitInsn(Opcodes.D2F);
                }
            }
        };
    }
}

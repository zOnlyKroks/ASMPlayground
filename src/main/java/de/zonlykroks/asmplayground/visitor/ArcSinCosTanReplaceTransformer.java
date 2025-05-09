package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.impl.ModConfig;
import net.minecraft.util.Mth;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ArcSinCosTanReplaceTransformer extends ClassVisitor {
    private String currentClass;

    public ArcSinCosTanReplaceTransformer(int api, ClassVisitor nextVisitor) {
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
                boolean isMathDouble = opcode == Opcodes.INVOKESTATIC
                        && ("java/lang/Math".equals(owner) || "java/lang/StrictMath".equals(owner))
                        && ("asin".equals(name) || "acos".equals(name) || "atan".equals(name))
                        && "(D)D".equals(desc);

                if (!ModConfig.INSTANCE.sinRedirectEnabled || !isMathDouble) {
                    super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                    return;
                }

                // Redirect invocation to FastMath's double methods
                super.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "de/zonlykroks/asmplayground/math/FastMath",
                        name,
                        "(D)D",
                        false
                );
            }
        };
    }
}

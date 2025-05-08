package de.zonlykroks.asmplayground.visitor;

import de.zonlykroks.asmplayground.MassASMEntrypoint;
import de.zonlykroks.asmplayground.config.SinRedirectMode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SinReplaceTransformer extends ClassVisitor {
    public SinReplaceTransformer(int api, ClassVisitor nextVisitor) {
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
                SinRedirectMode mode = MassASMEntrypoint.config.redirectMode;

                boolean isFloat = "(F)F".equals(desc);
                boolean isDouble = "(D)D".equals(desc);

                boolean isTarget = opcode == Opcodes.INVOKESTATIC &&
                        (
                                ("net/minecraft/util/Mth".equals(owner) && isFloat) ||
                                        (("java/lang/Math".equals(owner) || "java/lang/StrictMath".equals(owner)) && isDouble)
                        );

                if (!isTarget) {
                    super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                    return;
                }

                // Map redirect mode to class and method prefix
                String implOwner = switch (mode) {
                    case PIECEWISE -> "de/zonlykroks/asmplayground/math/PiecewiseSinCosTanImplementation";
                    case TAYLOR -> "de/zonlykroks/asmplayground/math/TaylorSinCosTanImplementation";
                    case RIVENS -> "de/zonlykroks/asmplayground/math/RivensFullMathSinCosTanImplementation";
                };

                String prefix = switch (mode) {
                    case PIECEWISE -> "fastPiecewise";
                    case TAYLOR -> "taylor";
                    case RIVENS -> "";
                };

                String methodName = switch (name) {
                    case "sin" -> prefix + "Sin";
                    case "cos" -> prefix + "Cos";
                    case "tan" -> prefix + "Tan";
                    default -> null;
                };

                if (methodName == null) {
                    super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                    return;
                }

                // Always call the (D)D method
                if (isFloat) {
                    super.visitInsn(Opcodes.F2D); // widen float to double
                }

                super.visitMethodInsn(Opcodes.INVOKESTATIC, implOwner, methodName, "(D)D", false);

                if (isFloat) {
                    super.visitInsn(Opcodes.D2F); // narrow result back to float
                }
            }
        };
    }
}

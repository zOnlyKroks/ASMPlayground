package de.zonlykroks.asmplayground;

import de.zonlykroks.asmplayground.visitor.ExplosionReplaceTransformer;
import de.zonlykroks.asmplayground.visitor.SinReplaceTransformer;
import de.zonlykroks.asmplayground.visitor.SqrtReplaceTransformer;
import de.zonlykroks.asmplayground.visitor.Vec3dReplaceTransformer;
import de.zonlykroks.massasmer.MassASMTransformer;
import de.zonlykroks.massasmer.filter.Filters;
import org.objectweb.asm.Opcodes;

public class MassASMEntrypoint implements Runnable {


    @Override
    public void run() {
        System.out.println("MassASMEntrypoint started");

        MassASMTransformer.registerVisitor(
                "faster-math-sin-cos-tan",
                Filters.contains("net.minecraft"),
                (className, nextVisitor) -> new SinReplaceTransformer(Opcodes.ASM9, nextVisitor)
        );

        MassASMTransformer.registerVisitor(
                "faster-math-sqrt",
                Filters.contains("net.minecraft"),
                (className, nextVisitor) -> new SqrtReplaceTransformer(Opcodes.ASM9, nextVisitor)
        );

        MassASMTransformer.registerVisitor(
                "faster-vec3d-normalize",
                Filters.contains("net.minecraft"),
                (className, nextVisitor) -> new Vec3dReplaceTransformer(Opcodes.ASM9, nextVisitor)
        );

        MassASMTransformer.registerVisitor(
                "faster-explosion-calc",
                Filters.contains("net.minecraft"),
                (className, nextVisitor) -> new ExplosionReplaceTransformer(Opcodes.ASM9, nextVisitor)
        );
    }
}

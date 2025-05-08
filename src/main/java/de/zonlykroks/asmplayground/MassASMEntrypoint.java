package de.zonlykroks.asmplayground;

import de.zonlykroks.asmplayground.config.ModConfig;
import de.zonlykroks.asmplayground.visitor.SinReplaceTransformer;
import de.zonlykroks.massasmer.MassASMTransformer;
import de.zonlykroks.massasmer.filter.Filters;
import org.objectweb.asm.Opcodes;

public class MassASMEntrypoint implements Runnable {

    public static final ModConfig config = ModConfig.load();

    @Override
    public void run() {
        System.out.println("MassASMEntrypoint started");

        MassASMTransformer.registerVisitor(
                "faster-math-sin-cos-tan",
                Filters.contains("net.minecraft"),
                (className, nextVisitor) -> new SinReplaceTransformer(Opcodes.ASM9, nextVisitor)
        );
    }
}

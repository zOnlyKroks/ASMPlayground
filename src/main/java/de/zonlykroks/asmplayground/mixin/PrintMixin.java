package de.zonlykroks.asmplayground.mixin;

import net.minecraft.util.ParticleUtils;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleUtils.class)
@Debug(export = true)
public class ParticleUtilsMixin {
}

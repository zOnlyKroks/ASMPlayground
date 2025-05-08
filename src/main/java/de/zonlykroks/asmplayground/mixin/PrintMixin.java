package de.zonlykroks.asmplayground.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
@Debug(export = true)
public class PrintMixin {
}

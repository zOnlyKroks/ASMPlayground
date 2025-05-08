package de.zonlykroks.asmplayground.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerExplosion.class)
@Debug(export = true)
public class PrintMixin {
}

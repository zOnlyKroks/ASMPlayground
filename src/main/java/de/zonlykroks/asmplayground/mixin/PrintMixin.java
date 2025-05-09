package de.zonlykroks.asmplayground.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
@Debug(export = true)
public class PrintMixin {
}

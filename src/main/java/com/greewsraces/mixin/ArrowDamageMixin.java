package com.greewsraces.mixin;

import com.greewsraces.RaceWeaponDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowDamageMixin {

    @Inject(method = "getDamage", at = @At("RETURN"), cancellable = true)
    private void greewsraces$scaleProjectileDamage(CallbackInfoReturnable<Double> cir) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        Entity owner = projectile.getOwner();
        if (!(owner instanceof PlayerEntity player)) {
            return;
        }
        cir.setReturnValue(RaceWeaponDamage.multiplyProjectile(player, cir.getReturnValue()));
    }
}

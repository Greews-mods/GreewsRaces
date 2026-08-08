package com.greewsraces.mixin;

import com.greewsraces.RaceWeaponDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowDamageMixin {

    @Shadow
    private double damage;

    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void greewsraces$scaleProjectileHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        Entity owner = projectile.getOwner();
        if (owner instanceof PlayerEntity player) {
            this.damage = RaceWeaponDamage.multiplyProjectile(player, this.damage);
        }
    }
}

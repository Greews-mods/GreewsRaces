package com.greewsraces.mixin;

import com.greewsraces.WandService;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public abstract class MagicOrbDamageMixin {
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void greewsraces$damageWithMagicOrb(EntityHitResult hitResult, CallbackInfo ci) {
        if (hitResult.getEntity() instanceof LivingEntity target) {
            WandService.hit((SnowballEntity) (Object) this, target);
        }
    }
}

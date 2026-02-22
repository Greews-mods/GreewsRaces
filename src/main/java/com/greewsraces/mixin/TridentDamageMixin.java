package com.greewsraces.mixin;

import com.greewsraces.PlayerDataManager;
import com.greewsraces.Race;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentDamageMixin {

    /**
     * Víla dostává +20% poškození s tridentem.
     * MC 1.21.10: getEntityWorld() místo getWorld()
     */
    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void onTridentEntityHit(EntityHitResult result, CallbackInfo ci) {
        TridentEntity trident = (TridentEntity) (Object) this;
        Entity owner = trident.getOwner();

        if (owner instanceof PlayerEntity player) {
            if (PlayerDataManager.hasRaceSelected(player)) {
                Race race = Race.fromId(PlayerDataManager.getRace(player));

                if (race == Race.FAIRY) {
                    Entity target = result.getEntity();
                    if (!player.getEntityWorld().isClient()) {
                        ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();
                        // +20% bonus damage (base trident damage ~8, bonus = 1.6)
                        target.damage(
                            serverWorld,
                            serverWorld.getDamageSources().thrown(trident, player),
                            1.6f
                        );
                    }
                }
            }
        }
    }
}
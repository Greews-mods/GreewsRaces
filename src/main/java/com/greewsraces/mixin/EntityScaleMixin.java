package com.greewsraces.mixin;

import com.greewsraces.PlayerDataManager;
import com.greewsraces.Race;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** MC 1.20.1 has no scale attribute; apply height via dimensions. */
@Mixin(PlayerEntity.class)
public abstract class EntityScaleMixin {

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void greewsraces$raceHeightScale(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!PlayerDataManager.hasRaceSelected(player)) {
            return;
        }
        Race race = Race.fromId(PlayerDataManager.getRace(player));
        if (race.getHeightScale() == 1.0) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue().scaled((float) race.getHeightScale()));
    }
}

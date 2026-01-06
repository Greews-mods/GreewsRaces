package com.greewsraces.mixin;

import com.greewsraces.PlayerDataManager;
import com.greewsraces.Race;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MiningSpeedMixin {
    
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyMiningSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));
            
            // Aplikujeme modifikátor rychlosti těžení
            float originalSpeed = cir.getReturnValue();
            float modifiedSpeed = (float) (originalSpeed * race.getMiningSpeed());
            cir.setReturnValue(modifiedSpeed);
        }
    }
}
package com.greewsraces.mixin;

import com.greewsraces.PlayerDataManager;
import com.greewsraces.Race;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(Camera.class)
public abstract class CameraEyeHeightMixin {

    @Shadow
    private Entity focusedEntity;

    @ModifyExpressionValue(
        method = "updateEyeHeight",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getStandingEyeHeight()F"
        )
    )
    private float greewsraces$keepVanillaFirstPersonHeight(float scaledEyeHeight) {
        if (!(focusedEntity instanceof PlayerEntity player)
            || player != MinecraftClient.getInstance().player
            || !PlayerDataManager.hasRaceSelected(player)) {
            return scaledEyeHeight;
        }

        double raceScale = Race.fromId(PlayerDataManager.getRace(player)).getHeightScale();
        return raceScale > 0.0 ? (float) (scaledEyeHeight / raceScale) : scaledEyeHeight;
    }
}

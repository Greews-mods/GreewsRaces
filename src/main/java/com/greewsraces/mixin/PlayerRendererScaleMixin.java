package com.greewsraces.mixin;

import com.greewsraces.PlayerDataManager;
import com.greewsraces.Race;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC 1.20.1: scale the rendered player model (getDimensions alone does not change visuals). */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererScaleMixin {

    @Inject(method = "scale", at = @At("TAIL"))
    private void greewsraces$raceRenderScale(AbstractClientPlayerEntity player, MatrixStack matrices, float amount, CallbackInfo ci) {
        if (!PlayerDataManager.hasRaceSelected(player)) {
            return;
        }
        Race race = Race.fromId(PlayerDataManager.getRace(player));
        float scale = (float) race.getHeightScale();
        if (scale != 1.0F) {
            matrices.scale(scale, scale, scale);
        }
    }
}

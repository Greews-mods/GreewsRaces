package com.greewsraces.mixin;

import com.greewsraces.ClientRaceStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public abstract class FairyFoodHudMixin {
    @ModifyVariable(
        method = "renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V",
        at = @At(value = "STORE", ordinal = 6),
        index = 12
    )
    private int greewsraces$moveFoodBarToArmorRow(int top) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && "fairy".equals(ClientRaceStorage.getRace(client.player.getUuid()))) {
            return top - 10;
        }
        return top;
    }
}

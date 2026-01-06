package com.greewsraces.mixin;

import com.greewsraces.*;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void addRacePrefixToTab(CallbackInfoReturnable<Text> cir) {
        PlayerListEntry entry = (PlayerListEntry) (Object) this;
        UUID playerId = entry.getProfile().id();
        
        if (ClientRaceStorage.hasRace(playerId)) {
            String raceId = ClientRaceStorage.getRace(playerId);
            Race race = Race.fromId(raceId);
            
            Language viewerLanguage = ClientLanguageStorage.getLanguage();
            String raceName = Translation.get("race." + race.getId(), viewerLanguage);
            
            Text originalName = cir.getReturnValue();
            if (originalName == null) {
                originalName = Text.literal(entry.getProfile().name());
            }
            
            MutableText prefix = Text.literal("[" + raceName + "] ")
                .styled(style -> style.withColor(race.getColor()));
            
            MutableText whiteName = Text.empty()
                .append(originalName)
                .styled(style -> style.withColor(0xFFFFFF));
            
            MutableText newName = prefix.append(whiteName);
            cir.setReturnValue(newName);
        }
    }
}
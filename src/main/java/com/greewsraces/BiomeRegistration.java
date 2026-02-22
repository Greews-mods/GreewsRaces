package com.greewsraces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

public class BiomeRegistration implements TerraBlenderApi {

    /**
     * Tato metoda je volána TerraBlenderem ve správný čas – po načtení jeho configu.
     * NEVOLAT z GreewsRaces.onInitialize()!
     */
    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new EvernightRegion(
            Identifier.of("greewsraces", "overworld"),
            RegionType.OVERWORLD,
            1
        ));
        GreewsRaces.LOGGER.info("Evernight biome registered via TerraBlender");
    }

    public static boolean isInEvernightBiome(PlayerEntity player) {
        if (player.getEntityWorld() == null) return false;
        var biomeEntry = player.getEntityWorld().getBiome(player.getBlockPos());
        return biomeEntry.matchesKey(EvernightBiome.EVERNIGHT);
    }
}
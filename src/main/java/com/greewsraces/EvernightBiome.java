package com.greewsraces;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

/**
 * Datapack biome {@code data/greewsraces/worldgen/biome/evernight.json} — Java only exposes the registry key.
 */
public class EvernightBiome {

    public static final RegistryKey<Biome> EVERNIGHT = RegistryKey.of(
        RegistryKeys.BIOME,
        Identifier.of("greewsraces", "evernight")
    );
}

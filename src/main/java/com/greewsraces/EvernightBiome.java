package com.greewsraces;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class EvernightBiome {

    public static final RegistryKey<Biome> EVERNIGHT = RegistryKey.of(
        RegistryKeys.BIOME,
        Identifier.of("greewsraces", "evernight")
    );

    public static Biome create() {
        return new Biome.Builder()
            .precipitation(true)
            .temperature(0.3f)
            .downfall(0.6f)
            .effects(new BiomeEffects.Builder()
                .skyColor(655381)
                .fogColor(1706542)
                .waterColor(1706542)
                .waterFogColor(851991)
                .foliageColor(1717024)
                .grassColor(1715738)
                .moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0))
                .additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111))
                .build())
            .spawnSettings(new SpawnSettings.Builder().build())
            .generationSettings(new GenerationSettings.Builder().build())
            .build();
    }
}
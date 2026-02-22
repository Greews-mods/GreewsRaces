package com.greewsraces;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class EvernightRegion extends Region {

    public EvernightRegion(Identifier name, RegionType type, int weight) {
        super(name, type, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
        addBiome(mapper,
            MultiNoiseUtil.ParameterRange.of(-0.5f, -0.1f), // temperature - chladné
            MultiNoiseUtil.ParameterRange.of(0.1f, 0.5f),   // humidity - vlhké
            MultiNoiseUtil.ParameterRange.of(-0.1f, 0.5f),  // continentalness
            MultiNoiseUtil.ParameterRange.of(-0.1f, 0.1f),  // erosion
            MultiNoiseUtil.ParameterRange.of(0.0f, 0.0f),   // depth
            MultiNoiseUtil.ParameterRange.of(0.0f, 0.0f),   // weirdness
            0.0f,
            EvernightBiome.EVERNIGHT
        );
    }
}
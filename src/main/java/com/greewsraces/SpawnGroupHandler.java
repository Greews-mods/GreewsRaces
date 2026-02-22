package com.greewsraces;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;

public class SpawnGroupHandler {

    private static final int MAX_BATS_NEARBY = 3;
    private static final int SPAWN_RADIUS = 20;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 800 != 0) return; // každých 40 sekund

            Random random = world.getRandom();

            for (var player : world.getPlayers()) {
                if (!BiomeRegistration.isInEvernightBiome(player)) continue;

                Box searchBox = Box.of(player.getBlockPos().toCenterPos(), SPAWN_RADIUS * 2, SPAWN_RADIUS, SPAWN_RADIUS * 2);   
                int nearbyBats = world.getEntitiesByType(EntityType.BAT, searchBox, e -> true).size();

                if (nearbyBats >= MAX_BATS_NEARBY) continue;

                int count = 1 + random.nextInt(1); // 1-2 netopýři
                for (int i = 0; i < count; i++) {
                    int x = (int) player.getX() + random.nextBetween(-SPAWN_RADIUS, SPAWN_RADIUS);
                    int z = (int) player.getZ() + random.nextBetween(-SPAWN_RADIUS, SPAWN_RADIUS);
                    int y = (int) player.getY() + random.nextBetween(2, 8);

                    BlockPos pos = new BlockPos(x, y, z);
                    if (!world.isAir(pos)) continue;

                    BatEntity bat = EntityType.BAT.create(world, SpawnReason.NATURAL);
                    if (bat == null) continue;

                    bat.refreshPositionAndAngles(pos, 0, 0);
                    world.spawnEntity(bat);
                }
            }
        });
    }
}
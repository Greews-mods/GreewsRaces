package com.greewsraces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class PlayerDataManager {
    
    public static void setRace(PlayerEntity player, String raceId) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            World world = serverPlayer.getEntityWorld();
            if (world instanceof ServerWorld serverWorld) {
                GreewsWorldState state = GreewsWorldState.get(serverWorld);
                state.setRace(player.getUuid(), raceId);
            }
        }
    }
    
    public static String getRace(PlayerEntity player) {
        if (player.getEntityWorld().isClient()) {
            return ClientRaceStorage.getRace(player.getUuid());
        }
        
        if (player instanceof ServerPlayerEntity serverPlayer) {
            World world = serverPlayer.getEntityWorld();
            if (world instanceof ServerWorld serverWorld) {
                GreewsWorldState state = GreewsWorldState.get(serverWorld);
                return state.getRace(player.getUuid());
            }
        }
        return "";
    }
    
    public static boolean hasRaceSelected(PlayerEntity player) {
        if (player.getEntityWorld().isClient()) {
            return ClientRaceStorage.hasRace(player.getUuid());
        }
        
        if (player instanceof ServerPlayerEntity serverPlayer) {
            World world = serverPlayer.getEntityWorld();
            if (world instanceof ServerWorld serverWorld) {
                GreewsWorldState state = GreewsWorldState.get(serverWorld);
                return state.hasRace(player.getUuid());
            }
        }
        return false;
    }
    
    public static void loadPlayerData(PlayerEntity player) {
        String raceId = getRace(player);
        if (raceId != null && !raceId.isEmpty()) {
            RaceHandler.applyRaceAttributes(player, raceId);
            GreewsRaces.LOGGER.info("Loaded race {} for player {}", raceId, player.getName().getString());
        }
    }
}
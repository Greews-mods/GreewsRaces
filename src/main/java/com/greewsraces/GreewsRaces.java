package com.greewsraces;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreewsRaces implements ModInitializer {
    public static final String MOD_ID = "greewsraces";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
   public void onInitialize() {
    LOGGER.info("GreewsRaces mod initialized!");
    
    PayloadTypeRegistry.playC2S().register(RaceSelectionPayload.ID, RaceSelectionPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(LanguageSelectionPayload.ID, LanguageSelectionPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(RaceSyncPayload.ID, RaceSyncPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(PlayerRaceSyncPayload.ID, PlayerRaceSyncPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(PlayerLanguageSyncPayload.ID, PlayerLanguageSyncPayload.CODEC);
    
    ServerPlayNetworking.registerGlobalReceiver(RaceSelectionPayload.ID, (payload, context) -> {
        context.server().execute(() -> {
            ServerPlayerEntity player = context.player();
            World world = player.getEntityWorld();
            
            if (world instanceof ServerWorld serverWorld) {
                GreewsWorldState state = GreewsWorldState.get(serverWorld);
                state.setRace(player.getUuid(), payload.raceId());
                
                RaceHandler.applyRaceAttributes(player, payload.raceId());
                
                LOGGER.info("Player {} selected race: {}", player.getName().getString(), payload.raceId());
                
                ServerPlayNetworking.send(player, new RaceSyncPayload(payload.raceId()));
                
                syncRaceToNearbyPlayers(player, payload.raceId());
            }
        });
    });
    
    ServerPlayNetworking.registerGlobalReceiver(LanguageSelectionPayload.ID, (payload, context) -> {
        context.server().execute(() -> {
            ServerPlayerEntity player = context.player();
            World world = player.getEntityWorld();
            
            if (world instanceof ServerWorld serverWorld) {
                GreewsWorldState state = GreewsWorldState.get(serverWorld);
                state.setLanguage(player.getUuid(), payload.languageCode());
                
                LOGGER.info("Player {} selected language: {}", player.getName().getString(), payload.languageCode());
                
                syncLanguageToAllPlayers(player, payload.languageCode());
            }
        });
    });
    
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
        ServerPlayerEntity player = handler.player;
        World world = player.getEntityWorld();
        
        if (world instanceof ServerWorld serverWorld) {
            GreewsWorldState state = GreewsWorldState.get(serverWorld);
            String raceId = state.getRace(player.getUuid());
            String languageCode = state.getLanguage(player.getUuid());
            
            LOGGER.info("Player {} joined, race: {}, language: {}", player.getName().getString(), raceId, languageCode);
            
            if (raceId != null && !raceId.isEmpty()) {
                RaceHandler.applyRaceAttributes(player, raceId);
            }
            
            ServerPlayNetworking.send(player, new RaceSyncPayload(raceId != null ? raceId : ""));
            
            syncAllRacesToPlayer(player);
            syncAllLanguagesToPlayer(player);
            
            if (raceId != null && !raceId.isEmpty()) {
                syncRaceToAllPlayers(player, raceId);
            }
            
            if (languageCode != null && !languageCode.isEmpty()) {
                syncLanguageToAllPlayers(player, languageCode);
            }
        }
    });
    
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
        RaceCommand.register(dispatcher, registryAccess, environment);
    });
}

private void syncLanguageToAllPlayers(ServerPlayerEntity player, String languageCode) {
    ServerWorld world = (ServerWorld) player.getEntityWorld();
    PlayerLanguageSyncPayload payload = new PlayerLanguageSyncPayload(player.getUuid(), languageCode);
    
    for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
        ServerPlayNetworking.send(otherPlayer, payload);
    }
    
    LOGGER.info("Synced language {} of player {} to all players", languageCode, player.getName().getString());
}

private void syncAllLanguagesToPlayer(ServerPlayerEntity receiver) {
    ServerWorld world = (ServerWorld) receiver.getEntityWorld();
    GreewsWorldState state = GreewsWorldState.get(world);
    
    for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
        String languageCode = state.getLanguage(otherPlayer.getUuid());
        if (languageCode != null && !languageCode.isEmpty()) {
            PlayerLanguageSyncPayload payload = new PlayerLanguageSyncPayload(otherPlayer.getUuid(), languageCode);
            ServerPlayNetworking.send(receiver, payload);
        }
    }
    
    LOGGER.info("Synced all player languages to {}", receiver.getName().getString());
}
    
    private void syncRaceToAllPlayers(ServerPlayerEntity player, String raceId) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        PlayerRaceSyncPayload payload = new PlayerRaceSyncPayload(player.getUuid(), raceId);
        
        for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
            if (otherPlayer != player) {
                ServerPlayNetworking.send(otherPlayer, payload);
            }
        }
        
        LOGGER.info("Synced race {} of player {} to all players", raceId, player.getName().getString());
    }
    
    private void syncRaceToNearbyPlayers(ServerPlayerEntity player, String raceId) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        PlayerRaceSyncPayload payload = new PlayerRaceSyncPayload(player.getUuid(), raceId);
        
        for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
            if (otherPlayer != player) {
                double distance = otherPlayer.squaredDistanceTo(player);
                if (distance < 128 * 128) {
                    ServerPlayNetworking.send(otherPlayer, payload);
                }
            }
        }
        
        LOGGER.info("Synced race {} of player {} to nearby players", raceId, player.getName().getString());
    }
    
    private void syncAllRacesToPlayer(ServerPlayerEntity receiver) {
        ServerWorld world = (ServerWorld) receiver.getEntityWorld();
        GreewsWorldState state = GreewsWorldState.get(world);
        
        for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
            if (otherPlayer != receiver) {
                String raceId = state.getRace(otherPlayer.getUuid());
                if (raceId != null && !raceId.isEmpty()) {
                    PlayerRaceSyncPayload payload = new PlayerRaceSyncPayload(otherPlayer.getUuid(), raceId);
                    ServerPlayNetworking.send(receiver, payload);
                }
            }
        }
        
        LOGGER.info("Synced all player races to {}", receiver.getName().getString());
    }
}
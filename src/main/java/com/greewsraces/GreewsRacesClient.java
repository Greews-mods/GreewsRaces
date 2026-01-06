package com.greewsraces;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class GreewsRacesClient implements ClientModInitializer {
    private static String clientRaceId = "";
    private static boolean syncReceived = false;
    private static boolean shouldOpenScreen = false;
    private static int ticksToWait = 0;
    
    @Override
    public void onInitializeClient() {
        GreewsRaces.LOGGER.info("GreewsRaces client initialized!");
        
        // Nastavit výchozí jazyk
        ClientLanguageStorage.setLanguage(Language.CZECH);
        
        ClientPlayNetworking.registerGlobalReceiver(RaceSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                clientRaceId = payload.raceId();
                syncReceived = true;
                
                MinecraftClient client = context.client();
                if (client.player != null) {
                    ClientRaceStorage.setRace(client.player.getUuid(), payload.raceId());
                }
                
                GreewsRaces.LOGGER.info("Received own race sync: {}", clientRaceId);
                
                if (clientRaceId == null || clientRaceId.isEmpty()) {
                    shouldOpenScreen = true;
                    ticksToWait = 20;
                }
            });
        });
        
        ClientPlayNetworking.registerGlobalReceiver(PlayerRaceSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientRaceStorage.setRace(payload.playerId(), payload.raceId());
                
                GreewsRaces.LOGGER.info("Received race sync for player {}: {}", 
                    payload.playerId(), payload.raceId());
            });
        });
        
        ClientPlayNetworking.registerGlobalReceiver(PlayerLanguageSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientLanguageStorage.setPlayerLanguage(payload.playerId(), Language.fromCode(payload.languageCode()));
                
                GreewsRaces.LOGGER.info("Received language sync for player {}: {}", 
                    payload.playerId(), payload.languageCode());
            });
        });
        
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientRaceStorage.clear();
            ClientLanguageStorage.clear();
            resetSync();
            GreewsRaces.LOGGER.info("Cleared client storage on disconnect");
        });
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenScreen && client.player != null && client.world != null) {
                if (ticksToWait > 0) {
                    ticksToWait--;
                } else {
                    if (client.currentScreen == null) {
                        client.setScreen(new RaceSelectionScreen());
                        shouldOpenScreen = false;
                        GreewsRaces.LOGGER.info("Opening race selection screen");
                    }
                }
            }
            
            if (client.player == null && client.world == null) {
                syncReceived = false;
                shouldOpenScreen = false;
                clientRaceId = "";
            }
        });
    }
    
    public static String getClientRaceId() {
        return clientRaceId;
    }
    
    public static boolean hasRace() {
        return clientRaceId != null && !clientRaceId.isEmpty();
    }
    
    public static void resetSync() {
        syncReceived = false;
        clientRaceId = "";
    }
}
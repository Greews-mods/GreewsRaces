package com.greewsraces;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class GreewsRacesClient implements ClientModInitializer {
    private static String clientRaceId = "";
    private static boolean pendingPostLoginScreens;
    private static int postLoginDelayTicks;

    @Override
    public void onInitializeClient() {
        ClientConfig.load();
        GreewsRaces.LOGGER.info("GreewsRaces client initialized!");

        ClientPlayNetworking.registerGlobalReceiver(RacesConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientRaceConfig.applySync(payload.enabledRaceIds(), payload.generateEvernightBiome());
                GreewsRaces.LOGGER.info("Received race config sync: {} races, evernight={}",
                    payload.enabledRaceIds().size(), payload.generateEvernightBiome());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                MinecraftClient client = context.client();
                if (payload.screenKind() == OpenGuiPayload.KIND_LANGUAGE) {
                    client.setScreen(new LanguageSelectionScreen());
                } else {
                    client.setScreen(new RaceSelectionScreen());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RaceSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                clientRaceId = payload.raceId() != null ? payload.raceId() : "";

                MinecraftClient client = context.client();
                if (client.player != null) {
                    ClientRaceStorage.setRace(client.player.getUuid(), clientRaceId);
                }

                GreewsRaces.LOGGER.info("Received own race sync: {}", clientRaceId);

                if (clientRaceId == null || clientRaceId.isEmpty()) {
                    schedulePostLoginFlow();
                } else {
                    pendingPostLoginScreens = false;
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
                MinecraftClient c = context.client();
                if (c.player != null && c.player.getUuid().equals(payload.playerId())) {
                    ClientLanguageStorage.setServerLanguageCode(payload.languageCode());
                } else {
                    ClientLanguageStorage.setPlayerLanguage(payload.playerId(), Language.fromCode(payload.languageCode()));
                }
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
            if (client.player == null || client.world == null) {
                return;
            }

            if (pendingPostLoginScreens && client.currentScreen == null && ClientRaceConfig.hasSync()) {
                if (postLoginDelayTicks > 0) {
                    postLoginDelayTicks--;
                } else if (!hasRace()) {
                    if (!ClientLanguageStorage.hasServerLanguageChoice()) {
                        ClientLanguageStorage.applyInitialClientLocaleIfNeeded();
                        client.setScreen(new LanguageSelectionScreen());
                        pendingPostLoginScreens = false;
                    } else {
                        client.setScreen(new RaceSelectionScreen());
                        pendingPostLoginScreens = false;
                    }
                }
            }
        });
    }

    private static void schedulePostLoginFlow() {
        pendingPostLoginScreens = true;
        postLoginDelayTicks = 20;
    }

    public static String getClientRaceId() {
        return clientRaceId;
    }

    public static boolean hasRace() {
        return clientRaceId != null && !clientRaceId.isEmpty();
    }

    public static boolean needsRaceSelection() {
        return !hasRace();
    }

    public static void resetSync() {
        clientRaceId = "";
        pendingPostLoginScreens = false;
        postLoginDelayTicks = 0;
        ClientRaceConfig.reset();
    }
}

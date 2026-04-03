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

        ClientPlayNetworking.registerGlobalReceiver(RacesConfigSyncPayload.ID, (client, handler, buf, responseSender) -> {
            RacesConfigSyncPayload payload = RacesConfigSyncPayload.read(buf);
            client.execute(() -> {
                ClientRaceConfig.applySync(payload.enabledRaceIds(), payload.generateEvernightBiome());
                GreewsRaces.LOGGER.info("Received race config sync: {} races, evernight={}",
                    payload.enabledRaceIds().size(), payload.generateEvernightBiome());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPayload.ID, (client, handler, buf, responseSender) -> {
            OpenGuiPayload payload = OpenGuiPayload.read(buf);
            client.execute(() -> {
                MinecraftClient c = client;
                if (payload.screenKind() == OpenGuiPayload.KIND_LANGUAGE) {
                    c.setScreen(new LanguageSelectionScreen());
                } else {
                    c.setScreen(new RaceSelectionScreen());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RaceSyncPayload.ID, (client, handler, buf, responseSender) -> {
            RaceSyncPayload payload = RaceSyncPayload.read(buf);
            client.execute(() -> {
                clientRaceId = payload.raceId() != null ? payload.raceId() : "";

                MinecraftClient c = client;
                if (c.player != null) {
                    ClientRaceStorage.setRace(c.player.getUuid(), clientRaceId);
                }

                GreewsRaces.LOGGER.info("Received own race sync: {}", clientRaceId);

                if (clientRaceId == null || clientRaceId.isEmpty()) {
                    schedulePostLoginFlow();
                } else {
                    pendingPostLoginScreens = false;
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerRaceSyncPayload.ID, (client, handler, buf, responseSender) -> {
            PlayerRaceSyncPayload payload = PlayerRaceSyncPayload.read(buf);
            client.execute(() -> {
                ClientRaceStorage.setRace(payload.playerId(), payload.raceId());
                GreewsRaces.LOGGER.info("Received race sync for player {}: {}",
                    payload.playerId(), payload.raceId());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerLanguageSyncPayload.ID, (client, handler, buf, responseSender) -> {
            PlayerLanguageSyncPayload payload = PlayerLanguageSyncPayload.read(buf);
            client.execute(() -> {
                MinecraftClient c = client;
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
                pendingPostLoginScreens = false;
                clientRaceId = "";
                return;
            }

            if (pendingPostLoginScreens && client.currentScreen == null && ClientRaceConfig.hasSync()) {
                if (postLoginDelayTicks > 0) {
                    postLoginDelayTicks--;
                } else if (!hasRace()) {
                    if (!ClientLanguageStorage.hasServerLanguageChoice()) {
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

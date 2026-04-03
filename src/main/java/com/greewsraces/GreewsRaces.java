package com.greewsraces;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
        ServerConfig.load();
        LOGGER.info("GreewsRaces mod initialized!");

        SpawnGroupHandler.register();

        ServerPlayNetworking.registerGlobalReceiver(RaceSelectionPayload.ID, (server, player, handler, buf, responseSender) -> {
            RaceSelectionPayload payload = RaceSelectionPayload.read(buf);
            server.execute(() -> {
                World world = player.getEntityWorld();

                if (world instanceof ServerWorld serverWorld) {
                    if (!ServerConfig.isRaceEnabled(payload.raceId())) {
                        LOGGER.warn("Player {} tried disabled race {}", player.getName().getString(), payload.raceId());
                        return;
                    }
                    GreewsWorldState state = GreewsWorldState.get(serverWorld);
                    state.setRace(player.getUuid(), payload.raceId());

                    RaceHandler.applyRaceAttributes(player, payload.raceId());

                    LOGGER.info("Player {} selected race: {}", player.getName().getString(), payload.raceId());

                    RaceSyncPayload.sendTo(player, payload.raceId());

                    syncRaceToNearbyPlayers(player, payload.raceId());
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(LanguageSelectionPayload.ID, (server, player, handler, buf, responseSender) -> {
            LanguageSelectionPayload payload = LanguageSelectionPayload.read(buf);
            server.execute(() -> {
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

                if (raceId != null && !raceId.isEmpty() && !ServerConfig.isRaceEnabled(raceId)) {
                    LOGGER.warn("Migrating player {} from disabled race {} to human", player.getName().getString(), raceId);
                    raceId = Race.HUMAN.getId();
                    state.setRace(player.getUuid(), raceId);
                }

                LOGGER.info("Player {} joined, race: {}, language: {}", player.getName().getString(), raceId, languageCode);

                if (raceId != null && !raceId.isEmpty()) {
                    RaceHandler.applyRaceAttributes(player, raceId);
                }

                RacesConfigSyncPayload.sendTo(player, new RacesConfigSyncPayload(
                    ServerConfig.enabledRaceIds(),
                    ServerConfig.get().generateEvernightBiome
                ));
                RaceSyncPayload.sendTo(player, raceId != null ? raceId : "");
                PlayerLanguageSyncPayload.sendTo(player, new PlayerLanguageSyncPayload(
                    player.getUuid(), languageCode != null ? languageCode : ""));

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

        // Blindness pro ne-upíry v Evernight biomu
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 != 0) return; // každou sekundu

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                World world = player.getEntityWorld();
                if (!(world instanceof ServerWorld serverWorld)) continue;

                GreewsWorldState state = GreewsWorldState.get(serverWorld);
                String raceId = state.getRace(player.getUuid());

                if (!"vampire".equals(raceId)) {
                    if (BiomeRegistration.isInEvernightBiome(player)) {
                        // Přidej darkness s velmi dlouhou dobou pokud ho ještě nemá
                        if (!player.hasStatusEffect(StatusEffects.DARKNESS)) {
                            player.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.DARKNESS,
                                Integer.MAX_VALUE,
                                0,
                                false,
                                false
                            ));
                        }
                    } else {
                        // Okamžitě odstraň darkness při opuštění biomu
                        player.removeStatusEffect(StatusEffects.DARKNESS);
                    }
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
            PlayerLanguageSyncPayload.sendTo(otherPlayer, payload);
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
                PlayerLanguageSyncPayload.sendTo(receiver, payload);
            }
        }

        LOGGER.info("Synced all player languages to {}", receiver.getName().getString());
    }

    private void syncRaceToAllPlayers(ServerPlayerEntity player, String raceId) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        PlayerRaceSyncPayload payload = new PlayerRaceSyncPayload(player.getUuid(), raceId);

        for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
            if (otherPlayer != player) {
                PlayerRaceSyncPayload.sendTo(otherPlayer, payload);
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
                    PlayerRaceSyncPayload.sendTo(otherPlayer, payload);
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
                    PlayerRaceSyncPayload.sendTo(receiver, payload);
                }
            }
        }

        LOGGER.info("Synced all player races to {}", receiver.getName().getString());
    }
}

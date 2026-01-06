package com.greewsraces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GreewsWorldState {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String RACES_FILE = "greewsraces_data.json";
    private static final String LANGUAGES_FILE = "greewsraces_languages.json";
    private static final Map<ServerWorld, GreewsWorldState> INSTANCES = new HashMap<>();
    
    private final Map<String, String> playerRaces = new HashMap<>();
    private final Map<String, String> playerLanguages = new HashMap<>();
    private final Path racesPath;
    private final Path languagesPath;
    
    private GreewsWorldState(Path racesPath, Path languagesPath) {
        this.racesPath = racesPath;
        this.languagesPath = languagesPath;
        load();
    }
    
    public static GreewsWorldState get(ServerWorld world) {
        return INSTANCES.computeIfAbsent(world, w -> {
            Path worldFolder = w.getServer().getSavePath(WorldSavePath.ROOT);
            Path racesFile = worldFolder.resolve(RACES_FILE);
            Path languagesFile = worldFolder.resolve(LANGUAGES_FILE);
            return new GreewsWorldState(racesFile, languagesFile);
        });
    }
    
    public String getRace(UUID playerUuid) {
        return playerRaces.getOrDefault(playerUuid.toString(), "");
    }
    
    public void setRace(UUID playerUuid, String raceId) {
        playerRaces.put(playerUuid.toString(), raceId);
        saveRaces();
    }
    
    public boolean hasRace(UUID playerUuid) {
        String race = getRace(playerUuid);
        return race != null && !race.isEmpty();
    }
    
    public String getLanguage(UUID playerUuid) {
        return playerLanguages.getOrDefault(playerUuid.toString(), "cs");
    }
    
    public void setLanguage(UUID playerUuid, String languageCode) {
        playerLanguages.put(playerUuid.toString(), languageCode);
        saveLanguages();
    }
    
    private void load() {
        loadRaces();
        loadLanguages();
    }
    
    private void loadRaces() {
        if (Files.exists(racesPath)) {
            try (Reader reader = Files.newBufferedReader(racesPath)) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> data = GSON.fromJson(reader, type);
                if (data != null) {
                    playerRaces.putAll(data);
                }
                GreewsRaces.LOGGER.info("Loaded {} player races from {}", playerRaces.size(), racesPath);
            } catch (Exception e) {
                GreewsRaces.LOGGER.error("Failed to load race data", e);
            }
        }
    }
    
    private void loadLanguages() {
        if (Files.exists(languagesPath)) {
            try (Reader reader = Files.newBufferedReader(languagesPath)) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> data = GSON.fromJson(reader, type);
                if (data != null) {
                    playerLanguages.putAll(data);
                }
                GreewsRaces.LOGGER.info("Loaded {} player languages from {}", playerLanguages.size(), languagesPath);
            } catch (Exception e) {
                GreewsRaces.LOGGER.error("Failed to load language data", e);
            }
        }
    }
    
    private void saveRaces() {
        try {
            Files.createDirectories(racesPath.getParent());
            try (Writer writer = Files.newBufferedWriter(racesPath)) {
                GSON.toJson(playerRaces, writer);
            }
        } catch (Exception e) {
            GreewsRaces.LOGGER.error("Failed to save race data", e);
        }
    }
    
    private void saveLanguages() {
        try {
            Files.createDirectories(languagesPath.getParent());
            try (Writer writer = Files.newBufferedWriter(languagesPath)) {
                GSON.toJson(playerLanguages, writer);
            }
        } catch (Exception e) {
            GreewsRaces.LOGGER.error("Failed to save language data", e);
        }
    }
    
    public static void clearCache() {
        INSTANCES.clear();
    }
}
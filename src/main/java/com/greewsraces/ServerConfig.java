package com.greewsraces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dedicated server and integrated server (singleplayer) use the same file:
 * {@code config/greewsraces-server.json}. Use a separate Git branch per Minecraft version when APIs differ.
 */
public final class ServerConfig {

    private static Data INSTANCE;

    public static void load() {
        if (INSTANCE != null) {
            return;
        }
        Path path = FabricLoader.getInstance().getConfigDir().resolve("greewsraces-server.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Data data;
        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                data = gson.fromJson(reader, Data.class);
                if (data == null) {
                    data = new Data();
                }
            } catch (Exception e) {
                GreewsRaces.LOGGER.error("Failed to read greewsraces-server.json, using defaults", e);
                data = new Data();
            }
        } else {
            data = new Data();
        }
        data.ensureRaceKeys();
        if (!data.anyRaceEnabled()) {
            GreewsRaces.LOGGER.warn("No races enabled — forcing human on.");
            data.enabledRaces.put(Race.HUMAN.getId(), true);
        }
        INSTANCE = data;
        try {
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path)) {
                gson.toJson(INSTANCE, w);
            }
            GreewsRaces.LOGGER.info("Loaded server config from {}", path.toAbsolutePath());
        } catch (Exception e) {
            GreewsRaces.LOGGER.error("Failed to write greewsraces-server.json", e);
        }
    }

    public static Data get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static boolean isRaceEnabled(String raceId) {
        if (raceId == null || raceId.isEmpty()) {
            return false;
        }
        Data d = get();
        Boolean v = d.enabledRaces.get(raceId);
        return v != null && v;
    }

    public static java.util.List<String> enabledRaceIds() {
        return get().enabledRaces.entrySet().stream()
            .filter(e -> Boolean.TRUE.equals(e.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public static final class Data {
        public Map<String, Boolean> enabledRaces = new LinkedHashMap<>();
        public boolean generateEvernightBiome = true;

        void ensureRaceKeys() {
            if (enabledRaces == null) {
                enabledRaces = new LinkedHashMap<>();
            }
            for (Race r : Race.values()) {
                enabledRaces.putIfAbsent(r.getId(), true);
            }
        }

        boolean anyRaceEnabled() {
            return enabledRaces != null && enabledRaces.values().stream().anyMatch(Boolean::booleanValue);
        }
    }
}

package com.greewsraces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Client-only file: {@code config/greewsraces-client.json}. Race/biome rules still come from the server
 * (integrated server in singleplayer reads {@code greewsraces-server.json}).
 */
public final class ClientConfig {

    private static Data INSTANCE;

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("greewsraces-client.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Data data;
        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                data = gson.fromJson(reader, Data.class);
                if (data == null) {
                    data = new Data();
                }
            } catch (Exception e) {
                GreewsRaces.LOGGER.error("Failed to read greewsraces-client.json", e);
                data = new Data();
            }
        } else {
            data = new Data();
        }
        INSTANCE = data;
        try {
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path)) {
                gson.toJson(INSTANCE, w);
            }
        } catch (Exception e) {
            GreewsRaces.LOGGER.error("Failed to write greewsraces-client.json", e);
        }
    }

    public static Data get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static final class Data {
        public int version = 1;
    }
}

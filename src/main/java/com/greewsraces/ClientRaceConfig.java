package com.greewsraces;

import java.util.ArrayList;
import java.util.List;

public final class ClientRaceConfig {

    private static List<String> enabledRaceIds = new ArrayList<>();
    private static boolean syncReceived;
    private static boolean generateEvernightBiome = true;

    private ClientRaceConfig() {}

    public static void reset() {
        enabledRaceIds.clear();
        syncReceived = false;
        generateEvernightBiome = true;
    }

    public static void applySync(List<String> ids, boolean generateEvernight) {
        enabledRaceIds = ids != null ? new ArrayList<>(ids) : new ArrayList<>();
        generateEvernightBiome = generateEvernight;
        syncReceived = true;
    }

    public static boolean hasSync() {
        return syncReceived;
    }

    public static boolean isEvernightEnabledOnServer() {
        return generateEvernightBiome;
    }

    public static Race[] enabledRacesForUi() {
        if (!syncReceived) {
            return Race.values();
        }
        List<Race> list = new ArrayList<>();
        for (Race r : Race.values()) {
            if (enabledRaceIds.contains(r.getId())) {
                list.add(r);
            }
        }
        return list.toArray(new Race[0]);
    }
}

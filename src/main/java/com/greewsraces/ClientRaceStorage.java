package com.greewsraces;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Klientské úložiště pro rasy hráčů
 * Drží informace o rasách všech viditelných hráčů
 */
public class ClientRaceStorage {
    private static final Map<UUID, String> clientRaces = new HashMap<>();
    
    public static void setRace(UUID playerId, String raceId) {
        if (raceId == null || raceId.isEmpty()) {
            clientRaces.remove(playerId);
        } else {
            clientRaces.put(playerId, raceId);
        }
    }
    
    public static String getRace(UUID playerId) {
        return clientRaces.getOrDefault(playerId, "");
    }
    
    public static boolean hasRace(UUID playerId) {
        return clientRaces.containsKey(playerId) && !clientRaces.get(playerId).isEmpty();
    }
    
    public static void clear() {
        clientRaces.clear();
    }
    
    public static void removePlayer(UUID playerId) {
        clientRaces.remove(playerId);
    }
}
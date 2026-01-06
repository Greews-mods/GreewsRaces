package com.greewsraces;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientLanguageStorage {
    private static Language currentLanguage = Language.CZECH;
    private static final Map<UUID, Language> playerLanguages = new HashMap<>();
    
    public static void setLanguage(Language language) {
        currentLanguage = language;
    }
    
    public static Language getLanguage() {
        return currentLanguage;
    }
    
    public static void setPlayerLanguage(UUID playerId, Language language) {
        playerLanguages.put(playerId, language);
    }
    
    public static Language getPlayerLanguage(UUID playerId) {
        return playerLanguages.getOrDefault(playerId, Language.CZECH);
    }
    
    public static void clear() {
        playerLanguages.clear();
    }
}
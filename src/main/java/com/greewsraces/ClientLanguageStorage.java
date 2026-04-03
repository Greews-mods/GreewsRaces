package com.greewsraces;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientLanguageStorage {
    private static Language currentLanguage = Language.CZECH;
    /** True after the server has a non-empty language code for this player. */
    private static boolean serverLanguageKnown;
    private static final Map<UUID, Language> playerLanguages = new HashMap<>();

    public static void setLanguage(Language language) {
        currentLanguage = language;
    }

    public static Language getLanguage() {
        return currentLanguage;
    }

    /**
     * Server sync: empty means the player has not chosen a language yet (show language screen first).
     */
    public static void setServerLanguageCode(String code) {
        if (code == null || code.isEmpty()) {
            serverLanguageKnown = false;
            currentLanguage = Language.CZECH;
        } else {
            serverLanguageKnown = true;
            currentLanguage = Language.fromCode(code);
        }
    }

    public static boolean hasServerLanguageChoice() {
        return serverLanguageKnown;
    }

    /** After picking a language on the client before the server echoes sync. */
    public static void applyLocalLanguageChoice(Language language) {
        currentLanguage = language;
        serverLanguageKnown = true;
    }

    public static void setPlayerLanguage(UUID playerId, Language language) {
        playerLanguages.put(playerId, language);
    }

    public static Language getPlayerLanguage(UUID playerId) {
        return playerLanguages.getOrDefault(playerId, Language.CZECH);
    }

    public static void clear() {
        playerLanguages.clear();
        serverLanguageKnown = false;
        currentLanguage = Language.CZECH;
    }
}

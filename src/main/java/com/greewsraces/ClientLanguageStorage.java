package com.greewsraces;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ClientLanguageStorage {
    private static Language currentLanguage = Language.DEFAULT;
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
            applyInitialClientLocaleIfNeeded();
        } else {
            serverLanguageKnown = true;
            currentLanguage = Language.fromCode(code);
        }
    }

    public static boolean hasServerLanguageChoice() {
        return serverLanguageKnown;
    }

    /** Uses the Minecraft client language before the player picks a mod language. */
    public static void applyInitialClientLocaleIfNeeded() {
        if (!serverLanguageKnown) {
            currentLanguage = Language.fromMinecraftLocale(detectMinecraftLocale());
        }
    }

    private static String detectMinecraftLocale() {
        try {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client != null && client.options != null) {
                return client.options.language;
            }
        } catch (Exception ignored) {
        }
        return "";
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
        return playerLanguages.getOrDefault(playerId, Language.DEFAULT);
    }

    public static void clear() {
        playerLanguages.clear();
        serverLanguageKnown = false;
        currentLanguage = Language.DEFAULT;
    }
}

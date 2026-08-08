package com.greewsraces;

import java.util.Locale;

public enum Language {
    ENGLISH("en", "English", "EN"),
    CZECH("cs", "Čeština", "CZ"),
    FRENCH("fr", "Français", "FR"),
    PORTUGUESE("pt", "Português", "PT"),
    TURKISH("tr", "Türkçe", "TR"),
    GERMAN("de", "Deutsch", "DE"),
    SPANISH("es", "Español", "ES"),
    POLISH("pl", "Polski", "PL"),
    SLOVAK("sk", "Slovenčina", "SK"),
    UKRAINIAN("uk", "Українська", "UA"),
    RUSSIAN("ru", "Русский", "RU");

    public static final Language DEFAULT = ENGLISH;

    private final String code;
    private final String displayName;
    private final String shortCode;

    Language(String code, String displayName, String shortCode) {
        this.code = code;
        this.displayName = displayName;
        this.shortCode = shortCode;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public String getShortCode() { return shortCode; }

    public static Language fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return DEFAULT;
        }
        for (Language lang : values()) {
            if (lang.code.equals(code)) {
                return lang;
            }
        }
        return DEFAULT;
    }

    /** Maps Minecraft locale (e.g. en_us, fr_fr) to a supported mod language, else English. */
    public static Language fromMinecraftLocale(String locale) {
        if (locale == null || locale.isEmpty()) {
            return DEFAULT;
        }
        String base = locale.split("_", 2)[0].toLowerCase(Locale.ROOT);
        for (Language lang : values()) {
            if (lang.code.equals(base)) {
                return lang;
            }
        }
        return DEFAULT;
    }
}

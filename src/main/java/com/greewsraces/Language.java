package com.greewsraces;

public enum Language {
    CZECH("cs", "Čeština", "CZ"),
    ENGLISH("en", "English", "EN"),
    GERMAN("de", "Deutsch", "DE"),
    SPANISH("es", "Español", "ES"),
    POLISH("pl", "Polski", "PL"),
    SLOVAK("sk", "Slovenčina", "SK"),
    UKRAINIAN("uk", "Українська", "UA"),
    RUSSIAN("ru", "Русский", "RU");

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
            return CZECH;
        }
        for (Language lang : values()) {
            if (lang.code.equals(code)) {
                return lang;
            }
        }
        return CZECH;
    }
}

package com.greewsraces;

public enum Language {
    CZECH("cs", "Čeština", "CZ"),
    ENGLISH("en", "English", "EN"),
    SPANISH("es", "Español", "ES"),
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
        for (Language lang : values()) {
            if (lang.code.equals(code)) {
                return lang;
            }
        }
        return CZECH;
    }
}
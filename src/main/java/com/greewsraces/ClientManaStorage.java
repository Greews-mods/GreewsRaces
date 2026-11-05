package com.greewsraces;

public final class ClientManaStorage {
    private static int manaTenths = 100;

    private ClientManaStorage() {}

    public static int getManaTenths() {
        return manaTenths;
    }

    public static void setManaTenths(int value) {
        manaTenths = Math.max(0, Math.min(100, value));
    }

    public static void reset() {
        manaTenths = 100;
    }
}

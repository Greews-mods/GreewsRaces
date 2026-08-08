package com.greewsraces;

import net.minecraft.util.Identifier;

public class RaceIcon {
    public static final int SIZE = 128;

    private final Race race;
    private final Identifier texture;

    public RaceIcon(Race race) {
        this.race = race;
        this.texture = Identifier.of("greewsraces", "textures/gui/races/" + race.getId() + ".png");
    }

    public Race getRace() {
        return race;
    }

    public Identifier getTexture() {
        return texture;
    }

    public int getSize() {
        return SIZE;
    }
}

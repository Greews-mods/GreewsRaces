package com.greewsraces;

import net.minecraft.util.Identifier;

public class RaceIcon {
    public static final Identifier SPRITE_SHEET = Identifier.of("greewsraces", "textures/gui/race_icons.png");
    
    private final Race race;
    private final int x;
    private final int y;
    private final int size;
    
    public RaceIcon(Race race, int x, int y, int size) {
        this.race = race;
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    public Race getRace() { return race; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
}
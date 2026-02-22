package com.greewsraces;

import java.util.HashMap;
import java.util.Map;

public class AllRaceIcons {
    public static final Map<Race, RaceIcon> ALL = new HashMap<>();
    
    // Ikony v řádku: human, dwarf, night_elf, wood_elf, demon, vampire
    // Každá ikona 64x64, celková textura 384x64
    public static final RaceIcon HUMAN = register(Race.HUMAN, 0, 0, 64);
    public static final RaceIcon DWARF = register(Race.DWARF, 64, 0, 64);
    public static final RaceIcon NIGHT_ELF = register(Race.NIGHT_ELF, 132, 0, 64);
    public static final RaceIcon WOOD_ELF = register(Race.WOOD_ELF, 200, 0, 64);
    public static final RaceIcon DEMON = register(Race.DEMON, 270, 0, 64);
    public static final RaceIcon VAMPIRE = register(Race.VAMPIRE, 332, 0, 64);
    public static final RaceIcon GHOUL = register(Race.GHOUL, 392, 0, 64);
    public static final RaceIcon FAIRY = register(Race.FAIRY, 455, 0, 64);
    
    private static RaceIcon register(Race race, int x, int y, int size) {
        RaceIcon icon = new RaceIcon(race, x, y, size);
        ALL.put(race, icon);
        return icon;
    }
    
    public static RaceIcon byRace(Race race) {
        return ALL.getOrDefault(race, HUMAN);
    }
}
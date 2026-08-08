package com.greewsraces;

import java.util.HashMap;
import java.util.Map;

public class AllRaceIcons {
    public static final Map<Race, RaceIcon> ALL = new HashMap<>();

    public static final RaceIcon HUMAN = register(Race.HUMAN);
    public static final RaceIcon WOOD_ELF = register(Race.WOOD_ELF);
    public static final RaceIcon NIGHT_ELF = register(Race.NIGHT_ELF);
    public static final RaceIcon DWARF = register(Race.DWARF);
    public static final RaceIcon DEMON = register(Race.DEMON);
    public static final RaceIcon VAMPIRE = register(Race.VAMPIRE);
    public static final RaceIcon GHOUL = register(Race.GHOUL);
    public static final RaceIcon FAIRY = register(Race.FAIRY);

    private static RaceIcon register(Race race) {
        RaceIcon icon = new RaceIcon(race);
        ALL.put(race, icon);
        return icon;
    }

    public static RaceIcon byRace(Race race) {
        return ALL.getOrDefault(race, HUMAN);
    }
}

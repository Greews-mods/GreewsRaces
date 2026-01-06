package com.greewsraces;

import java.util.ArrayList;
import java.util.List;

public enum Race {

    HUMAN("Člověk",
        1.0,
        1.0,
        1.0,
        20.0,
        0.0,
        0x4A90E2,
        "Všestranný bojovník",
        "+20% poškození s mečem",
        "Žádné specifické nevýhody"
    ),

    DWARF("Trpaslík",
        0.8,
        0.9,
        1.2,
        20.0,
        1.0,
        0x8B4513,
        "Mistr sekery a podzemí",
        "+20% poškození se sekerou\n+20% rychlost těžení\n+1 brnění",
        "-20% poškození s mečem\n-20% poškození s lukem\n-10% rychlost pohybu"
    ),

    NIGHT_ELF("Noční Elf",
        1.2,
        1.0,
        1.0,
        20.0,
        0.0,
        0x4B0082,
        "Střelec stínů",
        "+20% poškození s kuší\n+20% rychlost pohybu v noci",
        "-20% poškození se sekerou\n-20% poškození s lukem\n+20% výška"
    ),

    WOOD_ELF("Lesní Elf",
        1.2,
        1.1,
        1.0,
        20.0,
        0.0,
        0x00FF00,
        "Mistr luku a lesa",
        "+20% poškození s lukem\n+10% rychlost pohybu",
        "-20% poškození se sekerou\n+20% výška"
    ),

    DEMON("Démon",
        1.0,
        1.0,
        1.0,
        20.0,
        0.0,
        0xFF0000,
        "Pán ohně a destrukce",
        "+20% poškození s mečem\nImunita vůči ohni a lávě",
        "-20% poškození s lukem\n-20% poškození s kuší\nPoškození z vody a deště"
    ),

    VAMPIRE("Upír",
        1.0,
        1.0,
        1.0,
        20.0,
        0.0,
        0x8B0000,
        "Nestárnoucí lovec krve",
        "+20% poškození se všemi zbraněmi\n30% šance na lifesteal",
        "Hoří na slunci"
    );

    private final String displayName;
    private final double heightScale;
    private final double walkSpeed;
    private final double miningSpeed;
    private final double maxHealth;
    private final double armor;
    private final int color;
    private final String description;
    private final String bonuses;
    private final String maluses;

    Race(String displayName,
         double heightScale,
         double walkSpeed,
         double miningSpeed,
         double maxHealth,
         double armor,
         int color,
         String description,
         String bonuses,
         String maluses) {

        this.displayName = displayName;
        this.heightScale = heightScale;
        this.walkSpeed = walkSpeed;
        this.miningSpeed = miningSpeed;
        this.maxHealth = maxHealth;
        this.armor = armor;
        this.color = color;
        this.description = description;
        this.bonuses = bonuses;
        this.maluses = maluses;
    }

    public String getDisplayName() { return displayName; }
    public double getHeightScale() { return heightScale; }
    public double getWalkSpeed() { return walkSpeed; }
    public double getMiningSpeed() { return miningSpeed; }
    public double getMaxHealth() { return maxHealth; }
    public double getArmor() { return armor; }
    public int getColor() { return color; }
    public String getDescription() { return description; }
    public String getBonuses() { return bonuses; }
    public String getMaluses() { return maluses; }

    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add("§7" + description);
        tooltip.add("");
        tooltip.add("§a✓ Bonusy:");
        for (String line : bonuses.split("\n")) {
            tooltip.add("§a  " + line);
        }
        tooltip.add("");
        tooltip.add("§c✗ Nevýhody:");
        for (String line : maluses.split("\n")) {
            tooltip.add("§c  " + line);
        }
        return tooltip;
    }

    public String getId() {
        return name().toLowerCase();
    }

    public static Race fromId(String id) {
        for (Race race : values()) {
            if (race.getId().equals(id)) {
                return race;
            }
        }
        return HUMAN;
    }
}

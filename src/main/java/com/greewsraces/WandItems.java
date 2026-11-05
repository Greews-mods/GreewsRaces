package com.greewsraces;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public final class WandItems {
    public static final WandOrbItem ORB_WOOD = orb("orb_wood", 4.0f);
    public static final WandOrbItem ORB_COPPER = orb("orb_copper", 5.0f);
    public static final WandOrbItem ORB_IRON = orb("orb_iron", 6.0f);
    public static final WandOrbItem ORB_GOLD = orb("orb_gold", 7.0f);
    public static final WandOrbItem ORB_DIAMOND = orb("orb_diamond", 8.0f);
    public static final WandOrbItem ORB_NETHERITE = orb("orb_netherite", 10.0f);

    public static final WandItem WOOD_WAND = wand("wood_wand", ORB_WOOD);
    public static final WandItem COPPER_WAND = wand("copper_wand", ORB_COPPER);
    public static final WandItem IRON_WAND = wand("iron_wand", ORB_IRON);
    public static final WandItem GOLD_WAND = wand("gold_wand", ORB_GOLD);
    public static final WandItem DIAMOND_WAND = wand("diamond_wand", ORB_DIAMOND);
    public static final WandItem NETHERITE_WAND = wand("netherite_wand", ORB_NETHERITE);

    private WandItems() {}

    private static WandOrbItem orb(String id, float damage) {
        return Registry.register(Registries.ITEM, Identifier.of(GreewsRaces.MOD_ID, id), new WandOrbItem(damage));
    }

    private static WandItem wand(String id, WandOrbItem orb) {
        return Registry.register(Registries.ITEM, Identifier.of(GreewsRaces.MOD_ID, id), new WandItem(orb));
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(WOOD_WAND);
            entries.add(COPPER_WAND);
            entries.add(IRON_WAND);
            entries.add(GOLD_WAND);
            entries.add(DIAMOND_WAND);
            entries.add(NETHERITE_WAND);
        });
    }

    public static List<WandItem> allWands() {
        return List.of(WOOD_WAND, COPPER_WAND, IRON_WAND, GOLD_WAND, DIAMOND_WAND, NETHERITE_WAND);
    }
}

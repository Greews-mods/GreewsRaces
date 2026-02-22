package com.greewsraces;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class RaceHandler {

    private static final Identifier RACE_MODIFIER_ID = Identifier.of("greewsraces", "race_modifier");
    private static final Identifier RACE_REACH_ID    = Identifier.of("greewsraces", "race_reach_modifier");
    private static final Identifier RACE_SCALE_ID    = Identifier.of("greewsraces", "race_scale_modifier");

    public static void applyRaceAttributes(PlayerEntity player, String raceId) {
        Race race = Race.fromId(raceId);

        resetAttributes(player);

        // === MAX HEALTH ===
        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttr != null && race.getMaxHealth() != 20.0) {
            healthAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_MODIFIER_ID,
                race.getMaxHealth() - 20.0,
                EntityAttributeModifier.Operation.ADD_VALUE
            ));
        }

        // === POHYB ===
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null && race.getWalkSpeed() != 1.0) {
            speedAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_MODIFIER_ID,
                race.getWalkSpeed() - 1.0,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }

        // === BRNĚNÍ ===
        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.ARMOR);
        if (armorAttr != null && race.getArmor() != 0) {
            armorAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_MODIFIER_ID,
                race.getArmor(),
                EntityAttributeModifier.Operation.ADD_VALUE
            ));
        }

        // === SCALE (výška/velikost) ===
        // heightScale: 1.0 = normální, 0.8 = trpaslík (-20%), 0.6 = víla (-40%), 1.2 = elfové (+20%)
        // ADD_MULTIPLIED_BASE: přidáme (scale - 1.0) jako násobek základu
        EntityAttributeInstance scaleAttr = player.getAttributeInstance(EntityAttributes.SCALE);
        if (scaleAttr != null && race.getHeightScale() != 1.0) {
            scaleAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_SCALE_ID,
                race.getHeightScale() - 1.0,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }

        // === DOSAH – Trpaslík a Víla dostávají +20% dosah jako kompenzaci ===
        if (race == Race.DWARF || race == Race.FAIRY) {
            applyReachBonus(player, 0.20);
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void applyReachBonus(PlayerEntity player, double multiplierBonus) {
        EntityAttributeInstance blockReach = player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        if (blockReach != null) {
            blockReach.addTemporaryModifier(new EntityAttributeModifier(
                RACE_REACH_ID,
                multiplierBonus,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }

        EntityAttributeInstance entityReach = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (entityReach != null) {
            entityReach.addTemporaryModifier(new EntityAttributeModifier(
                RACE_REACH_ID,
                multiplierBonus,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }
    }

    public static void resetAttributes(PlayerEntity player) {
        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttr != null) healthAttr.removeModifier(RACE_MODIFIER_ID);

        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(RACE_MODIFIER_ID);

        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.ARMOR);
        if (armorAttr != null) armorAttr.removeModifier(RACE_MODIFIER_ID);

        // Reset scale
        EntityAttributeInstance scaleAttr = player.getAttributeInstance(EntityAttributes.SCALE);
        if (scaleAttr != null) scaleAttr.removeModifier(RACE_SCALE_ID);

        // Reset dosahu
        EntityAttributeInstance blockReach = player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        if (blockReach != null) blockReach.removeModifier(RACE_REACH_ID);

        EntityAttributeInstance entityReach = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (entityReach != null) entityReach.removeModifier(RACE_REACH_ID);

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
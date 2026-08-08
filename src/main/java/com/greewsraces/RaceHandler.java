package com.greewsraces;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

/**
 * MC 1.20.1: scale via {@link com.greewsraces.mixin.EntityScaleMixin}; reach not available on this version.
 */
public class RaceHandler {

    private static final UUID HEALTH_UUID = UUID.fromString("6b1e4c2a-9d3f-4e1b-8c7a-2f5e9d0b1a3c");
    private static final UUID SPEED_UUID = UUID.fromString("7c2f5d3b-0e40-5f2c-9d8b-3a6f0e1c2b4d");
    private static final UUID ARMOR_UUID = UUID.fromString("8d3a6e4c-1f50-6a3d-0e9c-4b7a1f2d3c5e");

    public static void applyRaceAttributes(PlayerEntity player, String raceId) {
        Race race = Race.fromId(raceId);

        resetAttributes(player);

        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null && race.getMaxHealth() != 20.0) {
            healthAttr.addTemporaryModifier(new EntityAttributeModifier(
                HEALTH_UUID,
                "greewsraces_health",
                race.getMaxHealth() - 20.0,
                EntityAttributeModifier.Operation.ADDITION
            ));
        }

        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null && race.getWalkSpeed() != 1.0) {
            speedAttr.addTemporaryModifier(new EntityAttributeModifier(
                SPEED_UUID,
                "greewsraces_speed",
                race.getWalkSpeed() - 1.0,
                EntityAttributeModifier.Operation.MULTIPLY_BASE
            ));
        }

        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null && race.getArmor() != 0) {
            armorAttr.addTemporaryModifier(new EntityAttributeModifier(
                ARMOR_UUID,
                "greewsraces_armor",
                race.getArmor(),
                EntityAttributeModifier.Operation.ADDITION
            ));
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        player.calculateDimensions();
    }

    public static void resetAttributes(PlayerEntity player) {
        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(HEALTH_UUID);
        }

        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_UUID);
        }

        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(ARMOR_UUID);
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        player.calculateDimensions();
    }
}

package com.greewsraces;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

/**
 * MC 1.20.1: modifikátory podle UUID; reach bonus vynechán (atributy dosahu až v novějších verzích).
 */
public class RaceHandler {

    private static final UUID RACE_MODIFIER_UUID = UUID.fromString("6b1e4c2a-9d3f-4e1b-8c7a-2f5e9d0b1a3c");

    public static void applyRaceAttributes(PlayerEntity player, String raceId) {
        Race race = Race.fromId(raceId);

        resetAttributes(player);

        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null && race.getMaxHealth() != 20.0) {
            healthAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_MODIFIER_UUID,
                "greewsraces_race",
                race.getMaxHealth() - 20.0,
                EntityAttributeModifier.Operation.ADDITION
            ));
        }

        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null && race.getWalkSpeed() != 1.0) {
            speedAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_MODIFIER_UUID,
                "greewsraces_race",
                race.getWalkSpeed() - 1.0,
                EntityAttributeModifier.Operation.MULTIPLY_BASE
            ));
        }

        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null && race.getArmor() != 0) {
            armorAttr.addTemporaryModifier(new EntityAttributeModifier(
                RACE_MODIFIER_UUID,
                "greewsraces_race",
                race.getArmor(),
                EntityAttributeModifier.Operation.ADDITION
            ));
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    public static void resetAttributes(PlayerEntity player) {
        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null) healthAttr.removeModifier(RACE_MODIFIER_UUID);

        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(RACE_MODIFIER_UUID);

        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null) armorAttr.removeModifier(RACE_MODIFIER_UUID);

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}

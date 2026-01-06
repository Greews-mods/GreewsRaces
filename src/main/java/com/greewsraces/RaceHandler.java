package com.greewsraces;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class RaceHandler {
    
    private static final Identifier RACE_MODIFIER_ID = Identifier.of("greewsraces", "race_modifier");
    
    public static void applyRaceAttributes(PlayerEntity player, String raceId) {
        Race race = Race.fromId(raceId);
        
        resetAttributes(player);
        
        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttr != null && race.getMaxHealth() != 20.0) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                RACE_MODIFIER_ID,
                race.getMaxHealth() - 20.0,
                EntityAttributeModifier.Operation.ADD_VALUE
            );
            healthAttr.addTemporaryModifier(modifier);
        }
        
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null && race.getWalkSpeed() != 1.0) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                RACE_MODIFIER_ID,
                race.getWalkSpeed() - 1.0,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
            speedAttr.addTemporaryModifier(modifier);
        }
        
        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.ARMOR);
        if (armorAttr != null && race.getArmor() != 0) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                RACE_MODIFIER_ID,
                race.getArmor(),
                EntityAttributeModifier.Operation.ADD_VALUE
            );
            armorAttr.addTemporaryModifier(modifier);
        }
        
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
    
    public static void resetAttributes(PlayerEntity player) {
        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(RACE_MODIFIER_ID);
        }
        
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(RACE_MODIFIER_ID);
        }
        
        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(RACE_MODIFIER_ID);
        }
        
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
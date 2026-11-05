package com.greewsraces;

import net.minecraft.item.Item;

public class WandOrbItem extends Item {
    private final float damage;

    public WandOrbItem(float damage, Item.Settings settings) {
        super(settings.maxCount(1));
        this.damage = damage;
    }

    public float getMagicDamage() {
        return damage;
    }
}

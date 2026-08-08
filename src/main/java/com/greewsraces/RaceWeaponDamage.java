package com.greewsraces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;

/**
 * Race-based weapon damage multipliers (melee and projectile).
 */
public final class RaceWeaponDamage {

    private static final float BONUS = 1.20f;
    private static final float PENALTY = 0.80f;

    public enum WeaponCategory {
        NONE, SWORD, AXE, BOW, CROSSBOW, PICKAXE, TRIDENT, OTHER
    }

    private RaceWeaponDamage() {
    }

    public static WeaponCategory category(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return WeaponCategory.NONE;
        }
        Item item = stack.getItem();
        if (item instanceof SwordItem) {
            return WeaponCategory.SWORD;
        }
        if (item instanceof AxeItem) {
            return WeaponCategory.AXE;
        }
        if (item instanceof BowItem) {
            return WeaponCategory.BOW;
        }
        if (item instanceof CrossbowItem) {
            return WeaponCategory.CROSSBOW;
        }
        if (item instanceof PickaxeItem) {
            return WeaponCategory.PICKAXE;
        }
        if (item instanceof TridentItem) {
            return WeaponCategory.TRIDENT;
        }
        return WeaponCategory.OTHER;
    }

    public static float multiplyMelee(PlayerEntity player, float damage) {
        if (!PlayerDataManager.hasRaceSelected(player) || damage <= 0.0f) {
            return damage;
        }
        Race race = Race.fromId(PlayerDataManager.getRace(player));
        return damage * meleeMultiplier(race, category(player.getMainHandStack()));
    }

    public static double multiplyProjectile(PlayerEntity player, double damage) {
        if (!PlayerDataManager.hasRaceSelected(player) || damage <= 0.0) {
            return damage;
        }
        Race race = Race.fromId(PlayerDataManager.getRace(player));
        WeaponCategory cat = WeaponCategory.BOW;
        if (player.getMainHandStack().getItem() instanceof CrossbowItem
            || player.getOffHandStack().getItem() instanceof CrossbowItem) {
            cat = WeaponCategory.CROSSBOW;
        }
        return damage * meleeMultiplier(race, cat);
    }

    private static float meleeMultiplier(Race race, WeaponCategory cat) {
        if (cat == WeaponCategory.NONE) {
            return 1.0f;
        }

        return switch (race) {
            case HUMAN -> cat == WeaponCategory.SWORD ? BONUS : 1.0f;
            case DWARF -> switch (cat) {
                case AXE -> BONUS;
                case SWORD, BOW -> PENALTY;
                default -> 1.0f;
            };
            case NIGHT_ELF -> switch (cat) {
                case CROSSBOW -> BONUS;
                case AXE, BOW -> PENALTY;
                default -> 1.0f;
            };
            case WOOD_ELF -> switch (cat) {
                case BOW -> BONUS;
                case AXE -> PENALTY;
                default -> 1.0f;
            };
            case DEMON -> switch (cat) {
                case SWORD -> BONUS;
                case BOW, CROSSBOW -> PENALTY;
                default -> 1.0f;
            };
            case VAMPIRE -> isWeapon(cat) ? BONUS : 1.0f;
            case FAIRY -> switch (cat) {
                case AXE, PICKAXE -> PENALTY;
                default -> 1.0f;
            };
            default -> 1.0f;
        };
    }

    private static boolean isWeapon(WeaponCategory cat) {
        return cat != WeaponCategory.NONE;
    }
}

package com.greewsraces.mixin;

import com.greewsraces.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void addRacePrefix(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));
            Text originalName = cir.getReturnValue();

            Language viewerLanguage = Language.DEFAULT;
            if (player.getEntityWorld().isClient()) {
                viewerLanguage = ClientLanguageStorage.getLanguage();
            }

            String raceName = Translation.get("race." + race.getId(), viewerLanguage);

            MutableText prefix = Text.literal("[" + raceName + "] ")
                .styled(style -> style.withColor(race.getColor()));

            MutableText whiteName = Text.empty()
                .append(originalName)
                .styled(style -> style.withColor(0xFFFFFF));

            MutableText newName = prefix.append(whiteName);
            cir.setReturnValue(newName);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));

            // Démon – imunita vůči ohni
            if (race == Race.DEMON) {
                if (source.isIn(DamageTypeTags.IS_FIRE)) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            // Ghůl – imunita vůči utopení
            if (race == Race.GHOUL) {
                if (source.isIn(DamageTypeTags.IS_DROWNING)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @ModifyArg(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
        ),
        index = 1
    )
    private float greewsraces$scaleMeleeDamage(float amount) {
        return RaceWeaponDamage.multiplyMelee((PlayerEntity) (Object) this, amount);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));

            // Upír – lifesteal
            if (race == Race.VAMPIRE && target instanceof LivingEntity) {
                if (player.getRandom().nextFloat() < 0.30f) {
                    float currentHealth = player.getHealth();
                    float maxHealth = player.getMaxHealth();
                    if (currentHealth < maxHealth) {
                        player.setHealth(Math.min(currentHealth + 1.0f, maxHealth));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getEntityWorld();

        if (!world.isClient() && PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));
            // === DÉMON ===
            if (race == Race.DEMON) {
                if (player.isOnFire()) {
                    player.extinguish();
                }
                if (player.isSubmergedInWater() || player.isTouchingWaterOrRain()) {
                    if (player.age % 20 == 0) {
                        player.damage(world.getDamageSources().drown(), 1.0f);
                    }
                }
            }

            // === UPÍR ===
            if (race == Race.VAMPIRE) {
                // V Evernight biomu upír nehoří – je to jejich domov
                boolean inEvernight = BiomeRegistration.isInEvernightBiome(player);
                if (!inEvernight && world.isDay() && !player.isSubmergedInWater()) {
                    if (world.isSkyVisible(player.getBlockPos())) {
                        if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE)) {
                            player.setOnFireFor(2);
                        }
                    }
                }
            }

            // === GHŮL – dýchání pod vodou ===
            if (race == Race.GHOUL) {
                if (player.isSubmergedInWater()) {
                    player.setAir(player.getMaxAir());
                }
            }

            // === NOČNÍ ELF – rychlost v noci ===
            if (race == Race.NIGHT_ELF) {
                var speedAttr = player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (speedAttr != null) {
                    double baseSpeed = 0.10000000149011612;
                    double currentBase = speedAttr.getBaseValue();

                    if (!world.isDay()) {
                        if (currentBase < baseSpeed * 1.15) {
                            speedAttr.setBaseValue(baseSpeed * 1.20);
                        }
                    } else {
                        if (currentBase > baseSpeed * 1.05) {
                            speedAttr.setBaseValue(baseSpeed);
                        }
                    }
                }
            }

            // === VÍLA – bonus brnění z kožené zbroje ===
            // Přepočítáváme každých 20 ticků (1 sekunda) aby nedocházelo k problémům
            if (race == Race.FAIRY && player.age % 20 == 0) {
                applyFairyLeatherArmorBonus(player);
            }
        }
    }

    /**
     * Víla: +20% brnění za každý kus kožené zbroje.
     * MC 1.21.10: getEquippedStack(EquipmentSlot) místo getArmorItems()
     */
    private void applyFairyLeatherArmorBonus(PlayerEntity player) {
        EquipmentSlot[] armorSlots = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        };

        int leatherPieces = 0;
        for (EquipmentSlot slot : armorSlots) {
            ItemStack piece = player.getEquippedStack(slot);
            if (!piece.isEmpty()) {
                String itemId = net.minecraft.registry.Registries.ITEM
                    .getId(piece.getItem()).toString();
                if (itemId.contains("leather")) {
                    leatherPieces++;
                }
            }
        }

        var armorAttr = player.getAttributeInstance(
            net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null) {
            java.util.UUID leatherUuid = java.util.UUID.fromString("7c2f5a1e-4b9d-4c3e-a1f2-9e8d7c6b5a40");
            armorAttr.removeModifier(leatherUuid);

            if (leatherPieces > 0) {
                double bonus = leatherPieces * 1.0;
                net.minecraft.entity.attribute.EntityAttributeModifier leatherMod =
                    new net.minecraft.entity.attribute.EntityAttributeModifier(
                        leatherUuid,
                        "greewsraces_fairy_leather",
                        bonus,
                        net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION
                    );
                armorAttr.addTemporaryModifier(leatherMod);
            }
        }
    }
}
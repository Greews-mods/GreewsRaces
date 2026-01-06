package com.greewsraces.mixin;

import com.greewsraces.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
            
            Language viewerLanguage = Language.CZECH;
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
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));
            
            if (race == Race.DEMON) {
                if (source.isIn(DamageTypeTags.IS_FIRE)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
    
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (PlayerDataManager.hasRaceSelected(player)) {
            Race race = Race.fromId(PlayerDataManager.getRace(player));
            
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
            ServerWorld serverWorld = (ServerWorld) world;
            
            if (race == Race.DEMON) {
                if (player.isOnFire()) {
                    player.extinguish();
                }
                
                if (player.isSubmergedInWater() || player.isTouchingWaterOrRain()) {
                    if (player.age % 20 == 0) {
                        player.damage(serverWorld, world.getDamageSources().drown(), 1.0f);
                    }
                }
            }
            
            if (race == Race.VAMPIRE) {
                if (world.isDay() && !player.isSubmergedInWater()) {
                    if (world.isSkyVisible(player.getBlockPos())) {
                        if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE)) {
                            player.setOnFireFor(2);
                        }
                    }
                }
            }
            
            if (race == Race.NIGHT_ELF) {
                var speedAttr = player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.MOVEMENT_SPEED);
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
        }
    }
}
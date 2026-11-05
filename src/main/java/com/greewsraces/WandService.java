package com.greewsraces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WandService {
    private static final long CAST_COOLDOWN_TICKS = 10;
    private static final Map<UUID, Long> LAST_CAST_TICK = new HashMap<>();

    private WandService() {}

    public static void fire(ServerPlayerEntity player, WandOrbItem orb) {
        if (!ManaHandler.isFairy(player)) {
            player.sendMessage(Text.translatable("message.greewsraces.wand.fairy_only"), true);
            return;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        long now = world.getTime();
        long previous = LAST_CAST_TICK.getOrDefault(player.getUuid(), Long.MIN_VALUE / 2);
        if (now - previous < CAST_COOLDOWN_TICKS) return;
        if (!ManaHandler.consume(player, ManaHandler.CAST_COST_TENTHS)) {
            player.sendMessage(Text.translatable("message.greewsraces.mana.empty"), true);
            return;
        }

        LAST_CAST_TICK.put(player.getUuid(), now);
        SnowballEntity projectile = new SnowballEntity(world, player);
        projectile.setItem(new ItemStack(orb));
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 1.6f, 0.0f);
        if (!world.spawnEntity(projectile)) {
            for (int i = 0; i < ManaHandler.CAST_COST_TENTHS; i++) ManaHandler.regenerateOneTenth(player);
            LAST_CAST_TICK.remove(player.getUuid());
            return;
        }

        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d start = player.getCameraPosVec(1.0f);
        for (int i = 1; i <= 4; i++) {
            Vec3d particlePos = start.add(direction.multiply(i * 0.35));
            world.spawnParticles(ParticleTypes.END_ROD, particlePos.x, particlePos.y, particlePos.z,
                1, 0.015, 0.015, 0.015, 0.0);
        }
    }

    public static void hit(SnowballEntity projectile, LivingEntity target) {
        if (!(projectile.getOwner() instanceof ServerPlayerEntity player)
            || !(projectile.getStack().getItem() instanceof WandOrbItem orb)
            || !ManaHandler.isFairy(player)) {
            return;
        }

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        target.damage(world.getDamageSources().playerAttack(player), orb.getMagicDamage());
    }
}

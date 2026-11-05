package com.greewsraces;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class ManaHandler {
    public static final int MAX_MANA_TENTHS = 100;
    public static final int CAST_COST_TENTHS = 10;

    private ManaHandler() {}

    public static boolean isFairy(ServerPlayerEntity player) {
        World world = player.getEntityWorld();
        return world instanceof ServerWorld serverWorld
            && Race.FAIRY.getId().equals(GreewsWorldState.get(serverWorld).getRace(player.getUuid()));
    }

    public static int getManaTenths(ServerPlayerEntity player) {
        World world = player.getEntityWorld();
        return world instanceof ServerWorld serverWorld
            ? GreewsWorldState.get(serverWorld).getManaTenths(player.getUuid())
            : MAX_MANA_TENTHS;
    }

    public static boolean consume(ServerPlayerEntity player, int amountTenths) {
        if (!isFairy(player)) return false;
        int current = getManaTenths(player);
        if (current < amountTenths) return false;
        setMana(player, current - amountTenths);
        return true;
    }

    public static void regenerateOneTenth(ServerPlayerEntity player) {
        int current = getManaTenths(player);
        if (current < MAX_MANA_TENTHS) setMana(player, current + 1);
    }

    public static void sync(ServerPlayerEntity player) {
        ManaSyncPayload.sendTo(player, getManaTenths(player));
    }

    private static void setMana(ServerPlayerEntity player, int value) {
        World world = player.getEntityWorld();
        if (world instanceof ServerWorld serverWorld) {
            GreewsWorldState.get(serverWorld).setManaTenths(player.getUuid(), value);
            sync(player);
        }
    }
}

package com.greewsraces;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record ManaSyncPayload(int manaTenths) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "mana_sync");

    public static void sendTo(ServerPlayerEntity player, int manaTenths) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(manaTenths);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static ManaSyncPayload read(PacketByteBuf buf) {
        return new ManaSyncPayload(buf.readVarInt());
    }
}

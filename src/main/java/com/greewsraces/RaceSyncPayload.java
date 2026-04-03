package com.greewsraces;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record RaceSyncPayload(String raceId) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "race_sync");

    public static void sendTo(ServerPlayerEntity player, String raceId) {
        PacketByteBuf buf = PacketByteBufs.create();
        new RaceSyncPayload(raceId).write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static RaceSyncPayload read(PacketByteBuf buf) {
        return new RaceSyncPayload(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(raceId != null ? raceId : "");
    }
}

package com.greewsraces;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * Payload pro synchronizaci rasy jiného hráče
 */
public record PlayerRaceSyncPayload(UUID playerId, String raceId) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "player_race_sync");

    public static void sendTo(ServerPlayerEntity player, PlayerRaceSyncPayload payload) {
        PacketByteBuf buf = PacketByteBufs.create();
        payload.write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static PlayerRaceSyncPayload read(PacketByteBuf buf) {
        return new PlayerRaceSyncPayload(buf.readUuid(), buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(playerId);
        buf.writeString(raceId != null ? raceId : "");
    }
}

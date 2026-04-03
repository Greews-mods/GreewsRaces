package com.greewsraces;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record PlayerLanguageSyncPayload(UUID playerId, String languageCode) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "player_language_sync");

    public static void sendTo(ServerPlayerEntity player, PlayerLanguageSyncPayload payload) {
        PacketByteBuf buf = PacketByteBufs.create();
        payload.write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static PlayerLanguageSyncPayload read(PacketByteBuf buf) {
        return new PlayerLanguageSyncPayload(buf.readUuid(), buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(playerId);
        buf.writeString(languageCode != null ? languageCode : "");
    }
}

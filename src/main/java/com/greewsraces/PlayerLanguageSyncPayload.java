package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record PlayerLanguageSyncPayload(UUID playerId, String languageCode) implements CustomPayload {
    public static final CustomPayload.Id<PlayerLanguageSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "player_language_sync"));
    
    public static final PacketCodec<RegistryByteBuf, PlayerLanguageSyncPayload> CODEC = 
        PacketCodec.tuple(
            Uuids.PACKET_CODEC, PlayerLanguageSyncPayload::playerId,
            PacketCodecs.STRING, PlayerLanguageSyncPayload::languageCode,
            PlayerLanguageSyncPayload::new
        );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
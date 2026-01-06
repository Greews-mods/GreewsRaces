package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * Payload pro synchronizaci rasy jiného hráče
 * Používá se pro zobrazení ras ostatních hráčů na klientovi
 */
public record PlayerRaceSyncPayload(UUID playerId, String raceId) implements CustomPayload {
    public static final CustomPayload.Id<PlayerRaceSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "player_race_sync"));
    
    public static final PacketCodec<RegistryByteBuf, PlayerRaceSyncPayload> CODEC = 
        PacketCodec.tuple(
            Uuids.PACKET_CODEC, PlayerRaceSyncPayload::playerId,
            PacketCodecs.STRING, PlayerRaceSyncPayload::raceId,
            PlayerRaceSyncPayload::new
        );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RaceSyncPayload(String raceId) implements CustomPayload {
    public static final CustomPayload.Id<RaceSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "race_sync"));
    
    public static final PacketCodec<RegistryByteBuf, RaceSyncPayload> CODEC = 
        PacketCodec.tuple(
            PacketCodecs.STRING, RaceSyncPayload::raceId,
            RaceSyncPayload::new
        );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
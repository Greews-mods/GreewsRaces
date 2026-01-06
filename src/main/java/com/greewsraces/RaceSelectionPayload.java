package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RaceSelectionPayload(String raceId) implements CustomPayload {
    public static final CustomPayload.Id<RaceSelectionPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "race_selection"));
    
    public static final PacketCodec<RegistryByteBuf, RaceSelectionPayload> CODEC = 
        PacketCodec.tuple(
            PacketCodecs.STRING, RaceSelectionPayload::raceId,
            RaceSelectionPayload::new
        );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
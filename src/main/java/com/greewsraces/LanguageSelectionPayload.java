package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LanguageSelectionPayload(String languageCode) implements CustomPayload {
    public static final CustomPayload.Id<LanguageSelectionPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "language_selection"));
    
    public static final PacketCodec<RegistryByteBuf, LanguageSelectionPayload> CODEC = 
        PacketCodec.tuple(
            PacketCodecs.STRING, LanguageSelectionPayload::languageCode,
            LanguageSelectionPayload::new
        );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ManaSyncPayload(int manaTenths) implements CustomPayload {
    public static final Id<ManaSyncPayload> ID = new Id<>(Identifier.of(GreewsRaces.MOD_ID, "mana_sync"));
    public static final PacketCodec<RegistryByteBuf, ManaSyncPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.VAR_INT, ManaSyncPayload::manaTenths,
        ManaSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

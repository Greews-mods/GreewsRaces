package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenGuiPayload(byte screenKind) implements CustomPayload {

    public static final byte KIND_LANGUAGE = 0;
    public static final byte KIND_RACE = 1;

    public static final CustomPayload.Id<OpenGuiPayload> ID =
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "open_gui"));

    public static final PacketCodec<RegistryByteBuf, OpenGuiPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.BYTE, OpenGuiPayload::screenKind,
        OpenGuiPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

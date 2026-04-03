package com.greewsraces;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record OpenGuiPayload(byte screenKind) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "open_gui");

    public static final byte KIND_LANGUAGE = 0;
    public static final byte KIND_RACE = 1;

    public static void sendTo(ServerPlayerEntity player, byte screenKind) {
        PacketByteBuf buf = PacketByteBufs.create();
        new OpenGuiPayload(screenKind).write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static OpenGuiPayload read(PacketByteBuf buf) {
        return new OpenGuiPayload(buf.readByte());
    }

    public void write(PacketByteBuf buf) {
        buf.writeByte(screenKind);
    }
}

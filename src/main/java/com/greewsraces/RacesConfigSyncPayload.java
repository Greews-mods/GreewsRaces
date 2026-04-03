package com.greewsraces;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record RacesConfigSyncPayload(List<String> enabledRaceIds, boolean generateEvernightBiome) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "races_config_sync");

    public static void sendTo(ServerPlayerEntity player, RacesConfigSyncPayload payload) {
        PacketByteBuf buf = PacketByteBufs.create();
        payload.write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static RacesConfigSyncPayload read(PacketByteBuf buf) {
        int n = buf.readVarInt();
        List<String> ids = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ids.add(buf.readString());
        }
        boolean gen = buf.readBoolean();
        return new RacesConfigSyncPayload(ids, gen);
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(enabledRaceIds.size());
        for (String s : enabledRaceIds) {
            buf.writeString(s);
        }
        buf.writeBoolean(generateEvernightBiome);
    }
}

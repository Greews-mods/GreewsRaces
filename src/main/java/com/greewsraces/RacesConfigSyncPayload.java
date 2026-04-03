package com.greewsraces;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record RacesConfigSyncPayload(List<String> enabledRaceIds, boolean generateEvernightBiome) implements CustomPayload {

    public static final CustomPayload.Id<RacesConfigSyncPayload> ID =
        new CustomPayload.Id<>(Identifier.of(GreewsRaces.MOD_ID, "races_config_sync"));

    public static final PacketCodec<RegistryByteBuf, RacesConfigSyncPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), RacesConfigSyncPayload::enabledRaceIds,
        PacketCodecs.BOOL, RacesConfigSyncPayload::generateEvernightBiome,
        RacesConfigSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

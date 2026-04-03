package com.greewsraces;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record RaceSelectionPayload(String raceId) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "race_selection");

    public static RaceSelectionPayload read(PacketByteBuf buf) {
        return new RaceSelectionPayload(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(raceId);
    }
}

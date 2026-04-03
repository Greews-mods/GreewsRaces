package com.greewsraces;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record LanguageSelectionPayload(String languageCode) {
    public static final Identifier ID = new Identifier(GreewsRaces.MOD_ID, "language_selection");

    public static LanguageSelectionPayload read(PacketByteBuf buf) {
        return new LanguageSelectionPayload(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(languageCode);
    }
}

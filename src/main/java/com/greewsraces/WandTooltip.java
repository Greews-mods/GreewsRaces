package com.greewsraces;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class WandTooltip {
    private WandTooltip() {}

    public static List<Text> lines(WandOrbItem orb) {
        return List.of(
            Text.translatable("tooltip.greewsraces.category").formatted(Formatting.BLUE),
            Text.translatable("tooltip.greewsraces.brand").formatted(Formatting.BLUE),
            Text.translatable("tooltip.greewsraces.mana", 1).formatted(Formatting.GRAY),
            Text.translatable("tooltip.greewsraces.damage", (int) orb.getMagicDamage()).formatted(Formatting.GRAY),
            Text.translatable("tooltip.greewsraces.fairy_only").formatted(Formatting.DARK_PURPLE)
        );
    }
}

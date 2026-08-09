package com.greewsraces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class GuiScreenBackground {
    public static final Identifier RACE_SELECTION = Identifier.of("greewsraces", "textures/gui/race_selection_bg.png");
    public static final Identifier LANGUAGE_SELECTION = Identifier.of("greewsraces", "textures/gui/language_selection_bg.png");

    public static final int TEXTURE_WIDTH = 1024;
    public static final int TEXTURE_HEIGHT = 558;

    private GuiScreenBackground() {
    }

    public static void draw(DrawContext context, int screenWidth, int screenHeight, Identifier texture) {
        context.drawTexturedQuad(
            texture,
            0, 0, screenWidth, screenHeight,
            0.0F, 1.0F, 0.0F, 1.0F
        );
    }
}

package com.greewsraces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RaceSelectionScreen extends Screen {

    private int currentRaceIndex = 0;
    private final Race[] races;

    public RaceSelectionScreen() {
        super(Text.literal("Race Selection"));
        this.races = Race.values();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> {
            currentRaceIndex--;
            if (currentRaceIndex < 0) {
                currentRaceIndex = races.length - 1;
            }
        }).dimensions(centerX - 160, centerY, 30, 30).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> {
            currentRaceIndex++;
            if (currentRaceIndex >= races.length) {
                currentRaceIndex = 0;
            }
        }).dimensions(centerX + 130, centerY, 30, 30).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(Translation.get("select", ClientLanguageStorage.getLanguage())), 
            button -> {
                selectRace(races[currentRaceIndex]);
            }
        ).dimensions(this.width - 110, this.height - 30, 100, 20).build());

        Language currentLang = ClientLanguageStorage.getLanguage();
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(currentLang.getShortCode()), 
            button -> {
                // Cyklovat mezi všemi jazyky
                Language[] langs = Language.values();
                int currentIndex = 0;
                for (int i = 0; i < langs.length; i++) {
                    if (langs[i] == currentLang) {
                        currentIndex = i;
                        break;
                    }
                }
                Language newLang = langs[(currentIndex + 1) % langs.length];
                ClientLanguageStorage.setLanguage(newLang);
                
                ClientPlayNetworking.send(new LanguageSelectionPayload(newLang.getCode()));
                
                this.clearAndInit();
            }
        ).dimensions(this.width - 40, 10, 30, 20).build());
    }

    private void selectRace(Race race) {
        ClientPlayNetworking.send(new RaceSelectionPayload(race.getId()));
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Race race = races[currentRaceIndex];
        Language lang = ClientLanguageStorage.getLanguage();
        int centerX = this.width / 2;
        int startY = 30;

        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(Translation.get("select_race", lang)),
            centerX,
            10,
            0xFFFFFFFF
        );

        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal((currentRaceIndex + 1) + " / " + races.length),
            centerX,
            25,
            0xFF888888
        );

        int cardWidth = 240;
        int cardHeight = 220;
        int cardX = centerX - cardWidth / 2;
        int cardY = startY + 15;

        context.fill(cardX, cardY, cardX + cardWidth, cardY + cardHeight, 0xDD2A2A2A);

        int borderColor = race.getColor() | 0xFF000000;
        context.fill(cardX, cardY, cardX + cardWidth, cardY + 2, borderColor);
        context.fill(cardX, cardY + cardHeight - 2, cardX + cardWidth, cardY + cardHeight, borderColor);
        context.fill(cardX, cardY, cardX + 2, cardY + cardHeight, borderColor);
        context.fill(cardX + cardWidth - 2, cardY, cardX + cardWidth, cardY + cardHeight, borderColor);

        int contentY = cardY + 10;

        int iconSize = 48;
        int iconX = centerX - iconSize / 2;
        drawRaceIcon(context, iconX, contentY, iconSize, race);

        context.fill(iconX - 2, contentY - 2, iconX + iconSize + 2, contentY, borderColor);
        context.fill(iconX - 2, contentY + iconSize, iconX + iconSize + 2, contentY + iconSize + 2, borderColor);
        context.fill(iconX - 2, contentY - 2, iconX, contentY + iconSize + 2, borderColor);
        context.fill(iconX + iconSize, contentY - 2, iconX + iconSize + 2, contentY + iconSize + 2, borderColor);

        contentY += iconSize + 8;

        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(Translation.get("race." + race.getId(), lang)),
            centerX,
            contentY,
            race.getColor() | 0xFF000000
        );
        contentY += 10;

        String description = Translation.get("race." + race.getId() + ".desc", lang);
        drawWrappedText(context, description, cardX + 10, contentY, cardWidth - 20, 0xFF888888);
        contentY += getWrappedTextHeight(description, cardWidth - 20) + 10;

        context.fill(cardX + 10, contentY, cardX + cardWidth - 10, contentY + 1, 0xFF444444);
        contentY += 6;

        context.drawText(
            this.textRenderer,
            Translation.get("bonuses", lang),
            cardX + 10,
            contentY,
            0xFF55FF55,
            true
        );
        contentY += 10;

        String bonuses = Translation.get("race." + race.getId() + ".bonus", lang);
        for (String bonus : bonuses.split("\n")) {
            String clean = bonus.trim();
            if (!clean.isEmpty()) {
                drawWrappedText(context, "+ " + clean, cardX + 12, contentY, cardWidth - 24, 0xFF55FF55);
                contentY += getWrappedTextHeight("+ " + clean, cardWidth - 24) + 2;
            }
        }

        contentY += 4;

        context.drawText(
            this.textRenderer,
            Translation.get("maluses", lang),
            cardX + 10,
            contentY,
            0xFFFF5555,
            true
        );
        contentY += 10;

        String maluses = Translation.get("race." + race.getId() + ".malus", lang);
        for (String malus : maluses.split("\n")) {
            String clean = malus.trim();
            if (!clean.isEmpty()) {
                drawWrappedText(context, "- " + clean, cardX + 12, contentY, cardWidth - 24, 0xFFFF5555);
                contentY += getWrappedTextHeight("- " + clean, cardWidth - 24) + 2;
            }
        }
    }

    private void drawRaceIcon(DrawContext context, int x, int y, int size, Race race) {
        RaceIcon icon = AllRaceIcons.byRace(race);
        
        try {
            context.drawTexture(
                net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
                RaceIcon.SPRITE_SHEET,
                x, y,
                (float) icon.getX(), (float) icon.getY(),
                size, size,
                384, 64
            );
        } catch (Exception e) {
            drawFallbackIcon(context, x, y, size, race);
        }
    }
    private void drawFallbackIcon(DrawContext context, int x, int y, int size, Race race) {
        int iconColor = race.getColor() | 0xFF000000;
        context.fill(x, y, x + size, y + size, iconColor);

        for (int i = 0; i < size / 2; i++) {
            int alpha = 80 - (i * 2);
            if (alpha > 0) {
                context.fill(x, y + size - i - 1, x + size, y + size - i, (alpha << 24));
            }
        }

        String initial = Translation.get("race." + race.getId(), ClientLanguageStorage.getLanguage())
            .substring(0, 1).toUpperCase();
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(initial),
            x + size / 2,
            y + size / 2 - 4,
            0xFFFFFFFF
        );
    }

    private void drawWrappedText(DrawContext context, String text, int x, int y, int maxWidth, int color) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int width = this.textRenderer.getWidth(testLine);

            if (width > maxWidth && line.length() > 0) {
                context.drawText(this.textRenderer, line.toString(), x, currentY, color, true);
                currentY += 9;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }

        if (line.length() > 0) {
            context.drawText(this.textRenderer, line.toString(), x, currentY, color, true);
        }
    }

    private int getWrappedTextHeight(String text, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lines = 0;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int width = this.textRenderer.getWidth(testLine);

            if (width > maxWidth && line.length() > 0) {
                lines++;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }

        if (line.length() > 0) {
            lines++;
        }

        return lines * 9;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
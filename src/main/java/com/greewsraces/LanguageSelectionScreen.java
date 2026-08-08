package com.greewsraces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class LanguageSelectionScreen extends Screen {
    private static final int COLS = 2;
    private static final int BUTTON_WIDTH = 110;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP_X = 8;
    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_TOP = 40;
    private static final int CONTENT_BOTTOM_PADDING = 16;

    private int scrollOffset = 0;

    public LanguageSelectionScreen() {
        super(Text.literal("Language"));
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int panelWidth = COLS * BUTTON_WIDTH + (COLS - 1) * GAP_X;
        int startX = centerX - panelWidth / 2;
        int startY = CONTENT_TOP - scrollOffset;

        Language[] languages = Language.values();
        for (int i = 0; i < languages.length; i++) {
            Language lang = languages[i];
            int col = i % COLS;
            int row = i / COLS;
            int buttonX = startX + col * (BUTTON_WIDTH + GAP_X);
            int buttonY = startY + row * ROW_HEIGHT;

            if (buttonY + BUTTON_HEIGHT < CONTENT_TOP || buttonY > this.height - CONTENT_BOTTOM_PADDING) {
                continue;
            }

            this.addDrawableChild(ButtonWidget.builder(
                Text.literal(lang.getDisplayName() + " (" + lang.getShortCode() + ")"),
                b -> selectLanguage(lang)
            ).dimensions(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        }
    }

    private int maxScroll() {
        int totalRows = (Language.values().length + COLS - 1) / COLS;
        int contentHeight = totalRows * ROW_HEIGHT;
        int visibleHeight = this.height - CONTENT_TOP - CONTENT_BOTTOM_PADDING;
        return Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        if (maxScroll() <= 0) {
            return super.mouseScrolled(mouseX, mouseY, verticalAmount);
        }

        scrollOffset = Math.max(0, Math.min(maxScroll(), scrollOffset - (int) Math.round(verticalAmount * ROW_HEIGHT)));
        this.init();
        return true;
    }

    private void selectLanguage(Language lang) {
        var buf = PacketByteBufs.create();
        buf.writeString(lang.getCode());
        ClientPlayNetworking.send(LanguageSelectionPayload.ID, buf);
        ClientLanguageStorage.applyLocalLanguageChoice(lang);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        if (GreewsRacesClient.needsRaceSelection()) {
            client.setScreen(new RaceSelectionScreen());
        } else {
            client.setScreen(null);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        MinecraftClient client = MinecraftClient.getInstance();
        Language uiLang = ClientLanguageStorage.hasServerLanguageChoice()
            ? ClientLanguageStorage.getLanguage()
            : Language.fromMinecraftLocale(
                client != null && client.options != null ? client.options.language : ""
            );
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(Translation.get("select_language", uiLang)),
            this.width / 2,
            16,
            0xFFFFFFFF
        );

        if (maxScroll() > 0) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Scroll"),
                this.width / 2,
                this.height - CONTENT_BOTTOM_PADDING + 2,
                0xFF888888
            );
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

package com.greewsraces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class LanguageSelectionScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP_X = 8;
    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_BOTTOM_PADDING = 16;

    private int scrollOffset = 0;

    public LanguageSelectionScreen() {
        super(Text.literal("Language"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        GuiScreenBackground.draw(context, this.width, this.height, GuiScreenBackground.LANGUAGE_SELECTION);
    }

    private int contentTop() {
        return Math.max(68, (int) (this.height * 0.14F));
    }

    private int cols() {
        return this.width < 260 ? 1 : 2;
    }

    private int buttonWidth() {
        int columnCount = cols();
        int maxPanelWidth = Math.max(120, this.width - 20);
        return Math.min(110, (maxPanelWidth - (columnCount - 1) * GAP_X) / columnCount);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int columnCount = cols();
        int buttonWidth = buttonWidth();
        int panelWidth = columnCount * buttonWidth + (columnCount - 1) * GAP_X;
        int startX = centerX - panelWidth / 2;
        int startY = contentTop() - scrollOffset;
        int contentTop = contentTop();

        Language[] languages = Language.values();
        for (int i = 0; i < languages.length; i++) {
            Language lang = languages[i];
            int col = i % columnCount;
            int row = i / columnCount;
            int buttonX = startX + col * (buttonWidth + GAP_X);
            int buttonY = startY + row * ROW_HEIGHT;

            if (buttonY + BUTTON_HEIGHT < contentTop || buttonY > this.height - CONTENT_BOTTOM_PADDING) {
                continue;
            }

            this.addDrawableChild(ButtonWidget.builder(
                Text.literal(lang.getDisplayName() + " (" + lang.getShortCode() + ")"),
                b -> selectLanguage(lang)
            ).dimensions(buttonX, buttonY, buttonWidth, BUTTON_HEIGHT).build());
        }
    }

    private int maxScroll() {
        int columnCount = cols();
        int totalRows = (Language.values().length + columnCount - 1) / columnCount;
        int contentHeight = totalRows * ROW_HEIGHT;
        int visibleHeight = this.height - contentTop() - CONTENT_BOTTOM_PADDING;
        return Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (maxScroll() <= 0) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        scrollOffset = Math.max(0, Math.min(maxScroll(), scrollOffset - (int) Math.round(verticalAmount * ROW_HEIGHT)));
        this.init();
        return true;
    }

    private void selectLanguage(Language lang) {
        ClientPlayNetworking.send(new LanguageSelectionPayload(lang.getCode()));
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

        if (maxScroll() > 0) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Scroll"),
                this.width / 2,
                this.height - CONTENT_BOTTOM_PADDING + 2,
                0xFFCCCCCC
            );
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

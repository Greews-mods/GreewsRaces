package com.greewsraces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class LanguageSelectionScreen extends Screen {

    public LanguageSelectionScreen() {
        super(Text.literal("Language"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 2 - Language.values().length * 12;

        int i = 0;
        for (Language lang : Language.values()) {
            int rowY = y + i * 24;
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal(lang.getDisplayName() + " (" + lang.getShortCode() + ")"),
                b -> selectLanguage(lang)
            ).dimensions(centerX - 100, rowY, 200, 20).build());
            i++;
        }
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
            20,
            0xFFFFFFFF
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

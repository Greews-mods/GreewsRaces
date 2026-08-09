package com.greewsraces;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RaceSelectionScreen extends Screen {

    private int currentRaceIndex = 0;
    private final Race[] races;
    private CarouselLayout layout;

    public RaceSelectionScreen() {
        super(Text.literal("Race Selection"));
        Race[] available = ClientRaceConfig.enabledRacesForUi();
        this.races = available.length > 0 ? available : new Race[] { Race.HUMAN };
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        GuiScreenBackground.draw(context, this.width, this.height, GuiScreenBackground.RACE_SELECTION);
    }

    @Override
    protected void init() {
        this.layout = computeLayout();

        if (races.length > 1) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> shiftRace(-1))
                .dimensions(layout.leftArrowX, layout.arrowY, layout.arrowSize, layout.arrowSize)
                .build());

            this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> shiftRace(1))
                .dimensions(layout.rightArrowX, layout.arrowY, layout.arrowSize, layout.arrowSize)
                .build());
        }

        int selectWidth = Math.min(120, Math.max(80, this.width / 5));
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(Translation.get("select", ClientLanguageStorage.getLanguage())),
            button -> selectRace(races[currentRaceIndex])
        ).dimensions(this.width / 2 - selectWidth / 2, this.height - 30, selectWidth, 20).build());
    }

    private CarouselLayout computeLayout() {
        int margin = Math.max(4, this.width / 80);
        int counterY = Math.max(46, (int) (this.height * 0.11F));
        int topReserve = counterY + 16;
        int bottomReserve = 34;
        int availW = Math.max(120, this.width - margin * 2);
        int availH = Math.max(100, this.height - topReserve - bottomReserve);

        int arrowSize = clamp(availW / 18, 18, 30);
        int gap = clamp(availW / 45, 4, 12);

        int centerW = clamp(Math.min(260, availW - 2 * (arrowSize + gap + 8)), 140, 260);
        int centerH = clamp(Math.min(240, availH), 120, 240);

        int previewW = clamp((availW - centerW - 2 * (arrowSize + gap * 3)) / 2, 0, 96);
        int previewH = previewW > 0 ? clamp((int) (previewW * 1.25F), 64, 120) : 0;

        int centerX = this.width / 2;
        int centerY = topReserve + availH / 2;

        int centerLeft = centerX - centerW / 2;
        int centerRight = centerX + centerW / 2;
        int leftArrowX = centerLeft - gap - arrowSize;
        int rightArrowX = centerRight + gap;

        boolean showPreviews = races.length > 1 && previewW >= 52;
        if (showPreviews) {
            int leftPreviewX = leftArrowX - gap - previewW;
            int rightPreviewX = rightArrowX + arrowSize + gap;
            int neededW = (rightPreviewX + previewW + margin) - (leftPreviewX - margin);
            if (neededW > this.width) {
                previewW = clamp((this.width - centerW - 2 * (arrowSize + gap * 3) - margin * 2) / 2, 0, previewW);
                previewH = previewW > 0 ? clamp((int) (previewW * 1.25F), 64, 120) : 0;
                showPreviews = previewW >= 52;
            }
            if (showPreviews) {
                leftPreviewX = Math.max(margin, centerLeft - gap - arrowSize - gap - previewW);
                rightPreviewX = Math.min(this.width - margin - previewW, centerRight + gap + arrowSize + gap);
                leftArrowX = leftPreviewX + previewW + gap;
                rightArrowX = rightPreviewX - gap - arrowSize;
                showPreviews = leftArrowX > margin && rightArrowX + arrowSize < this.width - margin;
            }
        }

        if (!showPreviews) {
            previewW = 0;
            previewH = 0;
            leftArrowX = Math.max(margin, centerLeft - gap - arrowSize);
            rightArrowX = Math.min(this.width - margin - arrowSize, centerRight + gap);
        }

        int leftPreviewX = showPreviews ? leftArrowX - gap - previewW : 0;
        int rightPreviewX = showPreviews ? rightArrowX + arrowSize + gap : 0;
        int arrowY = centerY - arrowSize / 2;

        return new CarouselLayout(
            centerX,
            centerY,
            centerW,
            centerH,
            leftPreviewX,
            rightPreviewX,
            previewW,
            previewH,
            leftArrowX,
            rightArrowX,
            arrowY,
            arrowSize,
            showPreviews,
            counterY
        );
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void shiftRace(int delta) {
        currentRaceIndex = wrapIndex(currentRaceIndex + delta);
    }

    private int wrapIndex(int index) {
        if (races.length == 0) {
            return 0;
        }
        int wrapped = index % races.length;
        return wrapped < 0 ? wrapped + races.length : wrapped;
    }

    private int previousIndex() {
        return wrapIndex(currentRaceIndex - 1);
    }

    private int nextIndex() {
        return wrapIndex(currentRaceIndex + 1);
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

        if (this.layout == null) {
            this.layout = computeLayout();
        }

        Language lang = ClientLanguageStorage.getLanguage();

        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal((currentRaceIndex + 1) + " / " + races.length),
            layout.centerX,
            layout.counterY,
            0xFFCCCCCC
        );

        if (layout.showPreviews) {
            drawRacePreview(
                context,
                layout.leftPreviewX,
                layout.centerY - layout.previewH / 2,
                layout.previewW,
                layout.previewH,
                races[previousIndex()],
                lang,
                true
            );
            drawRacePreview(
                context,
                layout.rightPreviewX,
                layout.centerY - layout.previewH / 2,
                layout.previewW,
                layout.previewH,
                races[nextIndex()],
                lang,
                false
            );
        }

        drawRaceCard(
            context,
            layout.centerX - layout.centerW / 2,
            layout.centerY - layout.centerH / 2,
            layout.centerW,
            layout.centerH,
            races[currentRaceIndex],
            lang
        );
    }

    private void drawRacePreview(
        DrawContext context,
        int x,
        int y,
        int width,
        int height,
        Race race,
        Language lang,
        boolean previous
    ) {
        context.fill(x, y, x + width, y + height, 0xAA1A1A1A);

        int borderColor = (race.getColor() & 0x00FFFFFF) | 0x88000000;
        context.fill(x, y, x + width, y + 1, borderColor);
        context.fill(x, y + height - 1, x + width, y + height, borderColor);
        context.fill(x, y, x + 1, y + height, borderColor);
        context.fill(x + width - 1, y, x + width, y + height, borderColor);

        int iconPaddingTop = 8;
        int iconSize = clamp(width - 16, 24, 40);
        int iconX = x + (width - iconSize) / 2;
        int iconY = y + iconPaddingTop;
        drawRaceIcon(context, iconX, iconY, iconSize, race);

        String label = Translation.get("race." + race.getId(), lang);
        int labelY = iconY + iconSize + 4;
        int labelAreaHeight = height - iconPaddingTop - iconSize - 8;
        drawWrappedCenteredText(
            context,
            label,
            x + width / 2,
            labelY,
            width - 8,
            race.getColor() | 0xFF000000,
            Math.max(1, labelAreaHeight / 9)
        );
    }

    private void drawRaceCard(
        DrawContext context,
        int cardX,
        int cardY,
        int cardWidth,
        int cardHeight,
        Race race,
        Language lang
    ) {
        int centerX = cardX + cardWidth / 2;
        int padding = clamp(cardWidth / 24, 6, 12);
        int textWidth = cardWidth - padding * 2;

        context.fill(cardX, cardY, cardX + cardWidth, cardY + cardHeight, 0xDD1E1E1E);

        int borderColor = race.getColor() | 0xFF000000;
        context.fill(cardX, cardY, cardX + cardWidth, cardY + 2, borderColor);
        context.fill(cardX, cardY + cardHeight - 2, cardX + cardWidth, cardY + cardHeight, borderColor);
        context.fill(cardX, cardY, cardX + 2, cardY + cardHeight, borderColor);
        context.fill(cardX + cardWidth - 2, cardY, cardX + cardWidth, cardY + cardHeight, borderColor);

        int contentY = cardY + padding;
        int iconSize = clamp(Math.min(cardWidth / 4, cardHeight / 5), 28, 52);
        int iconX = centerX - iconSize / 2;
        drawRaceIcon(context, iconX, contentY, iconSize, race);

        contentY += iconSize + 6;

        drawWrappedCenteredText(
            context,
            Translation.get("race." + race.getId(), lang),
            centerX,
            contentY,
            textWidth,
            race.getColor() | 0xFF000000,
            2
        );
        contentY += getWrappedTextHeight(Translation.get("race." + race.getId(), lang), textWidth) + 6;

        String description = Translation.get("race." + race.getId() + ".desc", lang);
        drawWrappedText(context, description, cardX + padding, contentY, textWidth, 0xFFAAAAAA);
        contentY += getWrappedTextHeight(description, textWidth) + 6;

        if (contentY + 40 > cardY + cardHeight) {
            return;
        }

        context.fill(cardX + padding, contentY, cardX + cardWidth - padding, contentY + 1, 0xFF444444);
        contentY += 5;

        context.drawText(
            this.textRenderer,
            Translation.get("bonuses", lang),
            cardX + padding,
            contentY,
            0xFF55FF55,
            true
        );
        contentY += 10;

        String bonuses = Translation.get("race." + race.getId() + ".bonus", lang);
        for (String bonus : bonuses.split("\n")) {
            String clean = bonus.trim();
            if (!clean.isEmpty()) {
                if (contentY + 9 > cardY + cardHeight) {
                    break;
                }
                drawWrappedText(context, "+ " + clean, cardX + padding + 2, contentY, textWidth - 4, 0xFF55FF55);
                contentY += getWrappedTextHeight("+ " + clean, textWidth - 4) + 2;
            }
        }

        contentY += 3;

        if (contentY + 20 > cardY + cardHeight) {
            return;
        }

        context.drawText(
            this.textRenderer,
            Translation.get("maluses", lang),
            cardX + padding,
            contentY,
            0xFFFF5555,
            true
        );
        contentY += 10;

        String maluses = Translation.get("race." + race.getId() + ".malus", lang);
        for (String malus : maluses.split("\n")) {
            String clean = malus.trim();
            if (!clean.isEmpty()) {
                if (contentY + 9 > cardY + cardHeight) {
                    break;
                }
                drawWrappedText(context, "- " + clean, cardX + padding + 2, contentY, textWidth - 4, 0xFFFF5555);
                contentY += getWrappedTextHeight("- " + clean, textWidth - 4) + 2;
            }
        }
    }

    private void drawRaceIcon(DrawContext context, int x, int y, int size, Race race) {
        RaceIcon icon = AllRaceIcons.byRace(race);
        context.drawTexturedQuad(
            icon.getTexture(),
            x, y, x + size, y + size,
            0.0F, 1.0F, 0.0F, 1.0F
        );
    }

    private void drawWrappedText(DrawContext context, String text, int x, int y, int maxWidth, int color) {
        for (String line : wrapText(text, maxWidth)) {
            context.drawText(this.textRenderer, line, x, y, color, true);
            y += 9;
        }
    }

    private void drawWrappedCenteredText(
        DrawContext context,
        String text,
        int centerX,
        int y,
        int maxWidth,
        int color,
        int maxLines
    ) {
        java.util.List<String> lines = wrapText(text, maxWidth);
        int lineCount = Math.min(maxLines, lines.size());
        for (int i = 0; i < lineCount; i++) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(lines.get(i)),
                centerX,
                y + i * 9,
                color
            );
        }
    }

    private java.util.List<String> wrapText(String text, int maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            if (this.textRenderer.getWidth(testLine) > maxWidth && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        if (lines.isEmpty()) {
            lines.add("");
        }

        return lines;
    }

    private int getWrappedTextHeight(String text, int maxWidth) {
        return wrapText(text, maxWidth).size() * 9;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (races.length > 1) {
            if (input.key() == GLFW.GLFW_KEY_LEFT) {
                shiftRace(-1);
                return true;
            }
            if (input.key() == GLFW.GLFW_KEY_RIGHT) {
                shiftRace(1);
                return true;
            }
        }
        return super.keyPressed(input);
    }

    private static final class CarouselLayout {
        private final int centerX;
        private final int centerY;
        private final int centerW;
        private final int centerH;
        private final int leftPreviewX;
        private final int rightPreviewX;
        private final int previewW;
        private final int previewH;
        private final int leftArrowX;
        private final int rightArrowX;
        private final int arrowY;
        private final int arrowSize;
        private final boolean showPreviews;
        private final int counterY;

        private CarouselLayout(
            int centerX,
            int centerY,
            int centerW,
            int centerH,
            int leftPreviewX,
            int rightPreviewX,
            int previewW,
            int previewH,
            int leftArrowX,
            int rightArrowX,
            int arrowY,
            int arrowSize,
            boolean showPreviews,
            int counterY
        ) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerW = centerW;
            this.centerH = centerH;
            this.leftPreviewX = leftPreviewX;
            this.rightPreviewX = rightPreviewX;
            this.previewW = previewW;
            this.previewH = previewH;
            this.leftArrowX = leftArrowX;
            this.rightArrowX = rightArrowX;
            this.arrowY = arrowY;
            this.arrowSize = arrowSize;
            this.showPreviews = showPreviews;
            this.counterY = counterY;
        }
    }
}

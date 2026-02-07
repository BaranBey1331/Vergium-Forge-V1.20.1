package com.vergium.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;

public class VergiumSlider extends AbstractWidget {

    private final String label;
    private final int minValue;
    private final int maxValue;
    private int value;
    private final Consumer<Integer> onChange;
    private final Function<Integer, String> formatter;
    private boolean dragging = false;

    public VergiumSlider(int x, int y, int width, int height,
                          String label, int min, int max, int initialValue,
                          Consumer<Integer> onChange, Function<Integer, String> formatter) {
        super(x, y, width, height, Component.literal(label));
        this.label = label;
        this.minValue = min;
        this.maxValue = max;
        this.value = Mth.clamp(initialValue, min, max);
        this.onChange = onChange;
        this.formatter = formatter;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHoveredOrFocused();

        // Background
        graphics.fill(this.getX(), this.getY(),
            this.getX() + this.getWidth(), this.getY() + this.getHeight(),
            0xAA000000);

        // Slider track
        int trackY = this.getY() + this.getHeight() - 6;
        int trackHeight = 4;
        graphics.fill(this.getX() + 2, trackY,
            this.getX() + this.getWidth() - 2, trackY + trackHeight,
            0xFF333333);

        // Slider fill
        float progress = (float) (value - minValue) / (maxValue - minValue);
        int fillWidth = (int) ((this.getWidth() - 4) * progress);
        graphics.fill(this.getX() + 2, trackY,
            this.getX() + 2 + fillWidth, trackY + trackHeight,
            0xFF4488DD);

        // Slider handle
        int handleX = this.getX() + 2 + fillWidth - 3;
        graphics.fill(handleX, trackY - 1,
            handleX + 6, trackY + trackHeight + 1,
            hovered || dragging ? 0xFFFFFFFF : 0xFFCCCCCC);

        // Border
        int borderColor = hovered ? 0xFFFFFFFF : 0xFF666666;
        graphics.fill(this.getX(), this.getY(),
            this.getX() + this.getWidth(), this.getY() + 1, borderColor);
        graphics.fill(this.getX(), this.getY() + this.getHeight() - 1,
            this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor);
        graphics.fill(this.getX(), this.getY(),
            this.getX() + 1, this.getY() + this.getHeight(), borderColor);
        graphics.fill(this.getX() + this.getWidth() - 1, this.getY(),
            this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor);

        // Label on left
        graphics.drawString(Minecraft.getInstance().font,
            label, this.getX() + 5, this.getY() + 2, 0xFFFFFF);

        // Value on right
        String valueText = formatter.apply(value);
        graphics.drawString(Minecraft.getInstance().font,
            valueText,
            this.getX() + this.getWidth() - Minecraft.getInstance().font.width(valueText) - 5,
            this.getY() + 2,
            0xFF88BBFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        dragging = true;
        updateValue(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (dragging) {
            updateValue(mouseX);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = false;
    }

    private void updateValue(double mouseX) {
        float progress = (float) (mouseX - this.getX() - 2) / (this.getWidth() - 4);
        progress = Mth.clamp(progress, 0, 1);
        int newValue = (int) (minValue + progress * (maxValue - minValue));
        if (newValue != value) {
            value = newValue;
            onChange.accept(value);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public int getValue() {
        return value;
    }
}

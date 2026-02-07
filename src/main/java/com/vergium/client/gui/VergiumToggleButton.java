package com.vergium.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class VergiumToggleButton extends AbstractWidget {

    private boolean value;
    private final String label;
    private final Consumer<Boolean> onChange;

    public VergiumToggleButton(int x, int y, int width, int height,
                                String label, boolean initialValue,
                                Consumer<Boolean> onChange) {
        super(x, y, width, height, Component.literal(label));
        this.label = label;
        this.value = initialValue;
        this.onChange = onChange;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        value = !value;
        onChange.accept(value);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHoveredOrFocused();

        // Background
        int bgColor = value ? 0x8844AA44 : 0x88AA4444;
        if (hovered) {
            bgColor = value ? 0xAA66CC66 : 0xAACC6666;
        }
        graphics.fill(this.getX(), this.getY(),
            this.getX() + this.getWidth(), this.getY() + this.getHeight(),
            bgColor);

        // Border
        int borderColor = hovered ? 0xFFFFFFFF : 0xFF888888;
        graphics.fill(this.getX(), this.getY(),
            this.getX() + this.getWidth(), this.getY() + 1, borderColor);
        graphics.fill(this.getX(), this.getY() + this.getHeight() - 1,
            this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor);
        graphics.fill(this.getX(), this.getY(),
            this.getX() + 1, this.getY() + this.getHeight(), borderColor);
        graphics.fill(this.getX() + this.getWidth() - 1, this.getY(),
            this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor);

        // Toggle indicator
        String statusText = value ? "ON" : "OFF";
        int statusColor = value ? 0xFF44FF44 : 0xFFFF4444;

        // Label on left
        graphics.drawString(Minecraft.getInstance().font,
            label, this.getX() + 5, this.getY() + 6, 0xFFFFFF);

        // Status on right
        graphics.drawString(Minecraft.getInstance().font,
            statusText,
            this.getX() + this.getWidth() - Minecraft.getInstance().font.width(statusText) - 5,
            this.getY() + 6,
            statusColor);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public boolean getValue() {
        return value;
    }
}

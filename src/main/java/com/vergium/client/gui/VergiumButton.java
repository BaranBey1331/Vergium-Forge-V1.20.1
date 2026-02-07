package com.vergium.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class VergiumButton extends Button {

    private final int baseColor;
    private final int hoverColor;

    public VergiumButton(int x, int y, int width, int height, Component message,
                          OnPress onPress, int baseColor, int hoverColor) {
        super(Button.builder(message, onPress).bounds(x, y, width, height));
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = this.isHoveredOrFocused();
        int color = hovered ? hoverColor : baseColor;

        // Background
        graphics.fill(this.getX(), this.getY(),
            this.getX() + this.getWidth(), this.getY() + this.getHeight(),
            0xAA000000);

        // Border
        int borderColor = hovered ? 0xFFFFFFFF : color;
        graphics.fill(this.getX(), this.getY(),
            this.getX() + this.getWidth(), this.getY() + 1, borderColor);
        graphics.fill(this.getX(), this.getY() + this.getHeight() - 1,
            this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor);
        graphics.fill(this.getX(), this.getY(),
            this.getX() + 1, this.getY() + this.getHeight(), borderColor);
        graphics.fill(this.getX() + this.getWidth() - 1, this.getY(),
            this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor);

        // Text
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
            this.getMessage(),
            this.getX() + this.getWidth() / 2,
            this.getY() + (this.getHeight() - 8) / 2,
            hovered ? 0xFFFFFF : 0xE0E0E0);
    }
}

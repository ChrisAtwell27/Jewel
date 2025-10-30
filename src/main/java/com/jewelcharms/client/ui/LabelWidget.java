package com.jewelcharms.client.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Simple text label widget
 */
public class LabelWidget extends Widget {
    private Component text;
    private int color = 0xFFFFFFFF;
    private boolean shadow = true;
    private Alignment alignment = Alignment.LEFT;

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public LabelWidget(Component text) {
        super(0, 9); // Height of text
        this.text = text;
    }

    public LabelWidget(Component text, int color) {
        this(text);
        this.color = color;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        int textX = x;

        // Calculate X based on alignment
        int textWidth = font.width(text);
        if (alignment == Alignment.CENTER) {
            textX = x + (width - textWidth) / 2;
        } else if (alignment == Alignment.RIGHT) {
            textX = x + width - textWidth;
        }

        graphics.drawString(font, text, textX, y, color, shadow);
    }

    public LabelWidget setText(Component text) {
        this.text = text;
        return this;
    }

    public LabelWidget setColor(int color) {
        this.color = color;
        return this;
    }

    public LabelWidget setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public LabelWidget setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }
}

package com.jewelcharms.client.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

/**
 * Button widget with hover effects and click handling
 */
public class ButtonWidget extends Widget {
    private final Component text;
    private final Runnable onClick;
    private boolean hovered = false;
    private int normalColor = 0xFF8B8B8B;
    private int hoverColor = 0xFFA0A0A0;
    private int disabledColor = 0xFF5F5F5F;
    private int textColor = 0xFFFFFFFF;

    public ButtonWidget(int width, int height, Component text, Runnable onClick) {
        super(width, height);
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        hovered = isMouseOver(mouseX, mouseY) && enabled;

        // Choose color based on state
        int bgColor = enabled ? (hovered ? hoverColor : normalColor) : disabledColor;

        // Draw button background
        graphics.fill(x, y, x + width, y + height, bgColor);

        // Draw border
        int borderColor = hovered ? 0xFFFFFFFF : 0xFF000000;
        graphics.fill(x, y, x + width, y + 1, borderColor); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, borderColor); // Bottom
        graphics.fill(x, y, x + 1, y + height, borderColor); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, borderColor); // Right

        // Draw text centered
        int textWidth = net.minecraft.client.Minecraft.getInstance().font.width(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, text, textX, textY, textColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && enabled && isMouseOver(mouseX, mouseY)) {
            // Play click sound
            playClickSound();

            // Execute callback
            if (onClick != null) {
                onClick.run();
            }
            return true;
        }
        return false;
    }

    private void playClickSound() {
        // Play button click sound
        net.minecraft.client.Minecraft.getInstance().getSoundManager()
            .play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public ButtonWidget setColors(int normal, int hover, int disabled) {
        this.normalColor = normal;
        this.hoverColor = hover;
        this.disabledColor = disabled;
        return this;
    }

    public ButtonWidget setTextColor(int color) {
        this.textColor = color;
        return this;
    }
}

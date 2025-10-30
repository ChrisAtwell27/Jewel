package com.jewelcharms.client.ui;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Base class for all UI widgets - programmatic UI building blocks
 */
public abstract class Widget {
    protected int x, y;
    protected int width, height;
    protected boolean visible = true;
    protected boolean enabled = true;

    public Widget(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set the position of this widget (called by layout system)
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Render this widget
     */
    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * Handle mouse click
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Handle mouse release
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Check if mouse is over this widget
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

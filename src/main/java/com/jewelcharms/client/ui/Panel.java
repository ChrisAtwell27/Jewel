package com.jewelcharms.client.ui;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Container widget that holds and layouts child widgets
 */
public class Panel extends Widget {
    private final List<Widget> children = new ArrayList<>();
    private LayoutDirection direction = LayoutDirection.VERTICAL;
    private int padding = 5;
    private int spacing = 5;
    private int backgroundColor = 0;
    private boolean drawBackground = false;

    public enum LayoutDirection {
        VERTICAL,
        HORIZONTAL
    }

    public Panel(int width, int height) {
        super(width, height);
    }

    /**
     * Add a child widget - it will be automatically positioned
     */
    public Panel addChild(Widget widget) {
        children.add(widget);
        updateLayout();
        return this;
    }

    /**
     * Set layout direction (VERTICAL or HORIZONTAL)
     */
    public Panel setDirection(LayoutDirection direction) {
        this.direction = direction;
        updateLayout();
        return this;
    }

    /**
     * Set padding (space between panel edge and content)
     */
    public Panel setPadding(int padding) {
        this.padding = padding;
        updateLayout();
        return this;
    }

    /**
     * Set spacing (space between child widgets)
     */
    public Panel setSpacing(int spacing) {
        this.spacing = spacing;
        updateLayout();
        return this;
    }

    /**
     * Set background color (ARGB format)
     */
    public Panel setBackgroundColor(int color) {
        this.backgroundColor = color;
        this.drawBackground = true;
        return this;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        updateLayout();
    }

    /**
     * Automatically position all child widgets based on layout direction
     */
    private void updateLayout() {
        int currentX = x + padding;
        int currentY = y + padding;

        for (Widget child : children) {
            child.setPosition(currentX, currentY);

            if (direction == LayoutDirection.VERTICAL) {
                currentY += child.getHeight() + spacing;
            } else {
                currentX += child.getWidth() + spacing;
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        // Draw background if enabled
        if (drawBackground) {
            graphics.fill(x, y, x + width, y + height, backgroundColor);
        }

        // Render all children
        for (Widget child : children) {
            if (child.isVisible()) {
                child.render(graphics, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;

        // Check children in reverse order (top-most first)
        for (int i = children.size() - 1; i >= 0; i--) {
            Widget child = children.get(i);
            if (child.isVisible() && child.isEnabled()) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        for (Widget child : children) {
            if (child.isVisible()) {
                if (child.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public void clearChildren() {
        children.clear();
    }
}

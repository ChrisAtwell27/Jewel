package com.jewelcharms.client.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * Progress bar widget with smooth animations
 */
public class ProgressBarWidget extends Widget {
    private float progress = 0f; // 0.0 to 1.0
    private float displayProgress = 0f; // Smoothed for animation
    private int backgroundColor = 0xFF333333;
    private int fillColor = 0xFF00FF00;
    private int borderColor = 0xFF000000;
    private boolean showBorder = true;
    private boolean animate = true;

    public ProgressBarWidget(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        // Smooth animation
        if (animate) {
            displayProgress = Mth.lerp(0.2f, displayProgress, progress);
        } else {
            displayProgress = progress;
        }

        // Draw background
        graphics.fill(x, y, x + width, y + height, backgroundColor);

        // Draw fill
        int fillWidth = (int)(width * displayProgress);
        if (fillWidth > 0) {
            graphics.fill(x, y, x + fillWidth, y + height, fillColor);
        }

        // Draw border
        if (showBorder) {
            graphics.fill(x, y, x + width, y + 1, borderColor); // Top
            graphics.fill(x, y + height - 1, x + width, y + height, borderColor); // Bottom
            graphics.fill(x, y, x + 1, y + height, borderColor); // Left
            graphics.fill(x + width - 1, y, x + width, y + height, borderColor); // Right
        }
    }

    /**
     * Set progress (0.0 to 1.0)
     */
    public ProgressBarWidget setProgress(float progress) {
        this.progress = Mth.clamp(progress, 0f, 1f);
        return this;
    }

    public float getProgress() {
        return progress;
    }

    public ProgressBarWidget setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public ProgressBarWidget setFillColor(int color) {
        this.fillColor = color;
        return this;
    }

    public ProgressBarWidget setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    public ProgressBarWidget setShowBorder(boolean show) {
        this.showBorder = show;
        return this;
    }

    public ProgressBarWidget setAnimate(boolean animate) {
        this.animate = animate;
        return this;
    }
}

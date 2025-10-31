package com.jewelcharms.client.gui;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.ToolJewelData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HUD overlay that displays active jewel effects
 */
@OnlyIn(Dist.CLIENT)
public class JewelEffectHUD implements IGuiOverlay {

    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation(JewelCharms.MOD_ID, "textures/gui/jewel_hud.png");
    private static final int ICON_SIZE = 16;
    private static final int HUD_PADDING = 4;
    private static final int FADE_TIME = 20; // Ticks to fade in/out

    private final Map<JewelEffect, EffectDisplay> activeEffects = new HashMap<>();
    private int animationTick = 0;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        // Update active effects from held item
        updateActiveEffects(player);

        if (activeEffects.isEmpty()) {
            return;
        }

        // Position HUD in top-left corner, below health/hunger bars
        int x = HUD_PADDING;
        int y = 60; // Below typical health/hunger position

        // Update animation
        animationTick++;

        // Render background panel
        int panelWidth = calculatePanelWidth();
        int panelHeight = calculatePanelHeight();
        renderPanel(graphics, x, y, panelWidth, panelHeight, partialTick);

        // Render effects
        int effectY = y + HUD_PADDING;
        for (Map.Entry<JewelEffect, EffectDisplay> entry : activeEffects.entrySet()) {
            renderEffect(graphics, x + HUD_PADDING, effectY, entry.getKey(), entry.getValue(), partialTick);
            effectY += ICON_SIZE + 2;
        }
    }

    private void updateActiveEffects(Player player) {
        Map<JewelEffect, EffectDisplay> newEffects = new HashMap<>();

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.isEmpty()) {
            List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(heldItem);

            for (ToolJewelData.AttachedJewel jewel : jewels) {
                for (Map.Entry<JewelEffect, Integer> effectEntry : jewel.getEffects().entrySet()) {
                    JewelEffect effect = effectEntry.getKey();
                    int level = effectEntry.getValue();

                    // Check if effect was already active
                    EffectDisplay existing = activeEffects.get(effect);
                    if (existing != null) {
                        existing.level = level;
                        existing.maxDurability = heldItem.getMaxDamage();
                        existing.currentDurability = heldItem.getMaxDamage() - heldItem.getDamageValue();
                        newEffects.put(effect, existing);
                    } else {
                        // New effect, fade in
                        EffectDisplay newDisplay = new EffectDisplay();
                        newDisplay.level = level;
                        newDisplay.fadeIn = 0;
                        newDisplay.maxDurability = heldItem.getMaxDamage();
                        newDisplay.currentDurability = heldItem.getMaxDamage() - heldItem.getDamageValue();
                        newEffects.put(effect, newDisplay);
                    }
                }
            }
        }

        // Update fade animations
        for (Map.Entry<JewelEffect, EffectDisplay> entry : activeEffects.entrySet()) {
            if (!newEffects.containsKey(entry.getKey())) {
                // Effect is no longer active, start fade out
                entry.getValue().fadeOut = FADE_TIME;
                if (entry.getValue().fadeOut > 0) {
                    newEffects.put(entry.getKey(), entry.getValue());
                }
            }
        }

        activeEffects.clear();
        activeEffects.putAll(newEffects);
    }

    private void renderPanel(GuiGraphics graphics, int x, int y, int width, int height, float partialTick) {
        // Semi-transparent background
        graphics.fill(x, y, x + width, y + height, 0x88000000);

        // Border
        graphics.fill(x, y, x + width, y + 1, 0xFFFFFFFF); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF); // Bottom
        graphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF); // Right
    }

    private void renderEffect(GuiGraphics graphics, int x, int y, JewelEffect effect, EffectDisplay display, float partialTick) {
        // Calculate alpha based on fade animation
        float alpha = 1.0f;
        if (display.fadeIn < FADE_TIME) {
            alpha = display.fadeIn / (float)FADE_TIME;
            display.fadeIn++;
        } else if (display.fadeOut > 0) {
            alpha = display.fadeOut / (float)FADE_TIME;
            display.fadeOut--;
        }

        int alphaInt = (int)(alpha * 255);
        int color = (alphaInt << 24) | 0xFFFFFF;

        // Render effect icon (placeholder - would need actual icons)
        renderEffectIcon(graphics, x, y, effect, color);

        // Render effect name and level
        String text = effect.getDisplayName() + " " + toRoman(display.level);
        graphics.drawString(Minecraft.getInstance().font, text, x + ICON_SIZE + 4, y + 4, color);

        // Render durability bar if applicable
        if (display.maxDurability > 0) {
            int barX = x + ICON_SIZE + 4;
            int barY = y + 12;
            int barWidth = 60;
            int barHeight = 2;

            float durabilityPercent = (float)display.currentDurability / display.maxDurability;
            int barColor = getDurabilityColor(durabilityPercent);

            // Background
            graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x88000000);
            // Durability bar
            graphics.fill(barX, barY, barX + (int)(barWidth * durabilityPercent), barY + barHeight, barColor | (alphaInt << 24));
        }

        // Pulse effect for special states
        if (effect == JewelEffect.EXECUTE && display.level > 0) {
            // Pulse red when execute is ready
            float pulse = (float)Math.sin(animationTick * 0.2f) * 0.5f + 0.5f;
            int pulseColor = (int)(pulse * 255) << 16 | alphaInt << 24;
            graphics.fill(x - 1, y - 1, x + ICON_SIZE + 1, y + ICON_SIZE + 1, pulseColor);
        }
    }

    private void renderEffectIcon(GuiGraphics graphics, int x, int y, JewelEffect effect, int color) {
        // For now, render colored square based on effect category
        int categoryColor = getCategoryColor(effect.getCategory());
        graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, categoryColor | (color & 0xFF000000));

        // Draw border
        graphics.fill(x, y, x + ICON_SIZE, y + 1, color); // Top
        graphics.fill(x, y + ICON_SIZE - 1, x + ICON_SIZE, y + ICON_SIZE, color); // Bottom
        graphics.fill(x, y, x + 1, y + ICON_SIZE, color); // Left
        graphics.fill(x + ICON_SIZE - 1, y, x + ICON_SIZE, y + ICON_SIZE, color); // Right
    }

    private int getCategoryColor(JewelEffect.EffectCategory category) {
        switch (category) {
            case TOOL:
                return 0xFFD700; // Gold
            case WEAPON:
                return 0xFF6B6B; // Red
            case UNIVERSAL:
                return 0x5DADE2; // Blue
            case PLAYER:
                return 0x50C878; // Green
            case SPECIAL:
                return 0x9370DB; // Purple
            default:
                return 0xFFFFFF; // White
        }
    }

    private int getDurabilityColor(float percent) {
        if (percent > 0.5f) {
            return 0x00FF00; // Green
        } else if (percent > 0.25f) {
            return 0xFFFF00; // Yellow
        } else {
            return 0xFF0000; // Red
        }
    }

    private String toRoman(int number) {
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(number);
        }
    }

    private int calculatePanelWidth() {
        int maxWidth = 100;
        for (Map.Entry<JewelEffect, EffectDisplay> entry : activeEffects.entrySet()) {
            String text = entry.getKey().getDisplayName() + " " + toRoman(entry.getValue().level);
            int textWidth = Minecraft.getInstance().font.width(text);
            maxWidth = Math.max(maxWidth, textWidth + ICON_SIZE + 12);
        }
        return maxWidth;
    }

    private int calculatePanelHeight() {
        return (activeEffects.size() * (ICON_SIZE + 2)) + (HUD_PADDING * 2) - 2;
    }

    private static class EffectDisplay {
        int level;
        int fadeIn;
        int fadeOut;
        int maxDurability;
        int currentDurability;
    }
}
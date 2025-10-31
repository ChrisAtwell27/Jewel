package com.jewelcharms.client.screen;

import com.jewelcharms.client.gui.JewelPreviewTooltip;
import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.util.JewelCreationHelper;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.JewelRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to render jewel preview panel in creation station
 */
@OnlyIn(Dist.CLIENT)
public class JewelPreviewPanel {

    public static void renderPreview(AbstractContainerScreen<JewelCreationStationMenu> screen, GuiGraphics graphics,
                                     int mouseX, int mouseY) {
        JewelCreationStationMenu menu = screen.getMenu();
        List<ItemStack> materials = menu.getMaterialStacks();

        // Only show preview if materials are present
        if (materials.isEmpty() || materials.stream().allMatch(ItemStack::isEmpty)) {
            return;
        }

        // Calculate preview jewel
        ItemStack previewJewel = JewelCreationHelper.createJewel(materials);
        if (previewJewel.isEmpty()) {
            return;
        }

        JewelData jewelData = JewelData.fromItemStack(previewJewel);
        if (jewelData == null) {
            return;
        }

        // Position preview panel in the bottom left corner
        int panelWidth = 150;
        int panelHeight = calculatePanelHeight(jewelData, materials.size());
        int panelX = 10; // Left margin from screen edge
        int panelY = screen.height - panelHeight - 10; // Bottom margin from screen edge

        // Draw panel background
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xDD000000);
        // Border
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFFAAAAAA);
        graphics.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFFAAAAAA);
        graphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFFAAAAAA);
        graphics.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0xFFAAAAAA);

        int currentY = panelY + 4;
        int textX = panelX + 4;
        Font font = Minecraft.getInstance().font;

        // Title
        Component title = Component.literal("Preview").withStyle(ChatFormatting.BOLD);
        graphics.drawString(font, title, textX + (panelWidth - 8 - font.width(title)) / 2,
                            currentY, 0xFFFFFF);
        currentY += 12;

        // Rarity
        Component rarityText = getRarityText(jewelData.getRarity());
        graphics.drawString(font, rarityText, textX, currentY, getRarityColor(jewelData.getRarity()));
        currentY += 10;

        // Separator
        graphics.fill(panelX + 4, currentY, panelX + panelWidth - 4, currentY + 1, 0xFF555555);
        currentY += 3;

        // Effects header
        Component effectsHeader = Component.literal("Effects:").withStyle(ChatFormatting.GOLD);
        graphics.drawString(font, effectsHeader, textX, currentY, 0xFFD700);
        currentY += 10;

        // Group and render effects by category
        Map<JewelEffect.EffectCategory, List<Map.Entry<JewelEffect, Integer>>> categorized = new HashMap<>();
        for (Map.Entry<JewelEffect, Integer> entry : jewelData.getEffects().entrySet()) {
            categorized.computeIfAbsent(entry.getKey().getCategory(), k -> new ArrayList<>()).add(entry);
        }

        for (JewelEffect.EffectCategory category : JewelEffect.EffectCategory.values()) {
            List<Map.Entry<JewelEffect, Integer>> effects = categorized.get(category);
            if (effects != null && !effects.isEmpty()) {
                // Category name
                Component categoryName = getCategoryName(category);
                graphics.drawString(font, categoryName, textX + 2, currentY, getCategoryColor(category));
                currentY += 9;

                // Effects
                for (Map.Entry<JewelEffect, Integer> effect : effects) {
                    String effectText = "  " + effect.getKey().getDisplayName() + " " + toRoman(effect.getValue());
                    graphics.drawString(font, effectText, textX + 4, currentY, 0xDDDDDD);
                    currentY += 8;
                }
            }
        }

        // Separator
        currentY += 2;
        graphics.fill(panelX + 4, currentY, panelX + panelWidth - 4, currentY + 1, 0xFF555555);
        currentY += 3;

        // Puzzle info
        Component puzzleText = Component.literal("Puzzle: ")
            .append(Component.literal(jewelData.getRarity().getPuzzleGridSize() + "x" +
                                     jewelData.getRarity().getPuzzleGridSize())
                .withStyle(ChatFormatting.WHITE));
        graphics.drawString(font, puzzleText, textX, currentY, 0xAAAAAA);
        currentY += 10;

        // Skip cost
        Component skipText = Component.literal("Skip Cost: ")
            .append(Component.literal(jewelData.getRarity().getSkipCost() + " XP")
                .withStyle(ChatFormatting.YELLOW));
        graphics.drawString(font, skipText, textX, currentY, 0xAAAAAA);
        currentY += 10;

        // Color preview
        graphics.fill(panelX + 4, currentY, panelX + panelWidth - 4, currentY + 1, 0xFF555555);
        currentY += 3;

        Component colorText = Component.literal("Color: ");
        graphics.drawString(font, colorText, textX, currentY, 0xAAAAAA);

        // Draw color swatches
        List<Integer> colors = jewelData.getIndividualColors();
        int swatchX = textX + font.width(colorText) + 2;
        for (int i = 0; i < Math.min(colors.size(), 5); i++) { // Limit to 5 colors max
            int color = colors.get(i);
            graphics.fill(swatchX, currentY, swatchX + 8, currentY + 8, 0xFF000000);
            graphics.fill(swatchX + 1, currentY + 1, swatchX + 7, currentY + 7, 0xFF000000 | color);
            swatchX += 10;
        }
    }

    private static int calculatePanelHeight(JewelData jewelData, int materialCount) {
        int height = 30; // Title + rarity
        height += 15; // Effects header
        height += jewelData.getEffects().size() * 8; // Each effect
        height += getUniqueCategoryCount(jewelData) * 9; // Category headers
        height += 30; // Puzzle info + skip cost
        height += 15; // Color preview
        height += 10; // Padding
        return height;
    }

    private static int getUniqueCategoryCount(JewelData jewelData) {
        return (int) jewelData.getEffects().keySet().stream()
            .map(JewelEffect::getCategory)
            .distinct()
            .count();
    }

    private static Component getRarityText(JewelRarity rarity) {
        return Component.literal(rarity.getDisplayName()).withStyle(getRarityFormatting(rarity));
    }

    private static ChatFormatting getRarityFormatting(JewelRarity rarity) {
        return switch (rarity) {
            case LEGENDARY -> ChatFormatting.GOLD;
            case EPIC -> ChatFormatting.LIGHT_PURPLE;
            case RARE -> ChatFormatting.AQUA;
            case UNCOMMON -> ChatFormatting.GREEN;
            default -> ChatFormatting.WHITE;
        };
    }

    private static int getRarityColor(JewelRarity rarity) {
        return switch (rarity) {
            case LEGENDARY -> 0xFFD700;
            case EPIC -> 0xFF55FF;
            case RARE -> 0x55FFFF;
            case UNCOMMON -> 0x55FF55;
            default -> 0xFFFFFF;
        };
    }

    private static Component getCategoryName(JewelEffect.EffectCategory category) {
        String name = switch (category) {
            case TOOL -> "Tool:";
            case WEAPON -> "Weapon:";
            case UNIVERSAL -> "Universal:";
            case PLAYER -> "Player:";
            case SPECIAL -> "Special:";
        };
        return Component.literal(name).withStyle(ChatFormatting.ITALIC);
    }

    private static int getCategoryColor(JewelEffect.EffectCategory category) {
        return switch (category) {
            case TOOL -> 0xFFAA00;
            case WEAPON -> 0xFF5555;
            case UNIVERSAL -> 0x5555FF;
            case PLAYER -> 0x55FF55;
            case SPECIAL -> 0xFF55FF;
        };
    }

    private static String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }
}
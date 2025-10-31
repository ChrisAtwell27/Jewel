package com.jewelcharms.client.gui;

import com.jewelcharms.config.MaterialEffectConfig;
import com.jewelcharms.config.MaterialMapping;
import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.util.JewelCreationHelper;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.JewelRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

/**
 * Renders a preview tooltip showing what jewel would be created from materials
 */
@OnlyIn(Dist.CLIENT)
public class JewelPreviewTooltip implements ClientTooltipComponent {

    private final List<ItemStack> materials;
    private final JewelData previewData;
    private final int width;
    private final int height;

    public JewelPreviewTooltip(List<ItemStack> materials) {
        this.materials = materials;

        // Calculate preview jewel data
        if (!materials.isEmpty()) {
            ItemStack previewJewel = JewelCreationHelper.createJewel(materials);
            this.previewData = previewJewel.isEmpty() ? null : JewelData.fromItemStack(previewJewel);
        } else {
            this.previewData = null;
        }

        // Calculate dimensions
        this.width = 200;
        this.height = calculateHeight();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        if (previewData == null) {
            renderNoPreview(graphics, font, x, y);
            return;
        }

        int currentY = y;

        // Title
        Component title = Component.literal("Jewel Preview").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
        graphics.drawString(font, title, x + (width - font.width(title)) / 2, currentY, 0xFFFFFF);
        currentY += 12;

        // Rarity
        Component rarityText = getRarityComponent(previewData.getRarity());
        graphics.drawString(font, rarityText, x + 2, currentY, getRarityColor(previewData.getRarity()));
        currentY += 10;

        // Draw separator
        graphics.fill(x, currentY, x + width, currentY + 1, 0xFF555555);
        currentY += 3;

        // Materials section
        Component materialsTitle = Component.literal("Materials:").withStyle(ChatFormatting.GRAY);
        graphics.drawString(font, materialsTitle, x + 2, currentY, 0xAAAAAA);
        currentY += 10;

        for (ItemStack material : materials) {
            String materialName = material.getHoverName().getString();
            Component materialText = Component.literal("  • " + materialName).withStyle(ChatFormatting.WHITE);
            graphics.drawString(font, materialText, x + 2, currentY, 0xFFFFFF);
            currentY += 9;
        }

        // Draw separator
        currentY += 2;
        graphics.fill(x, currentY, x + width, currentY + 1, 0xFF555555);
        currentY += 3;

        // Effects section
        Component effectsTitle = Component.literal("Resulting Effects:").withStyle(ChatFormatting.GOLD);
        graphics.drawString(font, effectsTitle, x + 2, currentY, 0xFFD700);
        currentY += 10;

        // Group effects by category
        Map<JewelEffect.EffectCategory, List<Map.Entry<JewelEffect, Integer>>> categorizedEffects = new HashMap<>();
        for (Map.Entry<JewelEffect, Integer> entry : previewData.getEffects().entrySet()) {
            categorizedEffects.computeIfAbsent(entry.getKey().getCategory(), k -> new ArrayList<>()).add(entry);
        }

        // Render effects by category
        for (JewelEffect.EffectCategory category : JewelEffect.EffectCategory.values()) {
            List<Map.Entry<JewelEffect, Integer>> effects = categorizedEffects.get(category);
            if (effects != null && !effects.isEmpty()) {
                // Category header
                Component categoryHeader = getCategoryComponent(category);
                graphics.drawString(font, categoryHeader, x + 4, currentY, getCategoryColor(category));
                currentY += 9;

                // Effects in category
                for (Map.Entry<JewelEffect, Integer> effect : effects) {
                    String effectText = "    " + effect.getKey().getDisplayName() + " " + toRoman(effect.getValue());
                    Component effectComponent = Component.literal(effectText).withStyle(ChatFormatting.WHITE);
                    graphics.drawString(font, effectComponent, x + 4, currentY, 0xFFFFFF);
                    currentY += 9;
                }
            }
        }

        // Color preview
        currentY += 2;
        graphics.fill(x, currentY, x + width, currentY + 1, 0xFF555555);
        currentY += 3;

        Component colorTitle = Component.literal("Jewel Color:").withStyle(ChatFormatting.GRAY);
        graphics.drawString(font, colorTitle, x + 2, currentY, 0xAAAAAA);

        // Draw color swatches
        List<Integer> colors = previewData.getIndividualColors();
        int swatchX = x + font.width(colorTitle) + 5;
        for (int color : colors) {
            graphics.fill(swatchX, currentY, swatchX + 8, currentY + 8, 0xFF000000 | color);
            graphics.fill(swatchX + 1, currentY + 1, swatchX + 7, currentY + 7, 0xFF000000 | color);
            swatchX += 10;
        }
        currentY += 10;

        // Puzzle difficulty
        currentY += 2;
        graphics.fill(x, currentY, x + width, currentY + 1, 0xFF555555);
        currentY += 3;

        Component puzzleInfo = Component.literal("Puzzle: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(previewData.getRarity().getPuzzleGridSize() + "x" + previewData.getRarity().getPuzzleGridSize())
                .withStyle(ChatFormatting.WHITE));
        graphics.drawString(font, puzzleInfo, x + 2, currentY, 0xAAAAAA);

        // Skip cost
        Component skipCost = Component.literal(" (Skip: " + previewData.getRarity().getSkipCost() + " XP)")
            .withStyle(ChatFormatting.YELLOW);
        graphics.drawString(font, skipCost, x + font.width(puzzleInfo) + 2, currentY, 0xFFFF00);
    }

    private void renderNoPreview(GuiGraphics graphics, Font font, int x, int y) {
        Component text = Component.literal("Add materials to preview jewel")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        graphics.drawString(font, text, x + (width - font.width(text)) / 2, y + height / 2 - 4, 0x808080);
    }

    private Component getRarityComponent(JewelRarity rarity) {
        return Component.literal("Rarity: " + rarity.getDisplayName())
            .withStyle(getRarityChatFormatting(rarity));
    }

    private ChatFormatting getRarityChatFormatting(JewelRarity rarity) {
        switch (rarity) {
            case LEGENDARY: return ChatFormatting.GOLD;
            case EPIC: return ChatFormatting.LIGHT_PURPLE;
            case RARE: return ChatFormatting.AQUA;
            case UNCOMMON: return ChatFormatting.GREEN;
            default: return ChatFormatting.WHITE;
        }
    }

    private int getRarityColor(JewelRarity rarity) {
        switch (rarity) {
            case LEGENDARY: return 0xFFD700;
            case EPIC: return 0xFF55FF;
            case RARE: return 0x55FFFF;
            case UNCOMMON: return 0x55FF55;
            default: return 0xFFFFFF;
        }
    }

    private Component getCategoryComponent(JewelEffect.EffectCategory category) {
        String name = switch (category) {
            case TOOL -> "Tool Effects:";
            case WEAPON -> "Weapon Effects:";
            case UNIVERSAL -> "Universal Effects:";
            case PLAYER -> "Player Effects:";
            case SPECIAL -> "Special Effects:";
        };
        return Component.literal(name).withStyle(ChatFormatting.BOLD);
    }

    private int getCategoryColor(JewelEffect.EffectCategory category) {
        return switch (category) {
            case TOOL -> 0xFFD700;      // Gold
            case WEAPON -> 0xFF6B6B;     // Red
            case UNIVERSAL -> 0x5DADE2;  // Blue
            case PLAYER -> 0x50C878;     // Green
            case SPECIAL -> 0x9370DB;    // Purple
        };
    }

    private String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }

    private int calculateHeight() {
        if (previewData == null) {
            return 20;
        }

        int baseHeight = 40; // Title + rarity
        baseHeight += materials.size() * 9 + 15; // Materials
        baseHeight += previewData.getEffects().size() * 9 + 30; // Effects with categories
        baseHeight += 30; // Color preview
        baseHeight += 20; // Puzzle info

        return baseHeight;
    }

    /**
     * Create a preview tooltip for hovered material items
     */
    public static List<Component> getMaterialTooltip(ItemStack material) {
        List<Component> tooltip = new ArrayList<>();

        MaterialEffectConfig config = MaterialEffectConfig.getInstance();
        MaterialMapping mapping = config != null ? config.getMapping(material.getItem()) : null;

        if (mapping != null) {
            tooltip.add(Component.literal("Jewel Material").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            tooltip.add(Component.empty());

            tooltip.add(Component.literal("Grants Effects:").withStyle(ChatFormatting.GRAY));
            for (Map.Entry<String, Integer> effect : mapping.getEffects().entrySet()) {
                JewelEffect jewelEffect = JewelEffect.fromId(effect.getKey());
                if (jewelEffect != null) {
                    Component effectLine = Component.literal("  ")
                        .append(Component.literal(jewelEffect.getDisplayName())
                            .withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(" " + toRomanStatic(effect.getValue()))
                            .withStyle(ChatFormatting.YELLOW));
                    tooltip.add(effectLine);
                }
            }

            if (mapping.getColor() != 0) {
                tooltip.add(Component.empty());
                tooltip.add(Component.literal("Color: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("■")
                        .withStyle(style -> style.withColor(mapping.getColor()))));
            }
        }

        return tooltip;
    }

    private static String toRomanStatic(int number) {
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
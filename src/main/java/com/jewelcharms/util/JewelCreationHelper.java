package com.jewelcharms.util;

import com.jewelcharms.config.MaterialEffectConfig;
import com.jewelcharms.config.MaterialMapping;
import com.jewelcharms.config.RarityConfig;
import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class JewelCreationHelper {
    private static MaterialEffectConfig config;
    private static RarityConfig rarityConfig;

    public static void setConfig(MaterialEffectConfig config) {
        JewelCreationHelper.config = config;
    }

    public static void setRarityConfig(RarityConfig rarityConfig) {
        JewelCreationHelper.rarityConfig = rarityConfig;
    }

    public static ItemStack createJewel(List<ItemStack> materialStacks) {
        if (materialStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        List<String> materialNames = new ArrayList<>();
        Map<JewelEffect, Integer> combinedEffects = new HashMap<>();
        List<Integer> colors = new ArrayList<>();

        for (ItemStack stack : materialStacks) {
            Item item = stack.getItem();
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);

            if (itemId != null) {
                materialNames.add(itemId.getPath());
                MaterialMapping mapping = config.getMapping(itemId.toString());

                if (mapping != null) {
                    // Add effects
                    Map<JewelEffect, Integer> effects = mapping.getEffectsAsEnum();
                    for (Map.Entry<JewelEffect, Integer> entry : effects.entrySet()) {
                        combinedEffects.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }

                    // Collect color
                    colors.add(mapping.getColor());
                }
            }
        }

        if (combinedEffects.isEmpty()) {
            return ItemStack.EMPTY; // No valid materials
        }

        // Calculate rarity based on materials
        JewelRarity rarity = calculateRarity(materialStacks);

        // Create jewel item with individual colors (no averaging)
        ItemStack jewel = new ItemStack(ModItems.JEWEL.get());
        JewelData jewelData = new JewelData(materialNames, combinedEffects, colors, rarity);
        jewelData.saveToItemStack(jewel);

        return jewel;
    }

    /**
     * Calculates the rarity of a jewel based on the materials used.
     * Sums up the rarity values of all materials and determines the tier.
     */
    public static JewelRarity calculateRarity(List<ItemStack> materialStacks) {
        if (rarityConfig == null) {
            return JewelRarity.COMMON; // Fallback if config not loaded
        }

        int totalRarityScore = 0;
        for (ItemStack stack : materialStacks) {
            Item item = stack.getItem();
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);

            if (itemId != null) {
                int rarityValue = rarityConfig.getMaterialRarityValue(itemId.toString());
                totalRarityScore += rarityValue;
            }
        }

        return JewelRarity.fromScore(totalRarityScore);
    }

    public static boolean isValidMaterial(Item item) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        return itemId != null && config.getMapping(itemId.toString()) != null;
    }
}

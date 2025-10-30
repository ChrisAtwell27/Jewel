package com.jewelcharms.util;

import com.jewelcharms.config.MaterialEffectConfig;
import com.jewelcharms.config.MaterialMapping;
import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class JewelCreationHelper {
    private static MaterialEffectConfig config;

    public static void setConfig(MaterialEffectConfig config) {
        JewelCreationHelper.config = config;
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

        // Create jewel item with individual colors (no averaging)
        ItemStack jewel = new ItemStack(ModItems.JEWEL.get());
        JewelData jewelData = new JewelData(materialNames, combinedEffects, colors);
        jewelData.saveToItemStack(jewel);

        return jewel;
    }

    public static boolean isValidMaterial(Item item) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        return itemId != null && config.getMapping(itemId.toString()) != null;
    }
}

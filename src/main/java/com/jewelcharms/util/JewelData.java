package com.jewelcharms.util;

import com.jewelcharms.effect.JewelEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class JewelData {
    private static final String JEWEL_DATA_TAG = "JewelData";
    private static final String MATERIALS_TAG = "Materials";
    private static final String EFFECTS_TAG = "Effects";
    private static final String COLOR_TAG = "Color";
    private static final String COLORS_TAG = "Colors";
    private static final String RARITY_TAG = "Rarity";

    private final List<String> materials;
    private final Map<JewelEffect, Integer> effects;
    private final int color; // Keep for backwards compatibility
    private final List<Integer> individualColors;
    private final JewelRarity rarity;

    public JewelData(List<String> materials, Map<JewelEffect, Integer> effects, int color) {
        this(materials, effects, color, JewelRarity.COMMON);
    }

    public JewelData(List<String> materials, Map<JewelEffect, Integer> effects, int color, JewelRarity rarity) {
        this.materials = new ArrayList<>(materials);
        this.effects = new HashMap<>(effects);
        this.color = color;
        this.individualColors = Collections.singletonList(color);
        this.rarity = rarity;
    }

    public JewelData(List<String> materials, Map<JewelEffect, Integer> effects, List<Integer> colors) {
        this(materials, effects, colors, JewelRarity.COMMON);
    }

    public JewelData(List<String> materials, Map<JewelEffect, Integer> effects, List<Integer> colors, JewelRarity rarity) {
        this.materials = new ArrayList<>(materials);
        this.effects = new HashMap<>(effects);
        this.individualColors = new ArrayList<>(colors);
        // Calculate averaged color for backwards compatibility
        this.color = averageColors(colors);
        this.rarity = rarity;
    }

    private static int averageColors(List<Integer> colors) {
        if (colors.isEmpty()) {
            return 0xFFFFFF;
        }
        int totalR = 0, totalG = 0, totalB = 0;
        for (int color : colors) {
            totalR += (color >> 16) & 0xFF;
            totalG += (color >> 8) & 0xFF;
            totalB += color & 0xFF;
        }
        int count = colors.size();
        return ((totalR / count) << 16) | ((totalG / count) << 8) | (totalB / count);
    }

    public List<String> getMaterials() {
        return new ArrayList<>(materials);
    }

    public Map<JewelEffect, Integer> getEffects() {
        return new HashMap<>(effects);
    }

    public int getColor() {
        return color;
    }

    public List<Integer> getIndividualColors() {
        return new ArrayList<>(individualColors);
    }

    public int getColorForLayer(int layerIndex) {
        if (layerIndex >= 0 && layerIndex < individualColors.size()) {
            return individualColors.get(layerIndex);
        }
        return 0xFFFFFF; // Default white
    }

    public JewelRarity getRarity() {
        return rarity;
    }

    public String getMaterialsString() {
        return materials.stream().collect(Collectors.joining(", "));
    }

    public void saveToItemStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag jewelTag = new CompoundTag();

        // Save materials
        ListTag materialsList = new ListTag();
        for (String material : materials) {
            materialsList.add(StringTag.valueOf(material));
        }
        jewelTag.put(MATERIALS_TAG, materialsList);

        // Save effects
        CompoundTag effectsTag = new CompoundTag();
        for (Map.Entry<JewelEffect, Integer> entry : effects.entrySet()) {
            effectsTag.putInt(entry.getKey().getId(), entry.getValue());
        }
        jewelTag.put(EFFECTS_TAG, effectsTag);

        // Save color (backwards compatibility)
        jewelTag.putInt(COLOR_TAG, color);

        // Save individual colors
        int[] colorsArray = individualColors.stream().mapToInt(Integer::intValue).toArray();
        jewelTag.putIntArray(COLORS_TAG, colorsArray);

        // Save rarity
        jewelTag.putString(RARITY_TAG, rarity.getId());

        tag.put(JEWEL_DATA_TAG, jewelTag);
    }

    public static JewelData fromItemStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(JEWEL_DATA_TAG)) {
            return null;
        }

        CompoundTag jewelTag = tag.getCompound(JEWEL_DATA_TAG);

        // Load materials
        List<String> materials = new ArrayList<>();
        ListTag materialsList = jewelTag.getList(MATERIALS_TAG, 8); // 8 = String type
        for (int i = 0; i < materialsList.size(); i++) {
            materials.add(materialsList.getString(i));
        }

        // Load effects
        Map<JewelEffect, Integer> effects = new HashMap<>();
        CompoundTag effectsTag = jewelTag.getCompound(EFFECTS_TAG);
        for (String key : effectsTag.getAllKeys()) {
            JewelEffect effect = JewelEffect.fromId(key);
            if (effect != null) {
                effects.put(effect, effectsTag.getInt(key));
            }
        }

        // Load rarity (default to COMMON for backwards compatibility)
        JewelRarity rarity = JewelRarity.COMMON;
        if (jewelTag.contains(RARITY_TAG)) {
            rarity = JewelRarity.fromId(jewelTag.getString(RARITY_TAG));
        }

        // Load individual colors if available
        if (jewelTag.contains(COLORS_TAG)) {
            int[] colorsArray = jewelTag.getIntArray(COLORS_TAG);
            List<Integer> colors = new ArrayList<>();
            for (int color : colorsArray) {
                colors.add(color);
            }
            return new JewelData(materials, effects, colors, rarity);
        }

        // Fallback to single color for backwards compatibility
        int color = jewelTag.getInt(COLOR_TAG);
        return new JewelData(materials, effects, color, rarity);
    }

    /**
     * Serialize JewelData to a string for network transmission
     */
    public String serialize() {
        CompoundTag tag = new CompoundTag();

        // Save materials
        ListTag materialsList = new ListTag();
        for (String material : materials) {
            materialsList.add(StringTag.valueOf(material));
        }
        tag.put(MATERIALS_TAG, materialsList);

        // Save effects
        CompoundTag effectsTag = new CompoundTag();
        for (Map.Entry<JewelEffect, Integer> entry : effects.entrySet()) {
            effectsTag.putInt(entry.getKey().getId(), entry.getValue());
        }
        tag.put(EFFECTS_TAG, effectsTag);

        // Save color
        tag.putInt(COLOR_TAG, color);

        // Save individual colors
        int[] colorsArray = individualColors.stream().mapToInt(Integer::intValue).toArray();
        tag.putIntArray(COLORS_TAG, colorsArray);

        // Save rarity
        tag.putString(RARITY_TAG, rarity.getId());

        return tag.toString();
    }

    /**
     * Deserialize JewelData from a string
     */
    public static JewelData deserialize(String data) {
        try {
            // Parse the NBT string
            CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(data);

            // Load materials
            List<String> materials = new ArrayList<>();
            ListTag materialsList = tag.getList(MATERIALS_TAG, 8); // 8 = String type
            for (int i = 0; i < materialsList.size(); i++) {
                materials.add(materialsList.getString(i));
            }

            // Load effects
            Map<JewelEffect, Integer> effects = new HashMap<>();
            CompoundTag effectsTag = tag.getCompound(EFFECTS_TAG);
            for (String key : effectsTag.getAllKeys()) {
                JewelEffect effect = JewelEffect.fromId(key);
                if (effect != null) {
                    effects.put(effect, effectsTag.getInt(key));
                }
            }

            // Load rarity
            JewelRarity rarity = JewelRarity.COMMON;
            if (tag.contains(RARITY_TAG)) {
                rarity = JewelRarity.fromId(tag.getString(RARITY_TAG));
            }

            // Load individual colors if available
            if (tag.contains(COLORS_TAG)) {
                int[] colorsArray = tag.getIntArray(COLORS_TAG);
                List<Integer> colors = new ArrayList<>();
                for (int color : colorsArray) {
                    colors.add(color);
                }
                return new JewelData(materials, effects, colors, rarity);
            }

            // Fallback to single color
            int color = tag.getInt(COLOR_TAG);
            return new JewelData(materials, effects, color, rarity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

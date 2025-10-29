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

    private final List<String> materials;
    private final Map<JewelEffect, Integer> effects;
    private final int color;

    public JewelData(List<String> materials, Map<JewelEffect, Integer> effects, int color) {
        this.materials = new ArrayList<>(materials);
        this.effects = new HashMap<>(effects);
        this.color = color;
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

        // Save color
        jewelTag.putInt(COLOR_TAG, color);

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

        // Load color
        int color = jewelTag.getInt(COLOR_TAG);

        return new JewelData(materials, effects, color);
    }
}

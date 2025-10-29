package com.jewelcharms.util;

import com.jewelcharms.effect.JewelEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolJewelData {
    private static final String TOOL_JEWELS_TAG = "JewelCharms";
    private static final String JEWELS_LIST_TAG = "Jewels";
    private static final int MAX_JEWELS = 2;

    public static class AttachedJewel {
        private final List<String> materials;
        private final Map<JewelEffect, Integer> effects;
        private final int color;

        public AttachedJewel(List<String> materials, Map<JewelEffect, Integer> effects, int color) {
            this.materials = materials;
            this.effects = effects;
            this.color = color;
        }

        public List<String> getMaterials() {
            return materials;
        }

        public Map<JewelEffect, Integer> getEffects() {
            return effects;
        }

        public int getColor() {
            return color;
        }
    }

    public static boolean canAttachJewel(ItemStack tool) {
        return getJewelCount(tool) < MAX_JEWELS;
    }

    public static int getJewelCount(ItemStack tool) {
        CompoundTag tag = tool.getTag();
        if (tag == null || !tag.contains(TOOL_JEWELS_TAG)) {
            return 0;
        }

        CompoundTag jewelTag = tag.getCompound(TOOL_JEWELS_TAG);
        ListTag jewelsList = jewelTag.getList(JEWELS_LIST_TAG, 10); // 10 = Compound type
        return jewelsList.size();
    }

    public static void attachJewel(ItemStack tool, JewelData jewelData) {
        if (!canAttachJewel(tool)) {
            return;
        }

        CompoundTag tag = tool.getOrCreateTag();
        CompoundTag jewelCharmTag = tag.getCompound(TOOL_JEWELS_TAG);

        if (!tag.contains(TOOL_JEWELS_TAG)) {
            jewelCharmTag = new CompoundTag();
        }

        ListTag jewelsList = jewelCharmTag.getList(JEWELS_LIST_TAG, 10);

        // Create jewel entry
        CompoundTag jewelEntry = new CompoundTag();

        // Save materials
        ListTag materialsList = new ListTag();
        for (String material : jewelData.getMaterials()) {
            CompoundTag materialTag = new CompoundTag();
            materialTag.putString("Material", material);
            materialsList.add(materialTag);
        }
        jewelEntry.put("Materials", materialsList);

        // Save effects
        CompoundTag effectsTag = new CompoundTag();
        for (Map.Entry<JewelEffect, Integer> entry : jewelData.getEffects().entrySet()) {
            effectsTag.putInt(entry.getKey().getId(), entry.getValue());
        }
        jewelEntry.put("Effects", effectsTag);

        // Save color
        jewelEntry.putInt("Color", jewelData.getColor());

        jewelsList.add(jewelEntry);
        jewelCharmTag.put(JEWELS_LIST_TAG, jewelsList);
        tag.put(TOOL_JEWELS_TAG, jewelCharmTag);
    }

    public static List<AttachedJewel> getAttachedJewels(ItemStack tool) {
        List<AttachedJewel> jewels = new ArrayList<>();

        CompoundTag tag = tool.getTag();
        if (tag == null || !tag.contains(TOOL_JEWELS_TAG)) {
            return jewels;
        }

        CompoundTag jewelCharmTag = tag.getCompound(TOOL_JEWELS_TAG);
        ListTag jewelsList = jewelCharmTag.getList(JEWELS_LIST_TAG, 10);

        for (int i = 0; i < jewelsList.size(); i++) {
            CompoundTag jewelEntry = jewelsList.getCompound(i);

            // Load materials
            List<String> materials = new ArrayList<>();
            ListTag materialsList = jewelEntry.getList("Materials", 10);
            for (int j = 0; j < materialsList.size(); j++) {
                CompoundTag materialTag = materialsList.getCompound(j);
                materials.add(materialTag.getString("Material"));
            }

            // Load effects
            Map<JewelEffect, Integer> effects = new java.util.HashMap<>();
            CompoundTag effectsTag = jewelEntry.getCompound("Effects");
            for (String key : effectsTag.getAllKeys()) {
                JewelEffect effect = JewelEffect.fromId(key);
                if (effect != null) {
                    effects.put(effect, effectsTag.getInt(key));
                }
            }

            // Load color
            int color = jewelEntry.getInt("Color");

            jewels.add(new AttachedJewel(materials, effects, color));
        }

        return jewels;
    }

    public static void removeAllJewels(ItemStack tool) {
        CompoundTag tag = tool.getTag();
        if (tag != null && tag.contains(TOOL_JEWELS_TAG)) {
            tag.remove(TOOL_JEWELS_TAG);
        }
    }
}

package com.jewelcharms.config;

import com.jewelcharms.effect.JewelEffect;

import java.util.HashMap;
import java.util.Map;

public class MaterialMapping {
    private String materialId;
    private Map<String, Integer> effects;
    private int color;

    public MaterialMapping() {
        this.effects = new HashMap<>();
    }

    public MaterialMapping(String materialId, Map<String, Integer> effects, int color) {
        this.materialId = materialId;
        this.effects = new HashMap<>(effects);
        this.color = color;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public Map<String, Integer> getEffects() {
        return effects;
    }

    public void setEffects(Map<String, Integer> effects) {
        this.effects = effects;
    }

    public Map<JewelEffect, Integer> getEffectsAsEnum() {
        Map<JewelEffect, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : effects.entrySet()) {
            JewelEffect effect = JewelEffect.fromId(entry.getKey());
            if (effect != null) {
                result.put(effect, entry.getValue());
            }
        }
        return result;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

package com.jewelcharms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jewelcharms.JewelCharms;
import com.jewelcharms.util.JewelRarity;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for material rarity values used to calculate jewel rarity tiers.
 */
public class RarityConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "jewelcharms", "rarity_config.json");

    private Map<String, Integer> materialRarityValues = new HashMap<>();

    public static RarityConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    RarityConfig config = GSON.fromJson(reader, RarityConfig.class);
                    JewelCharms.LOGGER.info("Loaded rarity config with {} materials", config.materialRarityValues.size());
                    return config;
                }
            } else {
                JewelCharms.LOGGER.info("Creating default rarity config");
                RarityConfig config = createDefault();
                config.save();
                return config;
            }
        } catch (Exception e) {
            JewelCharms.LOGGER.error("Failed to load rarity config, using defaults", e);
            return createDefault();
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
            JewelCharms.LOGGER.info("Saved rarity config");
        } catch (Exception e) {
            JewelCharms.LOGGER.error("Failed to save rarity config", e);
        }
    }

    /**
     * Gets the rarity value for a material. Returns 1 if not found (default to common).
     */
    public int getMaterialRarityValue(String materialId) {
        return materialRarityValues.getOrDefault(materialId, 1);
    }

    /**
     * Sets the rarity value for a material.
     */
    public void setMaterialRarityValue(String materialId, int rarityValue) {
        materialRarityValues.put(materialId, rarityValue);
    }

    public Map<String, Integer> getAllMaterialRarityValues() {
        return new HashMap<>(materialRarityValues);
    }

    private static RarityConfig createDefault() {
        RarityConfig config = new RarityConfig();

        // ========== COMMON MATERIALS (Rarity Value: 1-2) ==========
        config.setMaterialRarityValue("minecraft:coal", 1);
        config.setMaterialRarityValue("minecraft:iron_ingot", 2);
        config.setMaterialRarityValue("minecraft:redstone", 1);
        config.setMaterialRarityValue("minecraft:wheat_seeds", 1);
        config.setMaterialRarityValue("minecraft:oak_sapling", 1);
        config.setMaterialRarityValue("minecraft:slime_ball", 2);

        // ========== UNCOMMON MATERIALS (Rarity Value: 3) ==========
        config.setMaterialRarityValue("minecraft:diamond", 3);
        config.setMaterialRarityValue("minecraft:emerald", 3);
        config.setMaterialRarityValue("minecraft:gold_ingot", 3);
        config.setMaterialRarityValue("minecraft:lapis_lazuli", 2);
        config.setMaterialRarityValue("minecraft:quartz", 2);
        config.setMaterialRarityValue("minecraft:amethyst_shard", 3);
        config.setMaterialRarityValue("minecraft:prismarine_shard", 3);
        config.setMaterialRarityValue("minecraft:obsidian", 2);
        config.setMaterialRarityValue("minecraft:ender_pearl", 3);
        config.setMaterialRarityValue("minecraft:spider_eye", 2);
        config.setMaterialRarityValue("minecraft:rabbit_foot", 3);

        // ========== RARE MATERIALS (Rarity Value: 4) ==========
        config.setMaterialRarityValue("minecraft:blaze_powder", 4);
        config.setMaterialRarityValue("minecraft:blaze_rod", 4);
        config.setMaterialRarityValue("minecraft:ender_eye", 4);
        config.setMaterialRarityValue("minecraft:ghast_tear", 4);
        config.setMaterialRarityValue("minecraft:magma_cream", 4);
        config.setMaterialRarityValue("minecraft:glowstone_dust", 3);
        config.setMaterialRarityValue("minecraft:golden_apple", 4);
        config.setMaterialRarityValue("minecraft:glistering_melon_slice", 4);
        config.setMaterialRarityValue("minecraft:fermented_spider_eye", 3);
        config.setMaterialRarityValue("minecraft:chorus_fruit", 4);

        // ========== EPIC MATERIALS (Rarity Value: 5) ==========
        config.setMaterialRarityValue("minecraft:netherite_ingot", 5);
        config.setMaterialRarityValue("minecraft:wither_skeleton_skull", 5);
        config.setMaterialRarityValue("minecraft:zombie_head", 4);

        // ========== LEGENDARY MATERIALS (Rarity Value: 7+) ==========
        config.setMaterialRarityValue("minecraft:nether_star", 7);
        config.setMaterialRarityValue("minecraft:totem_of_undying", 7);
        config.setMaterialRarityValue("minecraft:barrier", 7);

        return config;
    }
}

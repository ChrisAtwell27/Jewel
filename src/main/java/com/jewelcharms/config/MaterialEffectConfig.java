package com.jewelcharms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jewelcharms.JewelCharms;
import com.jewelcharms.effect.JewelEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialEffectConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "jewelcharms", "material_effects.json");

    private static MaterialEffectConfig instance;

    private List<MaterialMapping> materials = new ArrayList<>();
    private transient Map<String, MaterialMapping> materialMap = new HashMap<>();

    public static MaterialEffectConfig getInstance() {
        return instance;
    }

    public static MaterialEffectConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    MaterialEffectConfig config = GSON.fromJson(reader, MaterialEffectConfig.class);
                    config.buildMap();
                    JewelCharms.LOGGER.info("Loaded material effects config with {} materials", config.materials.size());
                    instance = config;
                    return config;
                }
            } else {
                JewelCharms.LOGGER.info("Creating default material effects config");
                MaterialEffectConfig config = createDefault();
                config.save();
                instance = config;
                return config;
            }
        } catch (Exception e) {
            JewelCharms.LOGGER.error("Failed to load material effects config, using defaults", e);
            MaterialEffectConfig config = createDefault();
            instance = config;
            return config;
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
            JewelCharms.LOGGER.info("Saved material effects config");
        } catch (IOException e) {
            JewelCharms.LOGGER.error("Failed to save material effects config", e);
        }
    }

    private void buildMap() {
        if (materialMap == null) {
            materialMap = new HashMap<>();
        }
        materialMap.clear();

        if (materials == null) {
            materials = new ArrayList<>();
            JewelCharms.LOGGER.warn("Materials list was null during buildMap, initialized to empty list");
            return;
        }

        for (MaterialMapping mapping : materials) {
            if (mapping != null && mapping.getMaterialId() != null) {
                materialMap.put(mapping.getMaterialId(), mapping);
            } else {
                JewelCharms.LOGGER.warn("Skipped null or invalid material mapping during buildMap");
            }
        }
    }

    public MaterialMapping getMapping(String materialId) {
        if (materialId == null) {
            JewelCharms.LOGGER.warn("Attempted to get mapping for null material ID");
            return null;
        }
        MaterialMapping mapping = materialMap.get(materialId);
        if (mapping == null) {
            JewelCharms.LOGGER.debug("No mapping found for material: {}", materialId);
        }
        return mapping;
    }

    public MaterialMapping getMapping(Item item) {
        if (item == null) {
            JewelCharms.LOGGER.warn("Attempted to get mapping for null item");
            return null;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        if (itemId == null) {
            JewelCharms.LOGGER.warn("Could not find registry key for item: {}", item);
            return null;
        }
        return getMapping(itemId.toString());
    }

    public List<MaterialMapping> getAllMappings() {
        return new ArrayList<>(materials);
    }

    private static MaterialEffectConfig createDefault() {
        MaterialEffectConfig config = new MaterialEffectConfig();

        // ========== TOOL-FOCUSED MATERIALS ==========

        // Diamond - Mining Speed (Tool)
        config.addMaterial("minecraft:diamond", Map.of("mining_speed", 2), 0x5DADE2);

        // Emerald - Fortune (Tool)
        config.addMaterial("minecraft:emerald", Map.of("fortune", 1), 0x50C878);

        // Blaze Powder - Auto-Smelt (Tool)
        config.addMaterial("minecraft:blaze_powder", Map.of("auto_smelt", 1), 0xFFA500);

        // Redstone - Haste (Tool)
        config.addMaterial("minecraft:redstone", Map.of("haste", 1), 0xFF0000);

        // Amethyst - Silk Touch (Tool)
        config.addMaterial("minecraft:amethyst_shard", Map.of("silk_touch", 1), 0x9966CC);

        // Coal - Vein Miner (Tool)
        config.addMaterial("minecraft:coal", Map.of("aoe_mining", 1), 0x2F2F2F);

        // Wheat Seeds - Auto-Replant (Tool - Hoe)
        config.addMaterial("minecraft:wheat_seeds", Map.of("replanting", 1), 0x9ACD32);

        // Oak Sapling - Tree Capitator (Tool - Axe)
        config.addMaterial("minecraft:oak_sapling", Map.of("tree_felling", 1), 0x228B22);

        // Iron - Item Magnet (Tool)
        config.addMaterial("minecraft:iron_ingot", Map.of("magnetic", 2), 0xD8D8D8);

        // ========== WEAPON-FOCUSED MATERIALS ==========

        // Quartz - Sharpness (Weapon)
        config.addMaterial("minecraft:quartz", Map.of("damage", 2), 0xE8E8E8);

        // Lapis - Looting (Weapon)
        config.addMaterial("minecraft:lapis_lazuli", Map.of("looting", 1), 0x1E90FF);

        // Blaze Rod - Fire Aspect (Weapon)
        config.addMaterial("minecraft:blaze_rod", Map.of("fire_aspect", 2), 0xFF4500);

        // Slime Ball - Knockback (Weapon)
        config.addMaterial("minecraft:slime_ball", Map.of("knockback", 2), 0x90EE90);

        // Eye of Ender - Critical Strike (Weapon)
        config.addMaterial("minecraft:ender_eye", Map.of("critical_chance", 2), 0x2E8B57);

        // Glistering Melon - Life Steal (Weapon)
        config.addMaterial("minecraft:glistering_melon_slice", Map.of("lifesteal", 2), 0xFF1493);

        // Wither Skeleton Skull - Execute (Weapon)
        config.addMaterial("minecraft:wither_skeleton_skull", Map.of("execute", 1), 0x1A1A1A);

        // Spider Eye - Poison (Weapon)
        config.addMaterial("minecraft:spider_eye", Map.of("poison", 2), 0x8B008B);

        // Fermented Spider Eye - Weakness (Weapon)
        config.addMaterial("minecraft:fermented_spider_eye", Map.of("weakness", 2), 0x4B0082);

        // Zombie Head - Beheading (Weapon)
        config.addMaterial("minecraft:zombie_head", Map.of("beheading", 1), 0x556B2F);

        // ========== UNIVERSAL MATERIALS ==========

        // Gold - Experience Boost (Universal)
        config.addMaterial("minecraft:gold_ingot", Map.of("experience_boost", 2), 0xFFD700);

        // Ghast Tear - Self-Repair (Universal)
        config.addMaterial("minecraft:ghast_tear", Map.of("self_repair", 2), 0xF0F0F0);

        // Netherite - Durability + Damage + Speed (Universal - Powerful)
        config.addMaterial("minecraft:netherite_ingot", Map.of("durability", 5, "mining_speed", 2, "damage", 2), 0x4A4A4A);

        // Ender Pearl - Reach (Universal)
        config.addMaterial("minecraft:ender_pearl", Map.of("reach", 2), 0x008080);

        // ========== PLAYER EFFECT MATERIALS ==========

        // Glowstone - Night Vision (Player)
        config.addMaterial("minecraft:glowstone_dust", Map.of("night_vision", 1), 0xFFFF99);

        // Prismarine - Water Breathing + Speed (Player)
        config.addMaterial("minecraft:prismarine_shard", Map.of("water_breathing", 1, "speed", 1), 0x7FDBDA);

        // Obsidian - Resistance (Player)
        config.addMaterial("minecraft:obsidian", Map.of("resistance", 2), 0x3B2F4A);

        // Golden Apple - Regeneration (Player)
        config.addMaterial("minecraft:golden_apple", Map.of("regeneration", 2), 0xFFD700);

        // Rabbit Foot - Jump Boost (Player)
        config.addMaterial("minecraft:rabbit_foot", Map.of("jump_boost", 2), 0xDEB887);

        // Magma Cream - Fire Resistance (Player)
        config.addMaterial("minecraft:magma_cream", Map.of("fire_resistance", 1), 0xFF6347);

        // ========== SPECIAL EFFECT MATERIALS ==========

        // Chorus Fruit - Ender Pocket (Special)
        config.addMaterial("minecraft:chorus_fruit", Map.of("teleport_drops", 1), 0x9370DB);

        // Nether Star - Absorption (Special)
        config.addMaterial("minecraft:nether_star", Map.of("absorption", 3), 0xFFFFFF);

        // Barrier - Void Touch (Special - Destroys blocks)
        config.addMaterial("minecraft:barrier", Map.of("void_touch", 1), 0xFF0000);

        // Totem of Undying - Soulbound (Special - Keep on death)
        config.addMaterial("minecraft:totem_of_undying", Map.of("soulbound", 1), 0xFFD700);

        config.buildMap();
        return config;
    }

    private void addMaterial(String materialId, Map<String, Integer> effects, int color) {
        materials.add(new MaterialMapping(materialId, effects, color));
    }
}

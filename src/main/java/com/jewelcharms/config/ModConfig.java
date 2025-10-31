package com.jewelcharms.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static class Common {
        // Game Mechanics
        public final IntValue maxJewelsPerTool;
        public final IntValue materialSlotsInCreationStation;

        // Effect Multipliers - Tool Effects
        public final DoubleValue miningSpeedMultiplierPerLevel;
        public final DoubleValue autoSmeltChance;
        public final IntValue aoeMiningMaxBlocks;
        public final IntValue treeFellingMaxBlocks;
        public final DoubleValue magneticRangePerLevel;

        // Effect Multipliers - Weapon Effects
        public final DoubleValue damagePerLevel;
        public final DoubleValue criticalChancePerLevel;
        public final DoubleValue knockbackPerLevel;
        public final DoubleValue lifestealPercentPerLevel;
        public final DoubleValue executeThreshold;

        // Effect Multipliers - Universal Effects
        public final DoubleValue reachBonusPerLevel;
        public final DoubleValue experienceBoostMultiplierPerLevel;
        public final IntValue selfRepairIntervalTicks;
        public final DoubleValue selfRepairAmountPerLevel;

        // Special Effects
        public final LongValue dominoEffectChainTimeout;
        public final IntValue voidTouchInstantBreakLevel;
        public final DoubleValue quantumHarvestChance;
        public final IntValue soundHarvestingRange;
        public final DoubleValue moonPhaseDamageBonus;
        public final DoubleValue biomeResonanceBonus;

        // GUI Settings
        public final IntValue creationStationGuiWidth;
        public final IntValue creationStationGuiHeight;
        public final IntValue polishStationGuiWidth;
        public final IntValue polishStationGuiHeight;

        Common(Builder builder) {
            builder.comment("Jewel Charms Configuration Settings")
                    .push("general");

            // Game Mechanics
            builder.comment("Game Mechanics Settings").push("mechanics");

            maxJewelsPerTool = builder
                    .comment("Maximum number of jewels that can be attached to a single tool/weapon")
                    .defineInRange("maxJewelsPerTool", 2, 1, 5);

            materialSlotsInCreationStation = builder
                    .comment("Number of material slots in the Jewel Creation Station")
                    .defineInRange("materialSlotsInCreationStation", 3, 1, 5);

            builder.pop();

            // Tool Effects
            builder.comment("Tool Effect Multipliers").push("tool_effects");

            miningSpeedMultiplierPerLevel = builder
                    .comment("Mining speed increase multiplier per jewel level (1.0 = 100% increase)")
                    .defineInRange("miningSpeedMultiplierPerLevel", 0.25, 0.1, 1.0);

            autoSmeltChance = builder
                    .comment("Chance for auto-smelt to activate (1.0 = 100%)")
                    .defineInRange("autoSmeltChance", 1.0, 0.0, 1.0);

            aoeMiningMaxBlocks = builder
                    .comment("Maximum number of blocks that can be mined with AOE mining")
                    .defineInRange("aoeMiningMaxBlocks", 8, 1, 27);

            treeFellingMaxBlocks = builder
                    .comment("Maximum number of logs that can be felled at once")
                    .defineInRange("treeFellingMaxBlocks", 100, 10, 500);

            magneticRangePerLevel = builder
                    .comment("Magnetic pull range per jewel level (in blocks)")
                    .defineInRange("magneticRangePerLevel", 2.0, 1.0, 10.0);

            builder.pop();

            // Weapon Effects
            builder.comment("Weapon Effect Multipliers").push("weapon_effects");

            damagePerLevel = builder
                    .comment("Additional damage per jewel level")
                    .defineInRange("damagePerLevel", 1.25, 0.5, 5.0);

            criticalChancePerLevel = builder
                    .comment("Critical strike chance per jewel level (0.1 = 10%)")
                    .defineInRange("criticalChancePerLevel", 0.1, 0.05, 0.5);

            knockbackPerLevel = builder
                    .comment("Knockback strength per jewel level")
                    .defineInRange("knockbackPerLevel", 0.5, 0.1, 2.0);

            lifestealPercentPerLevel = builder
                    .comment("Lifesteal percentage of damage dealt per jewel level (0.1 = 10%)")
                    .defineInRange("lifestealPercentPerLevel", 0.1, 0.05, 0.5);

            executeThreshold = builder
                    .comment("Health threshold for execute effect (0.2 = 20% health)")
                    .defineInRange("executeThreshold", 0.2, 0.1, 0.5);

            builder.pop();

            // Universal Effects
            builder.comment("Universal Effect Settings").push("universal_effects");

            reachBonusPerLevel = builder
                    .comment("Reach distance bonus per jewel level (in blocks)")
                    .defineInRange("reachBonusPerLevel", 1.0, 0.5, 3.0);

            experienceBoostMultiplierPerLevel = builder
                    .comment("Experience orb value multiplier per jewel level")
                    .defineInRange("experienceBoostMultiplierPerLevel", 0.25, 0.1, 1.0);

            selfRepairIntervalTicks = builder
                    .comment("Ticks between self-repair attempts (20 ticks = 1 second)")
                    .defineInRange("selfRepairIntervalTicks", 100, 20, 600);

            selfRepairAmountPerLevel = builder
                    .comment("Durability repaired per level per interval")
                    .defineInRange("selfRepairAmountPerLevel", 1.0, 0.5, 5.0);

            builder.pop();

            // Special Effects
            builder.comment("Special Effect Settings").push("special_effects");

            dominoEffectChainTimeout = builder
                    .comment("Milliseconds before domino effect chain resets")
                    .defineInRange("dominoEffectChainTimeout", 3000L, 1000L, 10000L);

            voidTouchInstantBreakLevel = builder
                    .comment("Jewel level required for void touch instant break")
                    .defineInRange("voidTouchInstantBreakLevel", 1, 1, 5);

            quantumHarvestChance = builder
                    .comment("Chance for quantum harvest to duplicate items (0.1 = 10%)")
                    .defineInRange("quantumHarvestChance", 0.1, 0.05, 0.5);

            soundHarvestingRange = builder
                    .comment("Range in blocks for sound harvesting effect")
                    .defineInRange("soundHarvestingRange", 10, 5, 30);

            moonPhaseDamageBonus = builder
                    .comment("Maximum damage bonus during full moon")
                    .defineInRange("moonPhaseDamageBonus", 0.5, 0.1, 2.0);

            biomeResonanceBonus = builder
                    .comment("Bonus multiplier when in resonant biome")
                    .defineInRange("biomeResonanceBonus", 0.25, 0.1, 1.0);

            builder.pop();

            // GUI Settings
            builder.comment("GUI Dimension Settings").push("gui");

            creationStationGuiWidth = builder
                    .comment("Width of the Jewel Creation Station GUI")
                    .defineInRange("creationStationGuiWidth", 176, 176, 256);

            creationStationGuiHeight = builder
                    .comment("Height of the Jewel Creation Station GUI")
                    .defineInRange("creationStationGuiHeight", 166, 166, 256);

            polishStationGuiWidth = builder
                    .comment("Width of the Polish Station GUI")
                    .defineInRange("polishStationGuiWidth", 176, 176, 256);

            polishStationGuiHeight = builder
                    .comment("Height of the Polish Station GUI")
                    .defineInRange("polishStationGuiHeight", 166, 166, 256);

            builder.pop();

            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();
    }

    // Helper methods for easy access
    public static int getMaxJewelsPerTool() {
        return COMMON.maxJewelsPerTool.get();
    }

    public static double getMiningSpeedMultiplier() {
        return COMMON.miningSpeedMultiplierPerLevel.get();
    }

    public static double getDamagePerLevel() {
        return COMMON.damagePerLevel.get();
    }

    public static double getCriticalChancePerLevel() {
        return COMMON.criticalChancePerLevel.get();
    }

    public static double getReachBonusPerLevel() {
        return COMMON.reachBonusPerLevel.get();
    }

    public static int getAoeMiningMaxBlocks() {
        return COMMON.aoeMiningMaxBlocks.get();
    }

    public static int getTreeFellingMaxBlocks() {
        return COMMON.treeFellingMaxBlocks.get();
    }

    public static double getKnockbackPerLevel() {
        return COMMON.knockbackPerLevel.get();
    }

    public static double getLifestealPercentPerLevel() {
        return COMMON.lifestealPercentPerLevel.get();
    }

    public static long getDominoEffectTimeout() {
        return COMMON.dominoEffectChainTimeout.get();
    }

    public static int getMaterialSlots() {
        return COMMON.materialSlotsInCreationStation.get();
    }

    public static int getCreationStationGuiWidth() {
        return COMMON.creationStationGuiWidth.get();
    }

    public static int getCreationStationGuiHeight() {
        return COMMON.creationStationGuiHeight.get();
    }
}
package com.jewelcharms.util;

import net.minecraft.ChatFormatting;

/**
 * Represents the rarity tiers of jewels based on the materials used to create them.
 * Rarity affects visual effects, stat multipliers, and minigame difficulty.
 */
public enum JewelRarity {
    COMMON("common", "Common", 0, 5, 0xAAAAAA, ChatFormatting.GRAY, 1.0f, 4, 2),
    UNCOMMON("uncommon", "Uncommon", 6, 10, 0x55FF55, ChatFormatting.GREEN, 1.1f, 6, 4),
    RARE("rare", "Rare", 11, 15, 0x5555FF, ChatFormatting.BLUE, 1.25f, 8, 4),
    EPIC("epic", "Epic", 16, 20, 0xAA00AA, ChatFormatting.LIGHT_PURPLE, 1.5f, 10, 6),
    LEGENDARY("legendary", "Legendary", 21, 999, 0xFFAA00, ChatFormatting.GOLD, 2.0f, 12, 6);

    private final String id;
    private final String displayName;
    private final int minScore;
    private final int maxScore;
    private final int color;
    private final ChatFormatting chatFormatting;
    private final float statMultiplier;
    private final int puzzleGridSize;
    private final int puzzleCenterSize;

    JewelRarity(String id, String displayName, int minScore, int maxScore, int color,
                ChatFormatting chatFormatting, float statMultiplier, int puzzleGridSize, int puzzleCenterSize) {
        this.id = id;
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.color = color;
        this.chatFormatting = chatFormatting;
        this.statMultiplier = statMultiplier;
        this.puzzleGridSize = puzzleGridSize;
        this.puzzleCenterSize = puzzleCenterSize;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getColor() {
        return color;
    }

    public ChatFormatting getChatFormatting() {
        return chatFormatting;
    }

    public float getStatMultiplier() {
        return statMultiplier;
    }

    public int getPuzzleGridSize() {
        return puzzleGridSize;
    }

    public int getPuzzleCenterSize() {
        return puzzleCenterSize;
    }

    /**
     * Determines rarity based on the total rarity score of materials used.
     */
    public static JewelRarity fromScore(int score) {
        for (JewelRarity rarity : values()) {
            if (score >= rarity.minScore && score <= rarity.maxScore) {
                return rarity;
            }
        }
        return COMMON; // Default fallback
    }

    /**
     * Gets rarity from string ID. Returns COMMON if not found.
     */
    public static JewelRarity fromId(String id) {
        for (JewelRarity rarity : values()) {
            if (rarity.id.equals(id)) {
                return rarity;
            }
        }
        return COMMON; // Default fallback
    }

    /**
     * Returns whether this rarity should have particle effects.
     */
    public boolean hasParticles() {
        return this == EPIC || this == LEGENDARY;
    }

    /**
     * Returns the number of successful hits required for polishing minigame.
     */
    public int getPolishingHitsRequired() {
        return switch (this) {
            case COMMON -> 3;
            case UNCOMMON -> 4;
            case RARE -> 5;
            case EPIC -> 6;
            case LEGENDARY -> 7;
        };
    }

    /**
     * Returns the timing window (in seconds) for polishing rhythm game.
     */
    public float getPolishingTimingWindow() {
        return switch (this) {
            case COMMON -> 0.5f;
            case UNCOMMON -> 0.45f;
            case RARE -> 0.4f;
            case EPIC -> 0.3f;
            case LEGENDARY -> 0.2f;
        };
    }

    /**
     * Returns the XP cost to skip the puzzle minigame.
     */
    public int getSkipCost() {
        return switch (this) {
            case COMMON -> 5;
            case UNCOMMON -> 10;
            case RARE -> 15;
            case EPIC -> 20;
            case LEGENDARY -> 30;
        };
    }
}

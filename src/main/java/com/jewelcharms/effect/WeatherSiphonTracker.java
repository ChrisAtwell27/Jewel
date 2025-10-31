package com.jewelcharms.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Tracks charge levels for Weather Siphon effect
 * Tools charge in rain/thunder and deplete in sunshine
 * Charged tools provide bonuses
 */
public class WeatherSiphonTracker {

    private static final String CHARGE_TAG = "WeatherSiphonCharge";
    private static final String LAST_UPDATE_TAG = "WeatherSiphonLastUpdate";

    private static final int MAX_CHARGE = 1000;
    private static final int CHARGE_PER_TICK_RAIN = 2;
    private static final int CHARGE_PER_TICK_THUNDER = 5;
    private static final int DEPLETE_PER_TICK_SUN = 1;

    /**
     * Get the current charge level of an item (0-1000)
     */
    public static int getCharge(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(CHARGE_TAG)) {
            return tag.getInt(CHARGE_TAG);
        }
        return 0;
    }

    /**
     * Set the charge level of an item
     */
    public static void setCharge(ItemStack stack, int charge) {
        if (stack.isEmpty()) return;

        charge = Math.max(0, Math.min(MAX_CHARGE, charge)); // Clamp to 0-MAX_CHARGE
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(CHARGE_TAG, charge);
        tag.putLong(LAST_UPDATE_TAG, System.currentTimeMillis());
    }

    /**
     * Add charge to an item
     */
    public static void addCharge(ItemStack stack, int amount) {
        setCharge(stack, getCharge(stack) + amount);
    }

    /**
     * Remove charge from an item
     */
    public static void removeCharge(ItemStack stack, int amount) {
        setCharge(stack, getCharge(stack) - amount);
    }

    /**
     * Get the charge percentage (0.0 to 1.0)
     */
    public static float getChargePercentage(ItemStack stack) {
        return (float) getCharge(stack) / MAX_CHARGE;
    }

    /**
     * Get the charge level as a tier (0-5)
     * 0 = No charge, 5 = Max charge
     */
    public static int getChargeTier(ItemStack stack) {
        float percentage = getChargePercentage(stack);
        return (int) (percentage * 5);
    }

    /**
     * Get damage bonus multiplier based on charge (1.0 to 2.0)
     */
    public static float getDamageMultiplier(ItemStack stack) {
        float percentage = getChargePercentage(stack);
        return 1.0f + (percentage * 1.0f); // +0% to +100% damage
    }

    /**
     * Get speed bonus multiplier based on charge (1.0 to 1.5)
     */
    public static float getSpeedMultiplier(ItemStack stack) {
        float percentage = getChargePercentage(stack);
        return 1.0f + (percentage * 0.5f); // +0% to +50% speed
    }

    /**
     * Update charge based on weather
     * Called every tick for held items
     * @param isRaining if it's raining
     * @param isThundering if it's thundering
     * @param isSunny if it's sunny (clear day)
     */
    public static void updateCharge(ItemStack stack, boolean isRaining, boolean isThundering, boolean isSunny) {
        if (stack.isEmpty()) return;

        int currentCharge = getCharge(stack);

        if (isThundering) {
            // Charge quickly during thunder
            addCharge(stack, CHARGE_PER_TICK_THUNDER);
        } else if (isRaining) {
            // Charge slowly during rain
            addCharge(stack, CHARGE_PER_TICK_RAIN);
        } else if (isSunny) {
            // Deplete during sunshine
            removeCharge(stack, DEPLETE_PER_TICK_SUN);
        }
        // During night or other conditions, maintain current charge
    }

    /**
     * Consume charge for an action (e.g., attack or block break)
     * @param amount amount of charge to consume
     * @return true if there was enough charge, false otherwise
     */
    public static boolean consumeCharge(ItemStack stack, int amount) {
        int currentCharge = getCharge(stack);
        if (currentCharge >= amount) {
            removeCharge(stack, amount);
            return true;
        }
        return false;
    }

    /**
     * Check if the item has any charge
     */
    public static boolean hasCharge(ItemStack stack) {
        return getCharge(stack) > 0;
    }

    /**
     * Check if the item is fully charged
     */
    public static boolean isFullyCharged(ItemStack stack) {
        return getCharge(stack) >= MAX_CHARGE;
    }

    /**
     * Get max charge value
     */
    public static int getMaxCharge() {
        return MAX_CHARGE;
    }

    /**
     * Get visual charge bar color based on charge level
     * Returns RGB color value
     */
    public static int getChargeColor(ItemStack stack) {
        float percentage = getChargePercentage(stack);

        if (percentage < 0.25f) {
            return 0x4A4A4A; // Dark gray - low charge
        } else if (percentage < 0.5f) {
            return 0x5DADE2; // Light blue - medium charge
        } else if (percentage < 0.75f) {
            return 0x3498DB; // Blue - good charge
        } else {
            return 0x1E90FF; // Bright blue - high charge
        }
    }
}

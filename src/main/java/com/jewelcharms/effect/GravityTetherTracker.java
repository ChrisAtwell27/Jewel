package com.jewelcharms.effect;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Tracks orbiting items for Gravity Tether effect
 * Items orbit the player instead of falling, can be collected with a key press
 */
public class GravityTetherTracker {

    private static class OrbitingItem {
        ItemEntity entity;
        double angle;
        double radius;
        double height;
        long startTime;

        OrbitingItem(ItemEntity entity, double angle, double radius, double height) {
            this.entity = entity;
            this.angle = angle;
            this.radius = radius;
            this.height = height;
            this.startTime = System.currentTimeMillis();
        }
    }

    private static final Map<UUID, List<OrbitingItem>> playerOrbitingItems = new HashMap<>();
    private static final double ORBIT_SPEED = 0.05; // Radians per tick
    private static final double BASE_RADIUS = 2.0; // Base orbit radius
    private static final int MAX_ORBITING_ITEMS = 64; // Maximum items per player

    /**
     * Register an item to orbit around a player
     */
    public static void addOrbitingItem(Player player, ItemEntity item) {
        UUID playerId = player.getUUID();
        List<OrbitingItem> items = playerOrbitingItems.computeIfAbsent(playerId, k -> new ArrayList<>());

        // Don't add if at max capacity
        if (items.size() >= MAX_ORBITING_ITEMS) {
            return;
        }

        // Calculate starting angle based on current item count (evenly distribute)
        double angle = (2 * Math.PI * items.size()) / Math.max(1, items.size() + 1);
        double radius = BASE_RADIUS + (items.size() / 10.0); // Increase radius with more items
        double height = 0.5 + (items.size() % 3) * 0.5; // Vary height in layers

        items.add(new OrbitingItem(item, angle, radius, height));

        // Mark the item as not pickupable temporarily to prevent auto-pickup
        item.setPickUpDelay(10);
    }

    /**
     * Update orbiting items for a player
     * Called every tick
     */
    public static void updateOrbitingItems(Player player) {
        UUID playerId = player.getUUID();
        List<OrbitingItem> items = playerOrbitingItems.get(playerId);

        if (items == null || items.isEmpty()) {
            return;
        }

        Vec3 playerPos = player.position();
        Iterator<OrbitingItem> iterator = items.iterator();

        while (iterator.hasNext()) {
            OrbitingItem orbitItem = iterator.next();

            // Check if entity is still valid
            if (orbitItem.entity.isRemoved() || !orbitItem.entity.isAlive()) {
                iterator.remove();
                continue;
            }

            // Update angle
            orbitItem.angle += ORBIT_SPEED;
            if (orbitItem.angle > 2 * Math.PI) {
                orbitItem.angle -= 2 * Math.PI;
            }

            // Calculate new position
            double x = playerPos.x + Math.cos(orbitItem.angle) * orbitItem.radius;
            double y = playerPos.y + orbitItem.height + player.getEyeHeight() / 2;
            double z = playerPos.z + Math.sin(orbitItem.angle) * orbitItem.radius;

            // Set position and velocity
            orbitItem.entity.setPos(x, y, z);
            orbitItem.entity.setDeltaMovement(Vec3.ZERO);
            orbitItem.entity.setOnGround(false);

            // Reset pickup delay
            orbitItem.entity.setPickUpDelay(10);

            // Optional: Remove items after 5 minutes
            if (System.currentTimeMillis() - orbitItem.startTime > 300000) {
                iterator.remove();
            }
        }
    }

    /**
     * Collect all orbiting items for a player
     * Returns true if any items were collected
     */
    public static boolean collectAll(Player player) {
        UUID playerId = player.getUUID();
        List<OrbitingItem> items = playerOrbitingItems.get(playerId);

        if (items == null || items.isEmpty()) {
            return false;
        }

        boolean collected = false;
        Iterator<OrbitingItem> iterator = items.iterator();

        while (iterator.hasNext()) {
            OrbitingItem orbitItem = iterator.next();

            if (orbitItem.entity.isAlive() && !orbitItem.entity.isRemoved()) {
                // Reset pickup delay to allow immediate pickup
                orbitItem.entity.setPickUpDelay(0);
                // Move to player position
                orbitItem.entity.setPos(player.getX(), player.getY(), player.getZ());
                collected = true;
            }

            iterator.remove();
        }

        return collected;
    }

    /**
     * Get orbiting item count for a player
     */
    public static int getOrbitingCount(UUID playerId) {
        List<OrbitingItem> items = playerOrbitingItems.get(playerId);
        return items != null ? items.size() : 0;
    }

    /**
     * Clear all orbiting items for a player (e.g., on death)
     */
    public static void clearPlayer(UUID playerId) {
        playerOrbitingItems.remove(playerId);
    }

    /**
     * Clear all orbiting items (e.g., on server shutdown)
     */
    public static void clearAll() {
        playerOrbitingItems.clear();
    }
}

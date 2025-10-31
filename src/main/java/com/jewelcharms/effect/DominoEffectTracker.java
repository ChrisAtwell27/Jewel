package com.jewelcharms.effect;

import net.minecraft.world.level.block.Block;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks domino effect chains for players
 * First block = 1x drops, second = 2x drops, third = 3x drops, etc.
 * Breaking a different block type resets the chain
 */
public class DominoEffectTracker {

    private static class ChainData {
        Block currentBlock;
        int chainCount;
        long lastBreakTime;

        ChainData(Block block) {
            this.currentBlock = block;
            this.chainCount = 1;
            this.lastBreakTime = System.currentTimeMillis();
        }
    }

    private static final Map<UUID, ChainData> playerChains = new HashMap<>();
    private static final long CHAIN_TIMEOUT = 3000; // 3 seconds timeout

    /**
     * Called when a player breaks a block with Domino Effect
     * @return the multiplier for drops (1x, 2x, 3x, etc.)
     */
    public static int onBlockBreak(UUID playerId, Block brokenBlock) {
        ChainData chain = playerChains.get(playerId);
        long currentTime = System.currentTimeMillis();

        // Check if chain exists and is valid
        if (chain != null) {
            // Check timeout
            if (currentTime - chain.lastBreakTime > CHAIN_TIMEOUT) {
                // Chain timed out, reset
                chain = new ChainData(brokenBlock);
                playerChains.put(playerId, chain);
                return 1;
            }

            // Check if same block type
            if (chain.currentBlock == brokenBlock) {
                // Continue chain
                chain.chainCount++;
                chain.lastBreakTime = currentTime;
                return chain.chainCount;
            } else {
                // Different block, reset chain
                chain = new ChainData(brokenBlock);
                playerChains.put(playerId, chain);
                return 1;
            }
        } else {
            // New chain
            chain = new ChainData(brokenBlock);
            playerChains.put(playerId, chain);
            return 1;
        }
    }

    /**
     * Get current chain count for display purposes
     */
    public static int getChainCount(UUID playerId) {
        ChainData chain = playerChains.get(playerId);
        if (chain == null) return 0;

        long currentTime = System.currentTimeMillis();
        if (currentTime - chain.lastBreakTime > CHAIN_TIMEOUT) {
            return 0;
        }

        return chain.chainCount;
    }

    /**
     * Reset a player's chain (e.g., on death, dimension change)
     */
    public static void resetChain(UUID playerId) {
        playerChains.remove(playerId);
    }

    /**
     * Clear all chains (e.g., on server shutdown)
     */
    public static void clearAll() {
        playerChains.clear();
    }
}

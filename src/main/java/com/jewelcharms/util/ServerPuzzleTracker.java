package com.jewelcharms.util;

import com.jewelcharms.JewelCharms;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks puzzle states on the server side to prevent cheating
 * and ensure puzzle logic is validated server-side
 */
public class ServerPuzzleTracker {

    public static class ActivePuzzle {
        public final PuzzleState state;
        public final JewelData jewelData;
        public final BlockPos blockPos;
        public final long startTime;
        public int moveCount;

        public ActivePuzzle(PuzzleState state, JewelData jewelData, BlockPos blockPos) {
            this.state = state;
            this.jewelData = jewelData;
            this.blockPos = blockPos;
            this.startTime = System.currentTimeMillis();
            this.moveCount = 0;
        }

        public boolean isExpired() {
            // Expire after 10 minutes
            return System.currentTimeMillis() - startTime > 600000;
        }
    }

    // Track active puzzles per player UUID
    private static final Map<UUID, ActivePuzzle> activePuzzles = new HashMap<>();

    /**
     * Start a new puzzle for a player
     */
    public static void startPuzzle(ServerPlayer player, JewelData jewelData, BlockPos blockPos) {
        UUID playerId = player.getUUID();

        // Check if player already has an active puzzle
        if (activePuzzles.containsKey(playerId)) {
            JewelCharms.LOGGER.warn("Player {} already has an active puzzle, overwriting", player.getName().getString());
        }

        // Create puzzle based on jewel rarity
        int gridSize = jewelData.getRarity().getPuzzleGridSize();
        int centerSize = jewelData.getRarity().getPuzzleCenterSize();
        PuzzleState puzzleState = new PuzzleState(gridSize, centerSize, jewelData.getIndividualColors());

        // Scramble the puzzle
        int scrambleCount = gridSize * gridSize * 3;
        puzzleState.scramble(player.level().random, scrambleCount);

        // Store the puzzle
        ActivePuzzle activePuzzle = new ActivePuzzle(puzzleState, jewelData, blockPos);
        activePuzzles.put(playerId, activePuzzle);

        JewelCharms.LOGGER.info("Started server-side puzzle for player {} with grid size {}",
            player.getName().getString(), gridSize);
    }

    /**
     * Process a tile click from a player
     * Returns true if the move was valid
     */
    public static boolean processTileClick(ServerPlayer player, int row, int col) {
        UUID playerId = player.getUUID();
        ActivePuzzle puzzle = activePuzzles.get(playerId);

        if (puzzle == null) {
            JewelCharms.LOGGER.error("Player {} tried to click puzzle tile but has no active puzzle",
                player.getName().getString());
            return false;
        }

        // Check if puzzle is expired
        if (puzzle.isExpired()) {
            JewelCharms.LOGGER.warn("Puzzle for player {} has expired", player.getName().getString());
            activePuzzles.remove(playerId);
            return false;
        }

        // Try to slide the tile
        boolean slid = puzzle.state.slide(row, col);
        if (slid) {
            puzzle.moveCount++;
            JewelCharms.LOGGER.debug("Player {} made move {} at ({}, {})",
                player.getName().getString(), puzzle.moveCount, row, col);
        }

        return slid;
    }

    /**
     * Check if a player's puzzle is solved
     */
    public static boolean isPuzzleSolved(ServerPlayer player) {
        UUID playerId = player.getUUID();
        ActivePuzzle puzzle = activePuzzles.get(playerId);

        if (puzzle == null) {
            return false;
        }

        return puzzle.state.isSolved();
    }

    /**
     * Get the active puzzle for a player
     */
    public static ActivePuzzle getActivePuzzle(ServerPlayer player) {
        return activePuzzles.get(player.getUUID());
    }

    /**
     * Get the puzzle state for a player (for synchronization)
     */
    public static PuzzleState getPuzzleState(ServerPlayer player) {
        ActivePuzzle puzzle = activePuzzles.get(player.getUUID());
        return puzzle != null ? puzzle.state : null;
    }

    /**
     * Complete a puzzle and remove it from tracking
     */
    public static JewelData completePuzzle(ServerPlayer player) {
        UUID playerId = player.getUUID();
        ActivePuzzle puzzle = activePuzzles.remove(playerId);

        if (puzzle != null) {
            JewelCharms.LOGGER.info("Player {} completed puzzle in {} moves",
                player.getName().getString(), puzzle.moveCount);
            return puzzle.jewelData;
        }

        return null;
    }

    /**
     * Cancel a puzzle (player closed GUI, etc.)
     */
    public static void cancelPuzzle(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (activePuzzles.remove(playerId) != null) {
            JewelCharms.LOGGER.info("Cancelled puzzle for player {}", player.getName().getString());
        }
    }

    /**
     * Clear all puzzles (server shutdown)
     */
    public static void clearAll() {
        activePuzzles.clear();
    }

    /**
     * Clean up expired puzzles periodically
     */
    public static void cleanupExpired() {
        activePuzzles.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                JewelCharms.LOGGER.info("Removing expired puzzle for player {}", entry.getKey());
                return true;
            }
            return false;
        });
    }
}
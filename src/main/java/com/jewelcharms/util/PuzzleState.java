package com.jewelcharms.util;

import net.minecraft.util.RandomSource;

import java.util.*;

/**
 * Manages the state and logic of the slide puzzle minigame.
 * Supports dynamic grid sizes and color-based win conditions.
 */
public class PuzzleState {
    private final int gridSize;
    private final int centerSize;
    private final List<Integer> colors; // Material colors (1-5 colors)
    private int[][] grid; // Color index at each position (-1 = empty)
    private int emptyRow;
    private int emptyCol;

    public PuzzleState(int gridSize, int centerSize, List<Integer> colors) {
        this.gridSize = gridSize;
        this.centerSize = centerSize;
        this.colors = new ArrayList<>(colors);
        this.grid = new int[gridSize][gridSize];
        initializeSolvedState();
    }

    /**
     * Creates a solved state where all colored tiles are in the center,
     * and gray filler tiles are on the outside.
     */
    private void initializeSolvedState() {
        int centerStart = (gridSize - centerSize) / 2;
        int centerEnd = centerStart + centerSize;
        int centerTiles = centerSize * centerSize;
        final int GRAY_TILE_INDEX = colors.size(); // Gray tiles use this special index

        // Fill center with colored tiles (evenly distributed among colors)
        List<Integer> centerColorTiles = new ArrayList<>();
        int tilesPerColor = centerTiles / colors.size();
        int extraTiles = centerTiles % colors.size();

        for (int colorIdx = 0; colorIdx < colors.size(); colorIdx++) {
            int count = tilesPerColor + (colorIdx < extraTiles ? 1 : 0);
            for (int i = 0; i < count; i++) {
                centerColorTiles.add(colorIdx);
            }
        }

        // Shuffle colored tiles so they're not in order
        Collections.shuffle(centerColorTiles);

        // Fill the entire grid
        int colorTileIndex = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (row >= centerStart && row < centerEnd && col >= centerStart && col < centerEnd) {
                    // Center area: place colored tiles
                    grid[row][col] = centerColorTiles.get(colorTileIndex++);
                } else {
                    // Outside area: place gray filler tiles
                    grid[row][col] = GRAY_TILE_INDEX;
                }
            }
        }

        // Place empty tile at bottom-right
        grid[gridSize - 1][gridSize - 1] = -1;
        emptyRow = gridSize - 1;
        emptyCol = gridSize - 1;
    }

    /**
     * Scrambles the puzzle by performing random valid moves.
     * This guarantees the puzzle is solvable.
     */
    public void scramble(RandomSource random, int moves) {
        for (int i = 0; i < moves; i++) {
            List<int[]> validMoves = getValidMoves();
            if (!validMoves.isEmpty()) {
                int[] move = validMoves.get(random.nextInt(validMoves.size()));
                slide(move[0], move[1]);
            }
        }
    }

    /**
     * Gets all valid adjacent tiles that can slide into the empty space.
     */
    private List<int[]> getValidMoves() {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        for (int[] dir : directions) {
            int newRow = emptyRow + dir[0];
            int newCol = emptyCol + dir[1];
            if (isValidPosition(newRow, newCol)) {
                moves.add(new int[]{newRow, newCol});
            }
        }
        return moves;
    }

    /**
     * Attempts to slide the tile at (row, col) into the empty space.
     * Returns true if the slide was successful.
     */
    public boolean slide(int row, int col) {
        if (!canSlide(row, col)) {
            return false;
        }

        // Swap tile with empty space
        grid[emptyRow][emptyCol] = grid[row][col];
        grid[row][col] = -1;
        emptyRow = row;
        emptyCol = col;
        return true;
    }

    /**
     * Checks if the tile at (row, col) can slide into the empty space.
     */
    public boolean canSlide(int row, int col) {
        if (!isValidPosition(row, col) || grid[row][col] == -1) {
            return false;
        }

        // Must be adjacent to empty space
        int rowDiff = Math.abs(row - emptyRow);
        int colDiff = Math.abs(col - emptyCol);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    /**
     * Checks if the puzzle is solved (all colored tiles are in center area).
     * Win condition: ALL colored tiles must be inside the center, and NO colored tiles outside.
     */
    public boolean isSolved() {
        int centerStart = (gridSize - centerSize) / 2;
        int centerEnd = centerStart + centerSize;

        // Check the entire grid
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int colorIdx = grid[row][col];
                boolean isColoredTile = (colorIdx >= 0 && colorIdx < colors.size());
                boolean isInCenter = (row >= centerStart && row < centerEnd && col >= centerStart && col < centerEnd);

                // If we find a colored tile outside the center, puzzle is not solved
                if (isColoredTile && !isInCenter) {
                    return false;
                }

                // If we find a gray tile or empty space inside the center, puzzle is not solved
                if (!isColoredTile && isInCenter) {
                    return false;
                }
            }
        }

        // All colored tiles are in center, all gray tiles are outside
        return true;
    }

    /**
     * Gets the color index at the given position.
     */
    public int getColorIndex(int row, int col) {
        if (!isValidPosition(row, col)) {
            return -1;
        }
        return grid[row][col];
    }

    /**
     * Gets the actual color value at the given position.
     */
    public int getColor(int row, int col) {
        int colorIdx = getColorIndex(row, col);
        if (colorIdx == -1) {
            return 0x202020; // Dark gray for empty
        }
        if (colorIdx >= colors.size()) {
            return 0x808080; // Gray for filler tiles
        }
        return colors.get(colorIdx);
    }

    /**
     * Checks if the tile at this position is a gray filler tile.
     */
    public boolean isGrayTile(int row, int col) {
        int colorIdx = getColorIndex(row, col);
        return colorIdx >= 0 && colorIdx >= colors.size();
    }

    /**
     * Checks if the position is within the center target area.
     */
    public boolean isInCenter(int row, int col) {
        int centerStart = (gridSize - centerSize) / 2;
        int centerEnd = centerStart + centerSize;
        return row >= centerStart && row < centerEnd && col >= centerStart && col < centerEnd;
    }

    /**
     * Checks if the position is the empty tile.
     */
    public boolean isEmpty(int row, int col) {
        return row == emptyRow && col == emptyCol;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getCenterSize() {
        return centerSize;
    }

    public int getEmptyRow() {
        return emptyRow;
    }

    public int getEmptyCol() {
        return emptyCol;
    }

    /**
     * Serializes the puzzle state to a string for network transmission.
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(gridSize).append(";");
        sb.append(centerSize).append(";");
        sb.append(colors.size()).append(";");
        for (int color : colors) {
            sb.append(color).append(",");
        }
        sb.append(";");
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                sb.append(grid[row][col]).append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Deserializes a puzzle state from a string.
     */
    public static PuzzleState deserialize(String data) {
        String[] parts = data.split(";");
        int gridSize = Integer.parseInt(parts[0]);
        int centerSize = Integer.parseInt(parts[1]);
        int colorCount = Integer.parseInt(parts[2]);

        List<Integer> colors = new ArrayList<>();
        String[] colorStrs = parts[3].split(",");
        for (int i = 0; i < colorCount; i++) {
            colors.add(Integer.parseInt(colorStrs[i]));
        }

        PuzzleState state = new PuzzleState(gridSize, centerSize, colors);
        String[] gridStrs = parts[4].split(",");
        int idx = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int value = Integer.parseInt(gridStrs[idx++]);
                state.grid[row][col] = value;
                if (value == -1) {
                    state.emptyRow = row;
                    state.emptyCol = col;
                }
            }
        }
        return state;
    }
}

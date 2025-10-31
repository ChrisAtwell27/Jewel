package com.jewelcharms.effect;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

/**
 * Tracks musical notes generated from breaking blocks
 * Completing specific melodies grants bonus loot
 */
public class SoundHarvestingTracker {

    // Map blocks to musical notes (simplified to 7 notes: C, D, E, F, G, A, B)
    private static final Map<Block, Integer> BLOCK_TO_NOTE = new HashMap<>();

    // Define some simple melodies (sequences of notes)
    // Each melody is represented as an array of note indices (0-6 for C-B)
    private static final int[][] MELODIES = {
        {0, 2, 4, 5, 4, 2, 0},        // C Major scale up and down (7 notes)
        {0, 0, 4, 4, 5, 5, 4},         // Twinkle Twinkle (7 notes)
        {2, 2, 1, 2, 4},               // Simple melody (5 notes)
        {0, 4, 7},                     // Power chord (3 notes) - Note 7 is high C
        {4, 2, 0}                      // Descending (3 notes)
    };

    private static final String[] NOTE_NAMES = {"C", "D", "E", "F", "G", "A", "B", "C2"};

    static {
        // Map various blocks to different notes
        // This creates a musical experience when breaking different blocks
        BLOCK_TO_NOTE.put(Blocks.STONE, 0);           // C
        BLOCK_TO_NOTE.put(Blocks.COBBLESTONE, 0);     // C
        BLOCK_TO_NOTE.put(Blocks.DIRT, 1);            // D
        BLOCK_TO_NOTE.put(Blocks.GRASS_BLOCK, 1);     // D
        BLOCK_TO_NOTE.put(Blocks.OAK_LOG, 2);         // E
        BLOCK_TO_NOTE.put(Blocks.BIRCH_LOG, 2);       // E
        BLOCK_TO_NOTE.put(Blocks.SAND, 3);            // F
        BLOCK_TO_NOTE.put(Blocks.GRAVEL, 3);          // F
        BLOCK_TO_NOTE.put(Blocks.COAL_ORE, 4);        // G
        BLOCK_TO_NOTE.put(Blocks.IRON_ORE, 4);        // G
        BLOCK_TO_NOTE.put(Blocks.GOLD_ORE, 5);        // A
        BLOCK_TO_NOTE.put(Blocks.DIAMOND_ORE, 5);     // A
        BLOCK_TO_NOTE.put(Blocks.EMERALD_ORE, 6);     // B
        BLOCK_TO_NOTE.put(Blocks.LAPIS_ORE, 6);       // B
        BLOCK_TO_NOTE.put(Blocks.NETHERRACK, 1);      // D
        BLOCK_TO_NOTE.put(Blocks.END_STONE, 7);       // High C
    }

    private static class NoteSequence {
        List<Integer> notes;
        long lastNoteTime;

        NoteSequence() {
            this.notes = new ArrayList<>();
            this.lastNoteTime = System.currentTimeMillis();
        }
    }

    private static final Map<UUID, NoteSequence> playerSequences = new HashMap<>();
    private static final long NOTE_TIMEOUT = 5000; // 5 seconds between notes
    private static final int MAX_SEQUENCE_LENGTH = 10;

    /**
     * Called when a player breaks a block with Sound Harvesting
     * @return the note index (0-7) or -1 if no note
     */
    public static int onBlockBreak(UUID playerId, Block brokenBlock) {
        Integer note = BLOCK_TO_NOTE.get(brokenBlock);
        if (note == null) {
            return -1;
        }

        NoteSequence sequence = playerSequences.get(playerId);
        long currentTime = System.currentTimeMillis();

        if (sequence == null) {
            sequence = new NoteSequence();
            playerSequences.put(playerId, sequence);
        } else {
            // Check timeout
            if (currentTime - sequence.lastNoteTime > NOTE_TIMEOUT) {
                sequence.notes.clear();
            }
        }

        // Add note to sequence
        sequence.notes.add(note);
        sequence.lastNoteTime = currentTime;

        // Limit sequence length
        if (sequence.notes.size() > MAX_SEQUENCE_LENGTH) {
            sequence.notes.remove(0);
        }

        return note;
    }

    /**
     * Check if the player's current note sequence matches any melody
     * @return melody index (0-4) or -1 if no match
     */
    public static int checkForMelodyMatch(UUID playerId) {
        NoteSequence sequence = playerSequences.get(playerId);
        if (sequence == null || sequence.notes.isEmpty()) {
            return -1;
        }

        // Check each melody
        for (int melodyIndex = 0; melodyIndex < MELODIES.length; melodyIndex++) {
            int[] melody = MELODIES[melodyIndex];

            // Check if the end of the sequence matches the melody
            if (sequence.notes.size() >= melody.length) {
                boolean matches = true;
                int offset = sequence.notes.size() - melody.length;

                for (int i = 0; i < melody.length; i++) {
                    if (!sequence.notes.get(offset + i).equals(melody[i])) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    // Clear the sequence after a successful match
                    sequence.notes.clear();
                    return melodyIndex;
                }
            }
        }

        return -1;
    }

    /**
     * Get the note name for display
     */
    public static String getNoteName(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < NOTE_NAMES.length) {
            return NOTE_NAMES[noteIndex];
        }
        return "";
    }

    /**
     * Get the current sequence length for a player
     */
    public static int getSequenceLength(UUID playerId) {
        NoteSequence sequence = playerSequences.get(playerId);
        return sequence != null ? sequence.notes.size() : 0;
    }

    /**
     * Get reward multiplier based on melody complexity
     */
    public static int getRewardMultiplier(int melodyIndex) {
        if (melodyIndex < 0 || melodyIndex >= MELODIES.length) {
            return 1;
        }

        // Longer melodies give better rewards
        int melodyLength = MELODIES[melodyIndex].length;
        return Math.max(1, melodyLength / 2); // 7-note melody = 3x, 5-note = 2x, 3-note = 1x
    }

    /**
     * Reset a player's sequence
     */
    public static void resetSequence(UUID playerId) {
        playerSequences.remove(playerId);
    }

    /**
     * Clear all sequences
     */
    public static void clearAll() {
        playerSequences.clear();
    }

    /**
     * Get the sound event for a note
     */
    public static SoundEvent getSoundForNote(int noteIndex) {
        // Use note block sounds
        return switch (noteIndex) {
            case 0 -> SoundEvents.NOTE_BLOCK_HARP.value();
            case 1 -> SoundEvents.NOTE_BLOCK_HARP.value();
            case 2 -> SoundEvents.NOTE_BLOCK_HARP.value();
            case 3 -> SoundEvents.NOTE_BLOCK_HARP.value();
            case 4 -> SoundEvents.NOTE_BLOCK_HARP.value();
            case 5 -> SoundEvents.NOTE_BLOCK_HARP.value();
            case 6 -> SoundEvents.NOTE_BLOCK_HARP.value();
            default -> SoundEvents.NOTE_BLOCK_HARP.value();
        };
    }

    /**
     * Get pitch for a note (0.5 to 2.0)
     */
    public static float getPitchForNote(int noteIndex) {
        // Musical scale: C, D, E, F, G, A, B, C2
        // Semitone offsets: 0, 2, 4, 5, 7, 9, 11, 12
        float[] pitches = {0.5f, 0.595f, 0.667f, 0.707f, 0.794f, 0.891f, 1.0f, 1.059f};
        if (noteIndex >= 0 && noteIndex < pitches.length) {
            return pitches[noteIndex];
        }
        return 1.0f;
    }
}

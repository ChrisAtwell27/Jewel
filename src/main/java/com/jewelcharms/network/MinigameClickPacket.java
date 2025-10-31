package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MinigameClickPacket {
    private final BlockPos pos;
    private final int gridX;
    private final int gridY;
    private final boolean correctClick;

    public MinigameClickPacket(BlockPos pos, int gridX, int gridY, boolean correctClick) {
        this.pos = pos;
        this.gridX = gridX;
        this.gridY = gridY;
        this.correctClick = correctClick;
    }

    public static void encode(MinigameClickPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeInt(packet.gridX);
        buffer.writeInt(packet.gridY);
        buffer.writeBoolean(packet.correctClick);
    }

    public static MinigameClickPacket decode(FriendlyByteBuf buffer) {
        return new MinigameClickPacket(
            buffer.readBlockPos(),
            buffer.readInt(),
            buffer.readInt(),
            buffer.readBoolean()
        );
    }

    public static void handle(MinigameClickPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null) {
                JewelCharms.LOGGER.error("MinigameClickPacket: Player is null!");
                return;
            }

            // Server-side handling - process the tile click
            boolean validMove = com.jewelcharms.util.ServerPuzzleTracker.processTileClick(player, packet.gridY, packet.gridX);

            if (validMove) {
                // Get the updated puzzle state
                var activePuzzle = com.jewelcharms.util.ServerPuzzleTracker.getActivePuzzle(player);
                if (activePuzzle != null) {
                    // Send updated state back to client
                    ModNetwork.sendToPlayer(new PuzzleStateUpdatePacket(
                        packet.pos,
                        activePuzzle.state.serialize(),
                        activePuzzle.jewelData.serialize(),
                        false,  // not starting, just updating
                        activePuzzle.moveCount
                    ), player);

                    // Check if puzzle is solved
                    if (activePuzzle.state.isSolved()) {
                        JewelCharms.LOGGER.info("Player {} solved the puzzle!", player.getName().getString());
                        // The client will handle sending the completion packet
                    }
                }
            }

            JewelCharms.LOGGER.debug("Processed minigame click at ({}, {}) - valid: {}",
                packet.gridX, packet.gridY, validMove);
        });
        context.setPacketHandled(true);
    }
}

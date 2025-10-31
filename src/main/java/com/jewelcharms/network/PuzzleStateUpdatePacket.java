package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.client.screen.JewelCreationStationScreen;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.PuzzleState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from server to client to update puzzle state
 */
public class PuzzleStateUpdatePacket {
    private final BlockPos pos;
    private final String puzzleStateData;
    private final String jewelDataString;
    private final boolean startPuzzle;
    private final int moveCount;

    public PuzzleStateUpdatePacket(BlockPos pos, String puzzleStateData, String jewelDataString, boolean startPuzzle, int moveCount) {
        this.pos = pos;
        this.puzzleStateData = puzzleStateData;
        this.jewelDataString = jewelDataString;
        this.startPuzzle = startPuzzle;
        this.moveCount = moveCount;
    }

    public static void encode(PuzzleStateUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.puzzleStateData);
        buffer.writeUtf(packet.jewelDataString);
        buffer.writeBoolean(packet.startPuzzle);
        buffer.writeInt(packet.moveCount);
    }

    public static PuzzleStateUpdatePacket decode(FriendlyByteBuf buffer) {
        return new PuzzleStateUpdatePacket(
            buffer.readBlockPos(),
            buffer.readUtf(),
            buffer.readUtf(),
            buffer.readBoolean(),
            buffer.readInt()
        );
    }

    public static void handle(PuzzleStateUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            handleClientSide(packet);
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(PuzzleStateUpdatePacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof JewelCreationStationScreen screen) {
            // Deserialize puzzle state and jewel data
            PuzzleState puzzleState = PuzzleState.deserialize(packet.puzzleStateData);
            JewelData jewelData = JewelData.deserialize(packet.jewelDataString);

            if (puzzleState != null && jewelData != null) {
                // Update the screen with server state
                screen.updatePuzzleFromServer(puzzleState, jewelData, packet.startPuzzle, packet.moveCount);
                JewelCharms.LOGGER.debug("Updated client puzzle state from server");
            } else {
                JewelCharms.LOGGER.error("Failed to deserialize puzzle state from server");
            }
        }
    }
}
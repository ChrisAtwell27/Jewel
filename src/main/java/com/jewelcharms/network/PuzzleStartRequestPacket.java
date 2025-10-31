package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.util.JewelCreationHelper;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.ServerPuzzleTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Packet sent from client to server to request starting a puzzle
 */
public class PuzzleStartRequestPacket {
    private final BlockPos pos;

    public PuzzleStartRequestPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(PuzzleStartRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
    }

    public static PuzzleStartRequestPacket decode(FriendlyByteBuf buffer) {
        return new PuzzleStartRequestPacket(buffer.readBlockPos());
    }

    public static void handle(PuzzleStartRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                JewelCharms.LOGGER.error("PuzzleStartRequestPacket: Player is null!");
                return;
            }

            // Check if player is in JewelCreationStationMenu
            if (player.containerMenu instanceof JewelCreationStationMenu menu) {
                // Get materials and create jewel data
                List<ItemStack> materials = menu.getMaterialStacks();
                ItemStack jewel = JewelCreationHelper.createJewel(materials);

                if (!jewel.isEmpty()) {
                    JewelData jewelData = JewelData.fromItemStack(jewel);
                    if (jewelData != null) {
                        // Clear materials on server
                        menu.clearMaterialSlots();

                        // Start server-side puzzle
                        ServerPuzzleTracker.startPuzzle(player, jewelData, packet.pos);

                        // Get the puzzle state
                        var puzzleState = ServerPuzzleTracker.getPuzzleState(player);
                        if (puzzleState != null) {
                            // Send initial puzzle state to client
                            ModNetwork.sendToPlayer(new PuzzleStateUpdatePacket(
                                packet.pos,
                                puzzleState.serialize(),
                                jewelData.serialize(),
                                true,  // startPuzzle = true
                                0      // moveCount = 0
                            ), player);

                            JewelCharms.LOGGER.info("Started server-side puzzle for player {}", player.getName().getString());
                        }
                    } else {
                        JewelCharms.LOGGER.error("Failed to create JewelData from materials");
                    }
                } else {
                    JewelCharms.LOGGER.warn("Cannot start puzzle - invalid materials");
                }
            } else {
                JewelCharms.LOGGER.error("Player is not in JewelCreationStationMenu");
            }
        });
        context.setPacketHandled(true);
    }
}
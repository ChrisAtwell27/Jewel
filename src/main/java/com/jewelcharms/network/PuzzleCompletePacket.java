package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.init.ModItems;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.menu.PolishStationMenu;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.PuzzleState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when puzzle or polish minigame is completed.
 */
public class PuzzleCompletePacket {
    private final BlockPos pos;
    private final boolean success;
    private final String puzzleStateData;
    private final String jewelDataString; // Serialized JewelData

    public PuzzleCompletePacket(BlockPos pos, boolean success, String puzzleStateData, String jewelDataString) {
        this.pos = pos;
        this.success = success;
        this.puzzleStateData = puzzleStateData;
        this.jewelDataString = jewelDataString;
    }

    public static void encode(PuzzleCompletePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeBoolean(packet.success);
        buffer.writeUtf(packet.puzzleStateData);
        buffer.writeUtf(packet.jewelDataString);
    }

    public static PuzzleCompletePacket decode(FriendlyByteBuf buffer) {
        return new PuzzleCompletePacket(
            buffer.readBlockPos(),
            buffer.readBoolean(),
            buffer.readUtf(),
            buffer.readUtf()
        );
    }

    public static void handle(PuzzleCompletePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                JewelCharms.LOGGER.error("PuzzleCompletePacket: Player is null!");
                return;
            }

            JewelCharms.LOGGER.info("=== PuzzleCompletePacket received from player: {} ===", player.getName().getString());
            JewelCharms.LOGGER.info("Player container menu type: {}", player.containerMenu.getClass().getSimpleName());

            // Handle Jewel Creation Station (puzzle completion)
            if (player.containerMenu instanceof JewelCreationStationMenu menu) {
                JewelCharms.LOGGER.info("Player is in JewelCreationStationMenu - processing puzzle completion");
                if (packet.success) {
                    // Use server-side puzzle tracker for validation
                    if (com.jewelcharms.util.ServerPuzzleTracker.isPuzzleSolved(player)) {
                        // Complete the puzzle and get jewel data from server tracker
                        JewelData jewelData = com.jewelcharms.util.ServerPuzzleTracker.completePuzzle(player);

                        if (jewelData != null) {
                            JewelCharms.LOGGER.info("Creating rough jewel in output slot...");
                            // Create rough jewel and place in output slot
                            ItemStack roughJewel = new ItemStack(ModItems.ROUGH_JEWEL.get());
                            jewelData.saveToItemStack(roughJewel);
                            menu.setOutputJewel(roughJewel);

                            // Materials already cleared when puzzle started

                            JewelCharms.LOGGER.info("SUCCESS: Player {} completed puzzle and created rough jewel!",
                                player.getName().getString());
                        } else {
                            JewelCharms.LOGGER.error("ERROR: Could not get JewelData from server tracker!");
                        }
                    } else {
                        JewelCharms.LOGGER.warn("Player {} claimed to solve puzzle but server validation failed",
                            player.getName().getString());
                        // Cancel the puzzle
                        com.jewelcharms.util.ServerPuzzleTracker.cancelPuzzle(player);
                    }
                } else {
                    JewelCharms.LOGGER.info("Player {} failed/cancelled the puzzle", player.getName().getString());
                    // Cancel the server-side puzzle
                    com.jewelcharms.util.ServerPuzzleTracker.cancelPuzzle(player);
                    // Materials were already consumed, might want to restore them?
                }
            }
            // Handle Polish Station (polish minigame completion)
            else if (player.containerMenu instanceof PolishStationMenu menu) {
                JewelCharms.LOGGER.info("Player is in PolishStationMenu - processing polish completion");
                if (packet.success) {
                    // Deserialize jewel data from packet
                    JewelData jewelData = JewelData.deserialize(packet.jewelDataString);
                    JewelCharms.LOGGER.info("Jewel data received: {}", jewelData != null ? "valid" : "null");

                    if (jewelData != null) {
                        JewelCharms.LOGGER.info("Creating polished jewel...");
                        // Create polished jewel and give to player
                        ItemStack polishedJewel = new ItemStack(ModItems.JEWEL.get());
                        jewelData.saveToItemStack(polishedJewel);

                        // Clear the input slot
                        menu.clearInputSlot();

                        // Give polished jewel to player
                        if (!player.getInventory().add(polishedJewel)) {
                            // If inventory is full, drop it
                            player.drop(polishedJewel, false);
                        }

                        JewelCharms.LOGGER.info("SUCCESS: Player {} completed polish and created jewel!",
                            player.getName().getString());
                    } else {
                        JewelCharms.LOGGER.error("ERROR: Could not deserialize JewelData from packet!");
                    }
                } else {
                    JewelCharms.LOGGER.info("Player {} failed the polish minigame", player.getName().getString());
                    // Input jewel was already cleared on client side
                }
            } else {
                JewelCharms.LOGGER.error("ERROR: Player is NOT in a valid menu! Menu type: {}",
                    player.containerMenu.getClass().getSimpleName());
            }
        });
        context.setPacketHandled(true);
    }
}

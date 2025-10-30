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
                    // Verify puzzle solution on server side
                    PuzzleState puzzleState = PuzzleState.deserialize(packet.puzzleStateData);
                    JewelCharms.LOGGER.info("Puzzle solved status: {}", puzzleState.isSolved());

                    if (puzzleState.isSolved()) {
                        // Deserialize jewel data from packet
                        JewelData jewelData = JewelData.deserialize(packet.jewelDataString);
                        JewelCharms.LOGGER.info("Jewel data received: {}", jewelData != null ? "valid" : "null");

                        if (jewelData != null) {
                            JewelCharms.LOGGER.info("Creating rough jewel in output slot...");
                            // Create rough jewel and place in output slot
                            ItemStack roughJewel = new ItemStack(ModItems.ROUGH_JEWEL.get());
                            jewelData.saveToItemStack(roughJewel);
                            menu.setOutputJewel(roughJewel);

                            JewelCharms.LOGGER.info("SUCCESS: Player {} completed puzzle and created rough jewel!",
                                player.getName().getString());
                        } else {
                            JewelCharms.LOGGER.error("ERROR: Could not deserialize JewelData from packet!");
                        }
                    } else {
                        JewelCharms.LOGGER.warn("Player {} claimed to solve puzzle but verification failed",
                            player.getName().getString());
                    }
                } else {
                    JewelCharms.LOGGER.info("Player {} failed the puzzle", player.getName().getString());
                    // Materials remain in slots, player can try again
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
                        JewelCharms.LOGGER.info("Creating polished jewel in output slot...");
                        // Create polished jewel and place in output slot
                        ItemStack polishedJewel = new ItemStack(ModItems.JEWEL.get());
                        jewelData.saveToItemStack(polishedJewel);
                        menu.setOutputItem(polishedJewel);

                        // Clear the input slot
                        menu.clearInputSlot();

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

package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.init.ModItems;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.util.JewelData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when player skips the puzzle for XP.
 */
public class PuzzleSkipPacket {
    private final BlockPos pos;

    public PuzzleSkipPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(PuzzleSkipPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
    }

    public static PuzzleSkipPacket decode(FriendlyByteBuf buffer) {
        return new PuzzleSkipPacket(buffer.readBlockPos());
    }

    public static void handle(PuzzleSkipPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (player.containerMenu instanceof JewelCreationStationMenu menu) {
                ItemStack outputJewel = menu.getOutputSlot().getItem();

                if (!outputJewel.isEmpty() && outputJewel.getItem() == ModItems.JEWEL.get()) {
                    JewelData jewelData = JewelData.fromItemStack(outputJewel);
                    if (jewelData != null) {
                        int xpCost = jewelData.getRarity().getSkipCost();

                        // Check if player has enough XP
                        int playerXp = getPlayerTotalXp(player);
                        if (playerXp >= xpCost) {
                            // Deduct XP
                            subtractXp(player, xpCost);

                            // Convert to rough jewel
                            ItemStack roughJewel = new ItemStack(ModItems.ROUGH_JEWEL.get());
                            jewelData.saveToItemStack(roughJewel);
                            menu.setOutputJewel(roughJewel);

                            JewelCharms.LOGGER.info("Player {} skipped puzzle for {} XP",
                                player.getName().getString(), xpCost);
                        } else {
                            JewelCharms.LOGGER.warn("Player {} tried to skip puzzle but doesn't have enough XP",
                                player.getName().getString());
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    private static int getPlayerTotalXp(ServerPlayer player) {
        int level = player.experienceLevel;
        int xp = Math.round(player.experienceProgress * player.getXpNeededForNextLevel());

        int totalXp = xp;
        for (int i = 0; i < level; i++) {
            totalXp += getXpForLevel(i);
        }
        return totalXp;
    }

    private static int getXpForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    private static void subtractXp(ServerPlayer player, int amount) {
        int totalXp = getPlayerTotalXp(player);
        int newTotalXp = Math.max(0, totalXp - amount);

        // Reset player XP
        player.experienceLevel = 0;
        player.experienceProgress = 0.0f;
        player.totalExperience = 0;

        // Add back remaining XP
        player.giveExperiencePoints(newTotalXp);
    }
}

package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.util.JewelCreationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class MinigameCompletePacket {
    private final BlockPos pos;
    private final boolean success;

    public MinigameCompletePacket(BlockPos pos, boolean success) {
        this.pos = pos;
        this.success = success;
    }

    public static void encode(MinigameCompletePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeBoolean(packet.success);
    }

    public static MinigameCompletePacket decode(FriendlyByteBuf buffer) {
        return new MinigameCompletePacket(
            buffer.readBlockPos(),
            buffer.readBoolean()
        );
    }

    public static void handle(MinigameCompletePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (player.containerMenu instanceof JewelCreationStationMenu menu) {
                if (packet.success) {
                    // Get materials from the menu
                    List<ItemStack> materials = menu.getMaterialStacks();

                    if (!materials.isEmpty()) {
                        // Create jewel
                        ItemStack jewel = JewelCreationHelper.createJewel(materials);

                        if (!jewel.isEmpty()) {
                            // Set output
                            menu.setOutputJewel(jewel);

                            // Consume materials
                            menu.clearMaterialSlots();

                            JewelCharms.LOGGER.info("Player {} successfully created a jewel", player.getName().getString());
                        }
                    }
                } else {
                    JewelCharms.LOGGER.info("Player {} failed the minigame", player.getName().getString());
                }
            }
        });
        context.setPacketHandled(true);
    }
}

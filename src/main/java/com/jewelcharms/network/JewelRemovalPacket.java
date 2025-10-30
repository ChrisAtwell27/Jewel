package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.JewelCreationStationMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server to request jewel removal from a tool.
 */
public class JewelRemovalPacket {

    public JewelRemovalPacket() {
        // No data needed, just triggers the removal
    }

    public static void encode(JewelRemovalPacket packet, FriendlyByteBuf buffer) {
        // No data to encode
    }

    public static JewelRemovalPacket decode(FriendlyByteBuf buffer) {
        return new JewelRemovalPacket();
    }

    public static void handle(JewelRemovalPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                JewelCharms.LOGGER.error("JewelRemovalPacket: Player is null!");
                return;
            }

            // Handle jewel removal if player is in the creation station menu
            if (player.containerMenu instanceof JewelCreationStationMenu menu) {
                JewelCharms.LOGGER.info("Processing jewel removal for player: {}", player.getName().getString());
                menu.handleJewelRemoval();
            } else {
                JewelCharms.LOGGER.error("ERROR: Player is NOT in JewelCreationStationMenu! Menu type: {}",
                    player.containerMenu.getClass().getSimpleName());
            }
        });
        context.setPacketHandled(true);
    }
}

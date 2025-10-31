package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.effect.GravityTetherTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when player presses the Gravity Tether collect key
 */
public class GravityTetherCollectPacket {

    public GravityTetherCollectPacket() {
        // No data needed
    }

    public static void encode(GravityTetherCollectPacket packet, FriendlyByteBuf buffer) {
        // No data to encode
    }

    public static GravityTetherCollectPacket decode(FriendlyByteBuf buffer) {
        return new GravityTetherCollectPacket();
    }

    public static void handle(GravityTetherCollectPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                JewelCharms.LOGGER.error("GravityTetherCollectPacket: Player is null!");
                return;
            }

            // Collect all orbiting items
            boolean collected = GravityTetherTracker.collectAll(player);

            if (collected) {
                player.displayClientMessage(
                    Component.literal("Collected all orbiting items!")
                        .withStyle(net.minecraft.ChatFormatting.GREEN),
                    true
                );
                JewelCharms.LOGGER.info("Player {} collected orbiting items", player.getName().getString());
            } else {
                player.displayClientMessage(
                    Component.literal("No orbiting items to collect")
                        .withStyle(net.minecraft.ChatFormatting.GRAY),
                    true
                );
            }
        });
        context.setPacketHandled(true);
    }
}

package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(JewelCharms.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.messageBuilder(MinigameClickPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(MinigameClickPacket::encode)
                .decoder(MinigameClickPacket::decode)
                .consumerMainThread(MinigameClickPacket::handle)
                .add();

        CHANNEL.messageBuilder(MinigameStartPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(MinigameStartPacket::encode)
                .decoder(MinigameStartPacket::decode)
                .consumerMainThread(MinigameStartPacket::handle)
                .add();

        CHANNEL.messageBuilder(MinigameCompletePacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(MinigameCompletePacket::encode)
                .decoder(MinigameCompletePacket::decode)
                .consumerMainThread(MinigameCompletePacket::handle)
                .add();

        CHANNEL.messageBuilder(PuzzleCompletePacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PuzzleCompletePacket::encode)
                .decoder(PuzzleCompletePacket::decode)
                .consumerMainThread(PuzzleCompletePacket::handle)
                .add();

        CHANNEL.messageBuilder(PuzzleSkipPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PuzzleSkipPacket::encode)
                .decoder(PuzzleSkipPacket::decode)
                .consumerMainThread(PuzzleSkipPacket::handle)
                .add();

        CHANNEL.messageBuilder(JewelRemovalPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(JewelRemovalPacket::encode)
                .decoder(JewelRemovalPacket::decode)
                .consumerMainThread(JewelRemovalPacket::handle)
                .add();

        CHANNEL.messageBuilder(GravityTetherCollectPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(GravityTetherCollectPacket::encode)
                .decoder(GravityTetherCollectPacket::decode)
                .consumerMainThread(GravityTetherCollectPacket::handle)
                .add();

        JewelCharms.LOGGER.info("Registered network packets");
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}

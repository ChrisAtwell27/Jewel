package com.jewelcharms.network;

import com.jewelcharms.client.screen.JewelCreationStationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MinigameStartPacket {

    public MinigameStartPacket() {
    }

    public static void encode(MinigameStartPacket packet, FriendlyByteBuf buffer) {
        // No data to encode
    }

    public static MinigameStartPacket decode(FriendlyByteBuf buffer) {
        return new MinigameStartPacket();
    }

    public static void handle(MinigameStartPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().screen instanceof JewelCreationStationScreen screen) {
                    screen.startMinigame();
                }
            });
        });
        context.setPacketHandled(true);
    }
}

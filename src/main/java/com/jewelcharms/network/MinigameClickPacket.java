package com.jewelcharms.network;

import com.jewelcharms.JewelCharms;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MinigameClickPacket {
    private final BlockPos pos;
    private final int gridX;
    private final int gridY;
    private final boolean correctClick;

    public MinigameClickPacket(BlockPos pos, int gridX, int gridY, boolean correctClick) {
        this.pos = pos;
        this.gridX = gridX;
        this.gridY = gridY;
        this.correctClick = correctClick;
    }

    public static void encode(MinigameClickPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeInt(packet.gridX);
        buffer.writeInt(packet.gridY);
        buffer.writeBoolean(packet.correctClick);
    }

    public static MinigameClickPacket decode(FriendlyByteBuf buffer) {
        return new MinigameClickPacket(
            buffer.readBlockPos(),
            buffer.readInt(),
            buffer.readInt(),
            buffer.readBoolean()
        );
    }

    public static void handle(MinigameClickPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Server-side handling
            JewelCharms.LOGGER.debug("Received minigame click at ({}, {}) - correct: {}",
                packet.gridX, packet.gridY, packet.correctClick);
        });
        context.setPacketHandled(true);
    }
}

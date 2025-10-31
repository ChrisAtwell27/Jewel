package com.jewelcharms.client;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.network.GravityTetherCollectPacket;
import com.jewelcharms.network.ModNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final String CATEGORY = "key.jewelcharms.category";

    public static final KeyMapping GRAVITY_TETHER_COLLECT = new KeyMapping(
        "key.jewelcharms.collect_gravity_tether",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_G,  // Default to G key
        CATEGORY
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(GRAVITY_TETHER_COLLECT);
        JewelCharms.LOGGER.info("Registered key bindings");
    }

    @Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, value = Dist.CLIENT)
    public static class KeyInputHandler {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (GRAVITY_TETHER_COLLECT.consumeClick()) {
                // Send packet to server
                ModNetwork.sendToServer(new GravityTetherCollectPacket());
            }
        }
    }
}

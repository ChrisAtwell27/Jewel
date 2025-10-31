package com.jewelcharms.client;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.client.gui.JewelEffectHUD;
import com.jewelcharms.client.renderer.JeweledItemGlowRenderer;
import com.jewelcharms.client.sound.JewelSoundManager;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles client-side events for visual effects
 */
@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    private static final JewelEffectHUD HUD_INSTANCE = new JewelEffectHUD();

    /**
     * Update animations every client tick
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Update glow renderer animation
            JeweledItemGlowRenderer.tick();

            // Play ambient shimmer for held jeweled items
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
                ItemStack heldItem = mc.player.getMainHandItem();
                if (!heldItem.isEmpty() && !ToolJewelData.getAttachedJewels(heldItem).isEmpty()) {
                    // Small chance to play ambient shimmer sound
                    JewelSoundManager.playAmbientShimmer(mc.player, 0.001f); // 0.1% chance per tick
                }
            }
        }
    }

    /**
     * Register GUI overlays on the MOD bus
     */
    @Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEventHandler {

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            // Register HUD overlay
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "jewel_effects", HUD_INSTANCE);
        }
    }

    /**
     * Add glow effects to items in frames
     */
    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && !ToolJewelData.getAttachedJewels(stack).isEmpty()) {
            // Would add glow rendering here
            // Note: Actual implementation would need proper render context
        }
    }
}
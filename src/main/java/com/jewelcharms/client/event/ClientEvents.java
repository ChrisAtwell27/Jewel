package com.jewelcharms.client.event;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.client.renderer.ToolJewelRenderer;
import com.jewelcharms.init.ModItems;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.JewelRarity;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.*;
import com.mojang.math.Axis;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    private static int particleTickCounter = 0;

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty() || ToolJewelData.getJewelCount(stack) == 0) {
            return;
        }

        ToolJewelRenderer.renderJewelOverlays(
            stack,
            event.getPoseStack(),
            event.getMultiBufferSource(),
            event.getPackedLight(),
            0
        );
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty() || ToolJewelData.getJewelCount(stack) == 0) {
            return;
        }

        // Render jewel overlays on held items
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // Position the overlay on the item
        poseStack.translate(0.5, 0.5, 0.0);

        ToolJewelRenderer.renderJewelOverlays(
            stack,
            poseStack,
            event.getMultiBufferSource(),
            event.getPackedLight(),
            net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;

        if (player == null || level == null) {
            return;
        }

        // Throttle particle spawning (every 5 ticks = 4 times per second)
        particleTickCounter++;
        if (particleTickCounter < 5) {
            return;
        }
        particleTickCounter = 0;

        // Check main hand for rare jewels
        spawnParticlesForJewel(player, level, player.getMainHandItem());
        // Check offhand for rare jewels
        spawnParticlesForJewel(player, level, player.getOffhandItem());
    }

    private static void spawnParticlesForJewel(Player player, Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        // Check if it's a jewel item
        if (stack.getItem() == ModItems.JEWEL.get()) {
            JewelData jewelData = JewelData.fromItemStack(stack);
            if (jewelData != null && jewelData.getRarity().hasParticles()) {
                spawnRarityParticles(player, level, jewelData.getRarity());
            }
        }

        // Check if it's a tool with attached jewels
        if (ToolJewelData.getJewelCount(stack) > 0) {
            java.util.List<ToolJewelData.AttachedJewel> attachedJewels = ToolJewelData.getAttachedJewels(stack);
            for (ToolJewelData.AttachedJewel attachedJewel : attachedJewels) {
                // Reconstruct JewelData to check rarity
                JewelData jewelData = new JewelData(
                    attachedJewel.getMaterials(),
                    attachedJewel.getEffects(),
                    attachedJewel.getColor()
                );
                if (jewelData.getRarity().hasParticles()) {
                    spawnRarityParticles(player, level, jewelData.getRarity());
                    break; // Only spawn particles once per item
                }
            }
        }
    }

    private static void spawnRarityParticles(Player player, Level level, JewelRarity rarity) {
        double x = player.getX() + (level.random.nextDouble() - 0.5) * 0.8;
        double y = player.getY() + player.getBbHeight() * 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
        double z = player.getZ() + (level.random.nextDouble() - 0.5) * 0.8;

        double velocityX = (level.random.nextDouble() - 0.5) * 0.02;
        double velocityY = level.random.nextDouble() * 0.05;
        double velocityZ = (level.random.nextDouble() - 0.5) * 0.02;

        if (rarity == JewelRarity.EPIC) {
            // Purple particles for EPIC
            level.addParticle(ParticleTypes.END_ROD, x, y, z, velocityX, velocityY, velocityZ);
        } else if (rarity == JewelRarity.LEGENDARY) {
            // Dragon breath particles for LEGENDARY
            level.addParticle(ParticleTypes.DRAGON_BREATH, x, y, z, velocityX, velocityY, velocityZ);

            // Occasionally add enchantment glint particles for extra flair
            if (level.random.nextFloat() < 0.3f) {
                level.addParticle(ParticleTypes.ENCHANT, x, y, z, velocityX, velocityY, velocityZ);
            }
        }
    }
}

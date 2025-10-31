package com.jewelcharms.client.renderer;

import com.jewelcharms.util.ToolJewelData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Renders glowing and animated effects for tools with jewels
 */
@OnlyIn(Dist.CLIENT)
public class JeweledItemGlowRenderer {

    private static float animationTime = 0;

    /**
     * Called every client tick to update animation
     */
    public static void tick() {
        animationTime += 0.05f;
        if (animationTime > 360) {
            animationTime -= 360;
        }
    }

    /**
     * Render glow effect for jeweled items
     */
    public static void renderGlow(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                                   MultiBufferSource buffer, int light, int overlay) {

        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(stack);
        if (jewels.isEmpty()) {
            return;
        }

        // Calculate glow intensity based on number and rarity of jewels
        float glowIntensity = calculateGlowIntensity(jewels);

        // Render multiple layers for glow effect
        for (int layer = 0; layer < 3; layer++) {
            float scale = 1.0f + (layer * 0.05f);
            float alpha = glowIntensity * (0.3f - layer * 0.1f);

            if (alpha > 0) {
                renderGlowLayer(stack, context, poseStack, buffer, scale, alpha, layer);
            }
        }

        // Add sparkle particles
        if (context == ItemDisplayContext.GUI) {
            renderSparkles(poseStack, buffer, jewels);
        }
    }

    private static void renderGlowLayer(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                                        MultiBufferSource buffer, float scale, float alpha, int layer) {
        poseStack.pushPose();

        // Scale up slightly for glow
        poseStack.scale(scale, scale, scale);

        // Get glow color based on jewels
        int glowColor = getGlowColor(ToolJewelData.getAttachedJewels(stack));
        float r = ((glowColor >> 16) & 0xFF) / 255.0f;
        float g = ((glowColor >> 8) & 0xFF) / 255.0f;
        float b = (glowColor & 0xFF) / 255.0f;

        // Animate glow
        float pulse = (float) Math.sin(animationTime + layer) * 0.1f + 0.9f;
        alpha *= pulse;

        // Render with additive blending for glow effect
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucent());

        // Would need custom render here - simplified for example
        // In practice, would render the item model with glow shader

        poseStack.popPose();
    }

    private static void renderSparkles(PoseStack poseStack, MultiBufferSource buffer, List<ToolJewelData.AttachedJewel> jewels) {
        VertexConsumer sparkleConsumer = buffer.getBuffer(RenderType.glint());

        for (int i = 0; i < jewels.size() * 3; i++) {
            // Calculate sparkle position with animation
            float sparkleTime = animationTime * 2 + i * 137.5f; // Golden angle for distribution
            float x = (float) Math.sin(sparkleTime) * 8;
            float y = (float) Math.cos(sparkleTime * 1.3f) * 8;

            // Fade in and out
            float sparkleAlpha = (float) Math.sin(sparkleTime * 3) * 0.5f + 0.5f;

            if (sparkleAlpha > 0.1f) {
                renderSparkle(poseStack, sparkleConsumer, x, y, sparkleAlpha);
            }
        }
    }

    private static void renderSparkle(PoseStack poseStack, VertexConsumer consumer, float x, float y, float alpha) {
        Matrix4f matrix = poseStack.last().pose();

        float size = 1.0f;
        int light = 0xF000F0; // Full bright

        // Render quad for sparkle
        consumer.vertex(matrix, x - size, y - size, 0).color(1.0f, 1.0f, 1.0f, alpha)
                .uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(0, 0, 1).endVertex();

        consumer.vertex(matrix, x - size, y + size, 0).color(1.0f, 1.0f, 1.0f, alpha)
                .uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(0, 0, 1).endVertex();

        consumer.vertex(matrix, x + size, y + size, 0).color(1.0f, 1.0f, 1.0f, alpha)
                .uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(0, 0, 1).endVertex();

        consumer.vertex(matrix, x + size, y - size, 0).color(1.0f, 1.0f, 1.0f, alpha)
                .uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(0, 0, 1).endVertex();
    }

    private static float calculateGlowIntensity(List<ToolJewelData.AttachedJewel> jewels) {
        float intensity = 0;
        for (ToolJewelData.AttachedJewel jewel : jewels) {
            // Calculate intensity based on number of effects
            // More effects = stronger glow
            int effectCount = jewel.getEffects().size();
            intensity += Math.min(effectCount * 0.3f, 1.0f);
        }
        return Math.min(intensity, 1.0f);
    }

    private static int getGlowColor(List<ToolJewelData.AttachedJewel> jewels) {
        if (jewels.isEmpty()) {
            return 0xFFFFFF;
        }

        // Blend jewel colors
        float r = 0, g = 0, b = 0;
        int count = 0;

        for (ToolJewelData.AttachedJewel jewel : jewels) {
            int color = jewel.getColor();
            r += ((color >> 16) & 0xFF);
            g += ((color >> 8) & 0xFF);
            b += (color & 0xFF);
            count++;
        }

        if (count > 0) {
            r /= count;
            g /= count;
            b /= count;
        } else {
            r = g = b = 255;
        }

        return ((int)r << 16) | ((int)g << 8) | (int)b;
    }

    /**
     * Render subtle glow aura for jeweled items (no enchant glint)
     */
    public static void renderJewelAura(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                                        int light, int overlay) {
        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(stack);
        if (jewels.isEmpty()) {
            return;
        }

        // Get glow color based on jewels
        int glowColor = getGlowColor(jewels);
        float r = ((glowColor >> 16) & 0xFF) / 255.0f;
        float g = ((glowColor >> 8) & 0xFF) / 255.0f;
        float b = (glowColor & 0xFF) / 255.0f;

        // Render subtle colored aura instead of enchant glint
        // This would render a soft colored outline or glow effect
        // without using the vanilla enchantment glint texture
    }
}
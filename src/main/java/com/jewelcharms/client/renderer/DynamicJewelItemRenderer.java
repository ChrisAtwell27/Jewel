package com.jewelcharms.client.renderer;

import com.jewelcharms.util.JewelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom renderer for jewel items that generates unique textures based on materials.
 */
public class DynamicJewelItemRenderer {

    /**
     * Render a jewel item with dynamically generated texture
     */
    public static void renderJewel(ItemStack stack, ItemDisplayContext displayContext,
                                     PoseStack poseStack, MultiBufferSource buffer,
                                     int combinedLight, int combinedOverlay, boolean isPolished) {

        // Get jewel data
        JewelData jewelData = JewelData.fromItemStack(stack);
        if (jewelData == null) {
            return;
        }

        // Get material colors
        List<Integer> colors = new ArrayList<>();
        for (String material : jewelData.getMaterials()) {
            int color = getMaterialColor(material);
            colors.add(color);
        }

        if (colors.isEmpty()) {
            colors.add(0xFFFFFF);
        }

        // Generate or get cached texture
        ResourceLocation texture = DynamicJewelTextureGenerator.getOrCreateTexture(colors, isPolished);

        // Setup rendering
        poseStack.pushPose();

        // Apply transforms based on display context
        applyTransforms(poseStack, displayContext);

        // Get vertex consumer for rendering
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));

        // Render a simple quad with the dynamic texture
        renderQuad(vertexConsumer, poseStack, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private static void applyTransforms(PoseStack poseStack, ItemDisplayContext context) {
        switch (context) {
            case GUI:
                // GUI items: centered in inventory slots
                poseStack.translate(0.5, 0.5, 0);
                poseStack.scale(1, -1, 1);
                poseStack.translate(-0.5, -0.5, 0);
                break;
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                // First person: held item positioned correctly
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(1, -1, 1);
                poseStack.translate(-0.5, -0.5, -0.5);
                break;
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                // Third person: item in other player's hand
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(1, -1, 1);
                poseStack.translate(-0.5, -0.5, -0.5);
                break;
            case GROUND:
                // Item on ground
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.5f, -0.5f, 0.5f);
                poseStack.translate(-0.5, -0.5, -0.5);
                poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), 90));
                break;
            case FIXED:
                // Item frame: centered
                poseStack.translate(0.5, 0.5, 0);
                poseStack.scale(1, -1, 1);
                poseStack.translate(-0.5, -0.5, 0);
                break;
            default:
                poseStack.translate(0.5, 0.5, 0);
                poseStack.scale(1, -1, 1);
                poseStack.translate(-0.5, -0.5, 0);
                break;
        }
    }

    private static void renderQuad(VertexConsumer consumer, PoseStack poseStack,
                                    int light, int overlay) {
        Matrix4f matrix = poseStack.last().pose();

        // Render 1x1 quad centered at origin
        float min = -0.5f;
        float max = 0.5f;

        // Front face quad
        consumer.vertex(matrix, min, min, 0).color(255, 255, 255, 255)
                .uv(0, 1).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, max, min, 0).color(255, 255, 255, 255)
                .uv(1, 1).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, max, max, 0).color(255, 255, 255, 255)
                .uv(1, 0).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, min, max, 0).color(255, 255, 255, 255)
                .uv(0, 0).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
    }

    private static int getMaterialColor(String material) {
        return switch (material.toLowerCase()) {
            case "diamond" -> 0xB9F2FF;
            case "emerald" -> 0x50C878;
            case "gold" -> 0xFFD700;
            case "iron" -> 0xD8D8D8;
            case "copper" -> 0xB87333;
            case "quartz" -> 0xECE5E1;
            case "lapis" -> 0x1E90FF;
            case "redstone" -> 0xDC143C;
            case "amethyst" -> 0x9966CC;
            case "netherite" -> 0x4A4A4A;
            default -> 0xFFFFFF;
        };
    }
}

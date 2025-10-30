package com.jewelcharms.client.renderer;

import com.jewelcharms.util.ToolJewelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;

public class ToolJewelRenderer {

    public static void renderJewelOverlays(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(stack);

        if (jewels.isEmpty()) {
            return;
        }

        // Render jewel overlays
        for (int i = 0; i < jewels.size(); i++) {
            ToolJewelData.AttachedJewel jewel = jewels.get(i);
            int color = jewel.getColor();

            float offsetX = i == 0 ? 0.1f : -0.1f; // Opposite sides

            renderJewelGlow(poseStack, buffer, color, offsetX, combinedLight, combinedOverlay);
        }
    }

    private static void renderJewelGlow(PoseStack poseStack, MultiBufferSource buffer, int color, float offsetX, int combinedLight, int combinedOverlay) {
        // Extract RGB from color
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 0.8f; // More opaque for better visibility

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        // Render a larger, more visible gem overlay
        float size = 0.08f; // Increased size
        float y = 0.5f;
        float z = 0.02f; // Slightly more forward

        // Front face - main gem
        vertexConsumer.vertex(matrix, offsetX - size, y - size, z).color(r, g, b, a).uv(0, 0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        vertexConsumer.vertex(matrix, offsetX + size, y - size, z).color(r, g, b, a).uv(1, 0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        vertexConsumer.vertex(matrix, offsetX + size, y + size, z).color(r, g, b, a).uv(1, 1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        vertexConsumer.vertex(matrix, offsetX - size, y + size, z).color(r, g, b, a).uv(0, 1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();

        // Add a bright center highlight for sparkle effect
        float centerSize = size * 0.3f;
        float centerZ = z + 0.001f;
        vertexConsumer.vertex(matrix, offsetX - centerSize, y - centerSize, centerZ).color(1.0f, 1.0f, 1.0f, a * 0.8f).uv(0, 0).overlayCoords(combinedOverlay).uv2(15728880).normal(0, 0, 1).endVertex();
        vertexConsumer.vertex(matrix, offsetX + centerSize, y - centerSize, centerZ).color(1.0f, 1.0f, 1.0f, a * 0.8f).uv(1, 0).overlayCoords(combinedOverlay).uv2(15728880).normal(0, 0, 1).endVertex();
        vertexConsumer.vertex(matrix, offsetX + centerSize, y + centerSize, centerZ).color(1.0f, 1.0f, 1.0f, a * 0.8f).uv(1, 1).overlayCoords(combinedOverlay).uv2(15728880).normal(0, 0, 1).endVertex();
        vertexConsumer.vertex(matrix, offsetX - centerSize, y + centerSize, centerZ).color(1.0f, 1.0f, 1.0f, a * 0.8f).uv(0, 1).overlayCoords(combinedOverlay).uv2(15728880).normal(0, 0, 1).endVertex();
    }
}

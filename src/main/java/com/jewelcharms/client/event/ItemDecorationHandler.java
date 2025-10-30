package com.jewelcharms.client.event;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ItemDecorationHandler {

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        // Register decorators for all tool types that can have jewels
        // We need to register for each specific item type
        net.minecraft.world.item.Item[] tools = {
            net.minecraft.world.item.Items.DIAMOND_SWORD,
            net.minecraft.world.item.Items.DIAMOND_PICKAXE,
            net.minecraft.world.item.Items.DIAMOND_AXE,
            net.minecraft.world.item.Items.DIAMOND_SHOVEL,
            net.minecraft.world.item.Items.DIAMOND_HOE,
            net.minecraft.world.item.Items.NETHERITE_SWORD,
            net.minecraft.world.item.Items.NETHERITE_PICKAXE,
            net.minecraft.world.item.Items.NETHERITE_AXE,
            net.minecraft.world.item.Items.NETHERITE_SHOVEL,
            net.minecraft.world.item.Items.NETHERITE_HOE,
            net.minecraft.world.item.Items.IRON_SWORD,
            net.minecraft.world.item.Items.IRON_PICKAXE,
            net.minecraft.world.item.Items.IRON_AXE,
            net.minecraft.world.item.Items.IRON_SHOVEL,
            net.minecraft.world.item.Items.IRON_HOE,
            net.minecraft.world.item.Items.GOLDEN_SWORD,
            net.minecraft.world.item.Items.GOLDEN_PICKAXE,
            net.minecraft.world.item.Items.GOLDEN_AXE,
            net.minecraft.world.item.Items.GOLDEN_SHOVEL,
            net.minecraft.world.item.Items.GOLDEN_HOE
        };

        for (net.minecraft.world.item.Item tool : tools) {
            event.register(tool, (guiGraphics, font, stack, xOffset, yOffset) -> {
                return renderJewelDots(guiGraphics, stack, xOffset, yOffset);
            });
        }
    }

    private static boolean renderJewelDots(GuiGraphics guiGraphics, ItemStack stack, int xOffset, int yOffset) {
        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(stack);

        if (jewels.isEmpty()) {
            return false;
        }

        // Draw small colored dots for each jewel
        for (int i = 0; i < jewels.size(); i++) {
            ToolJewelData.AttachedJewel jewel = jewels.get(i);
            int color = jewel.getColor();

            // Position dots at bottom corners
            int dotX = xOffset + (i == 0 ? 2 : 11);
            int dotY = yOffset + 11;
            int dotSize = 3;

            // Draw dot with border
            guiGraphics.fill(dotX - 1, dotY - 1, dotX + dotSize + 1, dotY + dotSize + 1, 0xFF000000);
            guiGraphics.fill(dotX, dotY, dotX + dotSize, dotY + dotSize, 0xFF000000 | color);

            // Add white highlight for sparkle
            guiGraphics.fill(dotX, dotY, dotX + 1, dotY + 1, 0xFFFFFFFF);
        }

        return false; // Return false to allow other decorations
    }
}

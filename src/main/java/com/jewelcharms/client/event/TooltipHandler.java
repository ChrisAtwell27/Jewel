package com.jewelcharms.client.event;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) {
            return;
        }

        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(stack);

        if (jewels.isEmpty()) {
            return;
        }

        List<Component> tooltip = event.getToolTip();

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.jewelcharms.tool.jewels")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        for (int i = 0; i < jewels.size(); i++) {
            ToolJewelData.AttachedJewel jewel = jewels.get(i);

            tooltip.add(Component.literal("  Jewel " + (i + 1) + ":")
                    .withStyle(ChatFormatting.YELLOW));

            // Materials
            String materialsStr = String.join(", ", jewel.getMaterials());
            tooltip.add(Component.literal("    Materials: " + materialsStr)
                    .withStyle(ChatFormatting.GRAY));

            // Effects
            jewel.getEffects().forEach((effect, strength) -> {
                tooltip.add(Component.literal("    • " + effect.getDisplayName() + " " + strength)
                        .withStyle(ChatFormatting.AQUA));
            });
        }
    }
}

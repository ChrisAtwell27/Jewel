package com.jewelcharms.item;

import com.jewelcharms.util.JewelData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class JewelItem extends Item {

    public JewelItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        JewelData jewelData = JewelData.fromItemStack(stack);

        if (jewelData != null) {
            // Display materials used
            if (!jewelData.getMaterials().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.jewelcharms.jewel.materials",
                    jewelData.getMaterialsString()).withStyle(ChatFormatting.YELLOW));
            }

            // Display effects
            if (!jewelData.getEffects().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.jewelcharms.jewel.effects")
                    .withStyle(ChatFormatting.AQUA));
                jewelData.getEffects().forEach((effect, strength) -> {
                    tooltip.add(Component.literal("  • " + effect.getDisplayName() + " " +
                        strength).withStyle(ChatFormatting.GRAY));
                });
            }
        }
    }

    @Override
    public int getBarColor(ItemStack stack) {
        JewelData jewelData = JewelData.fromItemStack(stack);
        if (jewelData != null) {
            return jewelData.getColor();
        }
        return super.getBarColor(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return JewelData.fromItemStack(stack) != null;
    }
}

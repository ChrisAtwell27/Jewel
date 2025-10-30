package com.jewelcharms.item;

import com.jewelcharms.util.JewelData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Rough Jewel item created after completing the slide puzzle.
 * Must be polished at a Polish Station to become a usable jewel.
 */
public class RoughJewelItem extends Item {

    public RoughJewelItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        JewelData jewelData = JewelData.fromItemStack(stack);

        if (jewelData != null) {
            // Display rarity
            tooltip.add(Component.literal(jewelData.getRarity().getDisplayName() + " (Rough)")
                .withStyle(jewelData.getRarity().getChatFormatting()));

            tooltip.add(Component.literal("Requires polishing before use")
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

            // Display materials used
            if (!jewelData.getMaterials().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.jewelcharms.jewel.materials",
                    jewelData.getMaterialsString()).withStyle(ChatFormatting.YELLOW));
            }

            // Display effects (grayed out since unusable)
            if (!jewelData.getEffects().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.jewelcharms.jewel.effects")
                    .withStyle(ChatFormatting.DARK_GRAY));
                jewelData.getEffects().forEach((effect, strength) -> {
                    tooltip.add(Component.literal("  • " + effect.getDisplayName() + " " +
                        strength).withStyle(ChatFormatting.DARK_GRAY));
                });
            }
        }
    }

    @Override
    public int getBarColor(ItemStack stack) {
        JewelData jewelData = JewelData.fromItemStack(stack);
        if (jewelData != null) {
            // Darken the color to indicate it's rough/unfinished
            int color = jewelData.getColor();
            int r = ((color >> 16) & 0xFF) / 2;
            int g = ((color >> 8) & 0xFF) / 2;
            int b = (color & 0xFF) / 2;
            return (r << 16) | (g << 8) | b;
        }
        return super.getBarColor(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // Don't show durability bar for rough jewels
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Disable enchantment glint for rough jewels
        return false;
    }
}

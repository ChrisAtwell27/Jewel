package com.jewelcharms.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

public class GuidebookItem extends Item {
    private static final ResourceLocation BOOK_ID = new ResourceLocation("jewelcharms", "guide");

    public GuidebookItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            com.jewelcharms.JewelCharms.LOGGER.info("Attempting to open Jewel Charms guidebook with ID: " + BOOK_ID);

            try {
                PatchouliAPI.get().openBookGUI(BOOK_ID);
                com.jewelcharms.JewelCharms.LOGGER.info("Successfully opened guidebook");
            } catch (Exception e) {
                com.jewelcharms.JewelCharms.LOGGER.error("Failed to open guidebook: " + e.getMessage(), e);
                player.displayClientMessage(Component.literal("Error: Failed to open guidebook!"), true);
            }
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.jewelcharms.guidebook.desc"));
    }
}

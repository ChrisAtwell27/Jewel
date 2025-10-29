package com.jewelcharms.recipe;

import com.jewelcharms.init.ModItems;
import com.jewelcharms.init.ModRecipeTypes;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class JewelAttachmentRecipe implements Recipe<Container> {
    private final ResourceLocation id;

    public JewelAttachmentRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(Container container, Level level) {
        // Slot 0: Template (Jewel Socket Template)
        // Slot 1: Tool (base item)
        // Slot 2: Addition (Jewel)

        ItemStack template = container.getItem(0);
        ItemStack tool = container.getItem(1);
        ItemStack addition = container.getItem(2);

        // Check template
        if (!template.is(ModItems.JEWEL_SOCKET_TEMPLATE.get())) {
            return false;
        }

        // Check if tool is valid and can accept jewel
        if (tool.isEmpty() || !isValidTool(tool)) {
            return false;
        }

        // Check if tool can accept more jewels
        if (!ToolJewelData.canAttachJewel(tool)) {
            return false;
        }

        // Check if addition is a jewel
        if (!addition.is(ModItems.JEWEL.get())) {
            return false;
        }

        // Check if jewel has valid data
        return JewelData.fromItemStack(addition) != null;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack tool = container.getItem(1).copy();
        ItemStack jewel = container.getItem(2);

        JewelData jewelData = JewelData.fromItemStack(jewel);
        if (jewelData != null) {
            ToolJewelData.attachJewel(tool, jewelData);
        }

        return tool;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.JEWEL_ATTACHMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    private boolean isValidTool(ItemStack stack) {
        // Check if item is a vanilla tool
        return stack.isDamageableItem() && !stack.is(ModItems.JEWEL_CREATION_STATION_ITEM.get());
    }

    // Required for smithing table
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(ModItems.JEWEL_SOCKET_TEMPLATE.get());
    }

    public boolean isBaseIngredient(ItemStack stack) {
        return isValidTool(stack) && ToolJewelData.canAttachJewel(stack);
    }

    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(ModItems.JEWEL.get()) && JewelData.fromItemStack(stack) != null;
    }
}

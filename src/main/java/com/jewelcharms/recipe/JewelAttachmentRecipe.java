package com.jewelcharms.recipe;

import com.jewelcharms.init.ModItems;
import com.jewelcharms.init.ModRecipeTypes;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class JewelAttachmentRecipe extends SmithingTransformRecipe {
    private final ResourceLocation id;

    public JewelAttachmentRecipe(ResourceLocation id) {
        super(id,
            Ingredient.of(ModItems.JEWEL_SOCKET_TEMPLATE.get()),
            Ingredient.EMPTY,  // Will be checked dynamically
            Ingredient.of(ModItems.JEWEL.get()),
            ItemStack.EMPTY);
        this.id = id;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(ModItems.JEWEL_SOCKET_TEMPLATE.get());
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return isValidTool(stack) && ToolJewelData.canAttachJewel(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(ModItems.JEWEL.get()) && JewelData.fromItemStack(stack) != null;
    }

    @Override
    public ItemStack assemble(ItemStack template, ItemStack base, ItemStack addition, RegistryAccess registryAccess) {
        ItemStack result = base.copy();

        JewelData jewelData = JewelData.fromItemStack(addition);
        if (jewelData != null) {
            ToolJewelData.attachJewel(result, jewelData);
        }

        return result;
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
    public boolean isSpecial() {
        return true;
    }

    private boolean isValidTool(ItemStack stack) {
        // Check if item is a vanilla tool
        return stack.isDamageableItem() && !stack.is(ModItems.JEWEL_CREATION_STATION_ITEM.get());
    }
}

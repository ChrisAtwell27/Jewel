package com.jewelcharms.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class JewelAttachmentRecipeSerializer implements RecipeSerializer<JewelAttachmentRecipe> {

    @Override
    public JewelAttachmentRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        return new JewelAttachmentRecipe(recipeId);
    }

    @Override
    public JewelAttachmentRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        return new JewelAttachmentRecipe(recipeId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, JewelAttachmentRecipe recipe) {
        // No data to write
    }
}

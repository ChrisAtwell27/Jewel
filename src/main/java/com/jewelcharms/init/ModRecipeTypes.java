package com.jewelcharms.init;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.recipe.JewelAttachmentRecipe;
import com.jewelcharms.recipe.JewelAttachmentRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, JewelCharms.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, JewelCharms.MOD_ID);

    public static final RegistryObject<RecipeSerializer<JewelAttachmentRecipe>> JEWEL_ATTACHMENT_SERIALIZER = RECIPE_SERIALIZERS.register("jewel_attachment",
            JewelAttachmentRecipeSerializer::new);
}

package com.jewelcharms.recipe;

import com.jewelcharms.init.ModItems;
import com.jewelcharms.init.ModRecipeTypes;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import javax.annotation.Nonnull;

public class JewelAttachmentRecipe extends SmithingTransformRecipe {
    private final ResourceLocation id;

    public JewelAttachmentRecipe(ResourceLocation id) {
        super(id,
            Ingredient.of(ModItems.JEWEL_SOCKET_TEMPLATE.get()),
            // Accept common tools - will be checked more thoroughly in isBaseIngredient
            Ingredient.of(
                Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE,
                Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_HOE,
                Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE,
                Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE,
                Items.STONE_SWORD, Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_HOE,
                Items.WOODEN_SWORD, Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_SHOVEL, Items.WOODEN_HOE,
                Items.BOW, Items.CROSSBOW, Items.TRIDENT, Items.SHEARS, Items.FISHING_ROD
            ),
            Ingredient.of(ModItems.JEWEL.get()),
            ItemStack.EMPTY);
        this.id = id;
    }

    @Override
    public boolean matches(@Nonnull net.minecraft.world.Container container, @Nonnull net.minecraft.world.level.Level level) {
        // Container slots: 0 = template, 1 = base, 2 = addition
        if (container.getContainerSize() < 3) {
            com.jewelcharms.JewelCharms.LOGGER.info("Container size too small: {}", container.getContainerSize());
            return false;
        }

        ItemStack template = container.getItem(0);
        ItemStack base = container.getItem(1);
        ItemStack addition = container.getItem(2);

        boolean templateMatch = isTemplateIngredient(template);
        boolean baseMatch = isBaseIngredient(base);
        boolean additionMatch = isAdditionIngredient(addition);

        com.jewelcharms.JewelCharms.LOGGER.info("Recipe matching - Template: {} ({}), Base: {} ({}), Addition: {} ({})",
            template.getItem(), templateMatch,
            base.getItem(), baseMatch,
            addition.getItem(), additionMatch);

        return templateMatch && baseMatch && additionMatch;
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
        boolean isJewel = stack.is(ModItems.JEWEL.get());
        JewelData jewelData = JewelData.fromItemStack(stack);
        boolean hasData = jewelData != null;

        com.jewelcharms.JewelCharms.LOGGER.info("Addition check - Is jewel item: {}, Has JewelData: {}", isJewel, hasData);

        return isJewel && hasData;
    }

    @Override
    public ItemStack assemble(@Nonnull net.minecraft.world.Container container, @Nonnull RegistryAccess registryAccess) {
        ItemStack template = container.getItem(0);
        ItemStack base = container.getItem(1);
        ItemStack addition = container.getItem(2);

        com.jewelcharms.JewelCharms.LOGGER.info("Assemble called - Template: {}, Base: {}, Addition: {}",
            template.getItem(), base.getItem(), addition.getItem());

        ItemStack result = base.copy();

        JewelData jewelData = JewelData.fromItemStack(addition);
        if (jewelData != null) {
            ToolJewelData.attachJewel(result, jewelData);
            com.jewelcharms.JewelCharms.LOGGER.info("Jewel attached successfully! Result: {}", result);
        } else {
            com.jewelcharms.JewelCharms.LOGGER.warn("JewelData was null in assemble!");
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
    public net.minecraft.world.item.crafting.RecipeType<?> getType() {
        return net.minecraft.world.item.crafting.RecipeType.SMITHING;
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

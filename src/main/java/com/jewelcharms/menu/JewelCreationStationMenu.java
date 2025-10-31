package com.jewelcharms.menu;

import com.jewelcharms.init.ModItems;
import com.jewelcharms.init.ModMenuTypes;
import com.jewelcharms.util.JewelCreationHelper;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JewelCreationStationMenu extends AbstractContainerMenu {
    private static final int MATERIAL_SLOTS = 3;
    private static final int OUTPUT_SLOT = 3;
    private static final int REMOVAL_INPUT_SLOT = 4;
    private static final int REMOVAL_OUTPUT_1 = 5;
    private static final int REMOVAL_OUTPUT_2 = 6;

    private final Container container;
    private final BlockPos pos;
    public final net.minecraft.world.level.Level world;
    public final int x, y, z;
    public final net.minecraft.world.entity.player.Player entity;

    public JewelCreationStationMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, BlockPos.ZERO);
    }

    public JewelCreationStationMenu(int id, Inventory playerInventory, BlockPos pos) {
        super(ModMenuTypes.JEWEL_CREATION_STATION.get(), id);
        this.container = new SimpleContainer(7);
        this.pos = pos;
        this.world = playerInventory.player.level();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.entity = playerInventory.player;

        // Material input slots (slots 0-2) - aligned with 2nd, 4th, and 6th inventory columns
        // Column positions: 2nd = 8+18 = 26, 4th = 8+18*3 = 62, 6th = 8+18*5 = 98
        // Y position: 16
        this.addSlot(new MaterialSlot(container, 0, 26, 16));
        this.addSlot(new MaterialSlot(container, 1, 62, 16));
        this.addSlot(new MaterialSlot(container, 2, 98, 16));

        // Rough jewel output slot (slot 3) - aligned with 4th column = 62
        // Y position: 50
        this.addSlot(new OutputSlot(container, OUTPUT_SLOT, 62, 50));

        // Jewel removal input slot (slot 4) - aligned with 8th column = 8+18*7 = 134
        this.addSlot(new RemovalInputSlot(container, REMOVAL_INPUT_SLOT, 134, 16));

        // Jewel removal output slots (slots 5-6) - aligned with 7th and 9th columns
        // 7th column = 8+18*6 = 116, 9th column = 8+18*8 = 152
        this.addSlot(new OutputSlot(container, REMOVAL_OUTPUT_1, 116, 50));
        this.addSlot(new OutputSlot(container, REMOVAL_OUTPUT_2, 152, 50));

        // Player inventory (3 rows)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // Container has 7 slots (0-6), player inventory starts at index 7
            if (index < 7) {
                // Moving from container to player inventory
                if (!this.moveItemStackTo(slotStack, 7, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to container
                if (!this.moveItemStackTo(slotStack, 0, MATERIAL_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.container);
    }

    public List<ItemStack> getMaterialStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < MATERIAL_SLOTS; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    public void setOutputJewel(ItemStack jewel) {
        container.setItem(OUTPUT_SLOT, jewel);
    }

    public Slot getOutputSlot() {
        return this.slots.get(OUTPUT_SLOT);
    }

    public void clearMaterialSlots() {
        for (int i = 0; i < MATERIAL_SLOTS; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public void handleJewelRemoval() {
        ItemStack tool = container.getItem(REMOVAL_INPUT_SLOT);
        if (tool.isEmpty()) {
            return;
        }

        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(tool);
        if (jewels.isEmpty()) {
            return;
        }

        // Create jewel items from attached jewels
        for (int i = 0; i < Math.min(jewels.size(), 2); i++) {
            ToolJewelData.AttachedJewel jewel = jewels.get(i);
            ItemStack jewelItem = new ItemStack(ModItems.JEWEL.get());

            JewelData jewelData = new JewelData(jewel.getMaterials(), jewel.getEffects(), jewel.getColor());
            jewelData.saveToItemStack(jewelItem);

            if (i == 0) {
                container.setItem(REMOVAL_OUTPUT_1, jewelItem);
            } else {
                container.setItem(REMOVAL_OUTPUT_2, jewelItem);
            }
        }

        // Remove jewels from tool
        ToolJewelData.removeAllJewels(tool);
    }

    // Custom slots
    private static class MaterialSlot extends Slot {
        public MaterialSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return JewelCreationHelper.isValidMaterial(stack.getItem());
        }
    }

    private static class OutputSlot extends Slot {
        public OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false; // Output slot, no placing allowed
        }
    }

    private static class RemovalInputSlot extends Slot {
        public RemovalInputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Only allow tools with jewels
            return stack.getTag() != null && stack.getTag().contains("JewelCharms");
        }
    }
}

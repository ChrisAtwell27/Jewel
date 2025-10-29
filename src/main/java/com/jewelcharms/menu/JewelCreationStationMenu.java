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
    private static final int MATERIAL_SLOTS = 5;
    private static final int OUTPUT_SLOT = 5;
    private static final int REMOVAL_INPUT_SLOT = 6;
    private static final int REMOVAL_OUTPUT_1 = 7;
    private static final int REMOVAL_OUTPUT_2 = 8;

    private final Container container;
    private final BlockPos pos;

    public JewelCreationStationMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, BlockPos.ZERO);
    }

    public JewelCreationStationMenu(int id, Inventory playerInventory, BlockPos pos) {
        super(ModMenuTypes.JEWEL_CREATION_STATION.get(), id);
        this.container = new SimpleContainer(9);
        this.pos = pos;

        // Material input slots (5 slots)
        for (int i = 0; i < MATERIAL_SLOTS; i++) {
            this.addSlot(new MaterialSlot(container, i, 30 + i * 18, 35));
        }

        // Jewel output slot
        this.addSlot(new OutputSlot(container, OUTPUT_SLOT, 120, 75));

        // Jewel removal input slot
        this.addSlot(new RemovalInputSlot(container, REMOVAL_INPUT_SLOT, 30, 130));

        // Jewel removal output slots (for the 2 jewels)
        this.addSlot(new OutputSlot(container, REMOVAL_OUTPUT_1, 80, 130));
        this.addSlot(new OutputSlot(container, REMOVAL_OUTPUT_2, 110, 130));

        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 166 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 224));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < 9) {
                // Moving from container to player inventory
                if (!this.moveItemStackTo(slotStack, 9, this.slots.size(), true)) {
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

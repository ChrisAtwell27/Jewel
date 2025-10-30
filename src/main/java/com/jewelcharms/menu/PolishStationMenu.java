package com.jewelcharms.menu;

import com.jewelcharms.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PolishStationMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final Container container;
    private final BlockPos pos;

    public PolishStationMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, BlockPos.ZERO);
    }

    public PolishStationMenu(int id, Inventory playerInventory, BlockPos pos) {
        super(ModMenuTypes.POLISH_STATION.get(), id);
        this.container = new SimpleContainer(2);
        this.pos = pos;

        // Input slot (rough jewel) - top
        this.addSlot(new InputSlot(container, INPUT_SLOT, 80, 27));

        // Output slot (polished jewel) - bottom
        this.addSlot(new OutputSlot(container, OUTPUT_SLOT, 80, 57));

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

            if (index < 2) {
                // Moving from container to player inventory
                if (!this.moveItemStackTo(slotStack, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to container
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
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

    public ItemStack getInputItem() {
        return container.getItem(INPUT_SLOT);
    }

    public void setOutputItem(ItemStack stack) {
        container.setItem(OUTPUT_SLOT, stack);
    }

    public void clearInputSlot() {
        container.setItem(INPUT_SLOT, ItemStack.EMPTY);
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    // Custom slots
    private static class InputSlot extends Slot {
        public InputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Only allow rough jewels
            return stack.getItem().toString().contains("rough_jewel");
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
}

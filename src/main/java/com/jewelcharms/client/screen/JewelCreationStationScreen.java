package com.jewelcharms.client.screen;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.client.ui.*;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.network.ModNetwork;
import com.jewelcharms.network.PuzzleCompletePacket;
import com.jewelcharms.util.JewelCreationHelper;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.PuzzleState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class JewelCreationStationScreen extends AbstractContainerScreen<JewelCreationStationMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(JewelCharms.MOD_ID, "textures/gui/container/jewel_creation_station.png");
    // UI widgets
    private ButtonWidget createButton;
    private ButtonWidget removeButton;
    private LabelWidget statusLabel;
    private LabelWidget removalLabel;

    // Puzzle state
    private boolean puzzleActive = false;
    private PuzzleState puzzleState = null;
    private JewelData pendingJewelData = null;
    private int puzzleStartX = 0;
    private int puzzleStartY = 0;
    private int tileSize = 0;
    private int moveCount = 0;

    public JewelCreationStationScreen(JewelCreationStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 206; // Increased to accommodate removal section
        this.imageWidth = 176;
        this.inventoryLabelY = 113; // Adjusted for new inventory position
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = leftPos + imageWidth / 2;

        // Status label
        statusLabel = new LabelWidget(Component.literal("Insert Materials"), 0xFF404040);
        statusLabel.setAlignment(LabelWidget.Alignment.CENTER);
        statusLabel.setPosition(centerX - 70, topPos + 20);
        statusLabel.setWidth(140);

        // Create button (below material slots)
        createButton = new ButtonWidget(120, 20, Component.literal("Create Jewel"), this::startPuzzle);
        createButton.setPosition(leftPos + (imageWidth - 120) / 2, topPos + 68);
        createButton.setColors(0xFF5A5A5A, 0xFF7A7A7A, 0xFF3A3A3A);
        createButton.setTextColor(0xFFFFFFFF);

        // Removal section label
        removalLabel = new LabelWidget(Component.literal("Remove Jewels"), 0xFF404040);
        removalLabel.setAlignment(LabelWidget.Alignment.CENTER);
        removalLabel.setPosition(centerX - 70, topPos + 80);
        removalLabel.setWidth(140);

        // Remove button
        removeButton = new ButtonWidget(120, 20, Component.literal("Extract Jewels"), this::handleRemoval);
        removeButton.setPosition(leftPos + (imageWidth - 120) / 2, topPos + 100);
        removeButton.setColors(0xFF5A5A5A, 0xFF7A7A7A, 0xFF3A3A3A);
        removeButton.setTextColor(0xFFFFFFFF);
    }

    private void startPuzzle() {
        // Get materials and create jewel data (but don't put it in output slot yet!)
        List<ItemStack> materials = menu.getMaterialStacks();
        ItemStack jewel = JewelCreationHelper.createJewel(materials);

        if (!jewel.isEmpty()) {
            // Get jewel data for puzzle
            JewelData jewelData = JewelData.fromItemStack(jewel);
            if (jewelData != null) {
                // Store jewel data temporarily - we'll create the rough jewel after puzzle is solved
                this.pendingJewelData = jewelData;

                // Clear materials
                menu.clearMaterialSlots();

                // Initialize puzzle inline
                int gridSize = jewelData.getRarity().getPuzzleGridSize();
                int centerSize = jewelData.getRarity().getPuzzleCenterSize();
                puzzleState = new PuzzleState(gridSize, centerSize, jewelData.getIndividualColors());

                // Scramble puzzle
                int scrambleCount = gridSize * gridSize * 3;
                puzzleState.scramble(minecraft.level.random, scrambleCount);

                // Calculate tile size and position
                this.tileSize = Math.min(300 / gridSize, 50);
                int puzzleSize = gridSize * tileSize;
                puzzleStartX = (width - puzzleSize) / 2;
                puzzleStartY = (height - puzzleSize) / 2 - 20;

                puzzleActive = true;
                moveCount = 0;

                JewelCharms.LOGGER.info("Started puzzle with gridSize={}, tileSize={}", gridSize, tileSize);
            }
        }
    }

    private void handleRemoval() {
        // Send packet to server to handle removal
        ModNetwork.sendToServer(new com.jewelcharms.network.JewelRemovalPacket());

        // Play click sound
        minecraft.player.playSound(
            net.minecraft.sounds.SoundEvents.ANVIL_USE,
            0.8f, 1.2f
        );
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (puzzleActive) {
            return; // Don't render background when puzzle is active
        }

        // Render standard container background
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Render custom widgets when puzzle is not active
        if (!puzzleActive) {
            if (statusLabel != null) {
                // Update status text
                int materialCount = menu.getMaterialStacks().size();
                if (materialCount == 0) {
                    statusLabel.setText(Component.literal("Insert Materials"));
                } else {
                    statusLabel.setText(Component.literal(materialCount + " Material" + (materialCount > 1 ? "s" : "") + " Ready"));
                }
                statusLabel.render(graphics, mouseX, mouseY, partialTick);
            }

            if (createButton != null) {
                createButton.setEnabled(menu.getMaterialStacks().size() > 0);
                createButton.render(graphics, mouseX, mouseY, partialTick);
            }

            // Render removal section
            if (removalLabel != null) {
                removalLabel.render(graphics, mouseX, mouseY, partialTick);
            }

            if (removeButton != null) {
                // Enable button only if there's a tool with jewels in the removal input slot
                // Slot indices: 0-2 materials, 3 output, 4 removal input, 5-6 removal outputs
                ItemStack removalInput = menu.slots.get(4).getItem(); // REMOVAL_INPUT_SLOT is at index 4
                boolean hasJeweledTool = !removalInput.isEmpty() &&
                                        removalInput.getTag() != null &&
                                        removalInput.getTag().contains("JewelCharms");
                removeButton.setEnabled(hasJeweledTool);
                removeButton.render(graphics, mouseX, mouseY, partialTick);
            }
        }

        // Render puzzle overlay if active
        if (puzzleActive && puzzleState != null) {
            renderPuzzleOverlay(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle puzzle clicks
        if (puzzleActive && puzzleState != null) {
            if (button == 0) {
                // Check skip button first
                if (pendingJewelData != null) {
                    int skipCost = pendingJewelData.getRarity().getSkipCost();
                    int gridSize = puzzleState.getGridSize();
                    int skipButtonWidth = 100;
                    int skipButtonHeight = 20;
                    int skipButtonX = (width - skipButtonWidth) / 2;
                    int skipButtonY = puzzleStartY + gridSize * tileSize + 20;

                    if (mouseX >= skipButtonX && mouseX < skipButtonX + skipButtonWidth &&
                        mouseY >= skipButtonY && mouseY < skipButtonY + skipButtonHeight) {
                        // Check if player can afford
                        if (minecraft.player.experienceLevel >= skipCost) {
                            // Send skip packet
                            ModNetwork.sendToServer(new com.jewelcharms.network.PuzzleSkipPacket(menu.getBlockPos()));

                            // Close puzzle
                            puzzleActive = false;
                            puzzleState = null;
                            pendingJewelData = null;

                            // Play success sound
                            minecraft.player.playSound(
                                net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                                1.0f, 1.0f
                            );
                        } else {
                            // Play error sound
                            minecraft.player.playSound(
                                net.minecraft.sounds.SoundEvents.VILLAGER_NO,
                                1.0f, 1.0f
                            );
                        }
                        return true;
                    }
                }

                // Handle tile clicks
                int col = (int)((mouseX - puzzleStartX) / tileSize);
                int row = (int)((mouseY - puzzleStartY) / tileSize);

                if (puzzleState.slide(row, col)) {
                    moveCount++;

                    // Play click sound
                    minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON,
                        0.5f, 1.5f
                    );

                    // Check if solved
                    if (puzzleState.isSolved()) {
                        onPuzzleComplete();
                    }
                }
            }

            // Block ALL clicks when puzzle is active
            return true;
        }

        // Handle widget clicks
        if (!puzzleActive) {
            if (createButton != null && createButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (removeButton != null && removeButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderPuzzleOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        // Dim background
        graphics.fill(0, 0, width, height, 0xC0000000);

        int gridSize = puzzleState.getGridSize();

        // Draw title and info
        graphics.drawCenteredString(font, "Solve the Puzzle!", width / 2, puzzleStartY - 40, 0xFFFFFF);
        graphics.drawCenteredString(font, "Moves: " + moveCount, width / 2, puzzleStartY - 25, 0xAAAAAA);
        graphics.drawCenteredString(font, "Get all colored tiles into the center!", width / 2, puzzleStartY - 10, 0xAAAAAA);

        // Draw skip button
        if (pendingJewelData != null) {
            int skipCost = pendingJewelData.getRarity().getSkipCost();
            String skipText = "Skip (" + skipCost + " XP)";
            int skipButtonWidth = 100;
            int skipButtonHeight = 20;
            int skipButtonX = (width - skipButtonWidth) / 2;
            int skipButtonY = puzzleStartY + gridSize * tileSize + 20;

            boolean canAfford = minecraft.player.experienceLevel >= skipCost;
            boolean skipHovered = mouseX >= skipButtonX && mouseX < skipButtonX + skipButtonWidth &&
                                 mouseY >= skipButtonY && mouseY < skipButtonY + skipButtonHeight;

            int skipBgColor = canAfford ? (skipHovered ? 0xFF7A7A7A : 0xFF5A5A5A) : 0xFF3A3A3A;
            int skipTextColor = canAfford ? 0xFFFFFF00 : 0xFF777777;

            // Draw button
            graphics.fill(skipButtonX, skipButtonY, skipButtonX + skipButtonWidth, skipButtonY + skipButtonHeight, skipBgColor);
            graphics.fill(skipButtonX, skipButtonY, skipButtonX + skipButtonWidth, skipButtonY + 1, 0xFFFFFFFF); // Top
            graphics.fill(skipButtonX, skipButtonY + skipButtonHeight - 1, skipButtonX + skipButtonWidth, skipButtonY + skipButtonHeight, 0xFF000000); // Bottom
            graphics.fill(skipButtonX, skipButtonY, skipButtonX + 1, skipButtonY + skipButtonHeight, 0xFFFFFFFF); // Left
            graphics.fill(skipButtonX + skipButtonWidth - 1, skipButtonY, skipButtonX + skipButtonWidth, skipButtonY + skipButtonHeight, 0xFF000000); // Right

            // Draw text
            int textWidth = font.width(skipText);
            int textX = skipButtonX + (skipButtonWidth - textWidth) / 2;
            int textY = skipButtonY + (skipButtonHeight - 8) / 2;
            graphics.drawString(font, skipText, textX, textY, skipTextColor);
        }

        // Calculate which tile is hovered
        int hoveredCol = (mouseX - puzzleStartX) / tileSize;
        int hoveredRow = (mouseY - puzzleStartY) / tileSize;

        // Draw puzzle border
        graphics.fill(puzzleStartX - 2, puzzleStartY - 2,
                     puzzleStartX + gridSize * tileSize + 2,
                     puzzleStartY + gridSize * tileSize + 2,
                     0xFFFFFFFF);

        // Draw center highlight
        int centerStart = (gridSize - puzzleState.getCenterSize()) / 2;
        int centerSize = puzzleState.getCenterSize();
        int centerX = puzzleStartX + centerStart * tileSize;
        int centerY = puzzleStartY + centerStart * tileSize;
        int centerWidth = centerSize * tileSize;
        graphics.fill(centerX - 2, centerY - 2, centerX + centerWidth + 2, centerY + centerWidth + 2, 0xFFFFFF00);

        // Draw tiles
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int x = puzzleStartX + col * tileSize;
                int y = puzzleStartY + row * tileSize;

                if (puzzleState.isEmpty(row, col)) {
                    // Empty tile
                    graphics.fill(x + 1, y + 1, x + tileSize - 1, y + tileSize - 1, 0xFF101010);
                } else {
                    // Tile with color
                    int color = puzzleState.getColor(row, col);
                    boolean isHovered = (hoveredCol == col && hoveredRow == row);
                    boolean canSlide = puzzleState.canSlide(row, col);

                    // Draw tile background
                    graphics.fill(x + 2, y + 2, x + tileSize - 2, y + tileSize - 2, 0xFF000000 | color);

                    // Add 3D border
                    graphics.fill(x + 1, y + 1, x + tileSize - 1, y + 2, 0xFFCCCCCC);
                    graphics.fill(x + 1, y + 1, x + 2, y + tileSize - 1, 0xFFCCCCCC);
                    graphics.fill(x + tileSize - 2, y + 1, x + tileSize - 1, y + tileSize - 1, 0xFF404040);
                    graphics.fill(x + 1, y + tileSize - 2, x + tileSize - 1, y + tileSize - 1, 0xFF404040);

                    // Highlight if hovered and can slide
                    if (isHovered && canSlide) {
                        graphics.fill(x + 2, y + 2, x + tileSize - 2, y + tileSize - 2, 0x80FFFFFF);
                    }
                }
            }
        }
    }

    private void onPuzzleComplete() {
        // Play success sound
        minecraft.player.playSound(
            net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
            1.0f, 1.0f
        );

        // Send completion packet to server with jewel data
        if (pendingJewelData != null) {
            String jewelDataString = pendingJewelData.serialize();
            ModNetwork.sendToServer(new PuzzleCompletePacket(
                menu.getBlockPos(),
                true,
                puzzleState.serialize(),
                jewelDataString
            ));

            JewelCharms.LOGGER.info("Puzzle completed! Sent jewel data to server.");
        } else {
            JewelCharms.LOGGER.error("ERROR: pendingJewelData is null on puzzle complete!");
        }

        // Hide puzzle - player can now see and pick up the rough jewel from output slot!
        puzzleActive = false;
        puzzleState = null;
        pendingJewelData = null;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!puzzleActive) {
            // Render title at the top
            graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
            // Render inventory label
            graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
        }
    }
}

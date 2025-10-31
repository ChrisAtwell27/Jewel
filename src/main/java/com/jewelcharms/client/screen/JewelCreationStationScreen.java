package com.jewelcharms.client.screen;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.network.ModNetwork;
import com.jewelcharms.network.PuzzleCompletePacket;
import com.jewelcharms.util.JewelCreationHelper;
import com.jewelcharms.util.JewelData;
import com.jewelcharms.util.PuzzleState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class JewelCreationStationScreen extends AbstractContainerScreen<JewelCreationStationMenu> {
    private final Level world;
    private final int x, y, z;
    private final Player entity;

    Button button_create;
    Button button_remove;

    // Puzzle state
    private boolean puzzleActive = false;
    private PuzzleState puzzleState = null;
    private JewelData pendingJewelData = null;
    private int puzzleStartX = 0;
    private int puzzleStartY = 0;
    private int tileSize = 0;
    private int moveCount = 0;

    public JewelCreationStationScreen(JewelCreationStationMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94; // Standard position for inventory label
    }

    private static final ResourceLocation texture = new ResourceLocation(JewelCharms.MOD_ID, "textures/gui/creation_station.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // When puzzle is active, only render the puzzle overlay (no inventory/slots)
        if (puzzleActive && puzzleState != null) {
            this.renderBackground(guiGraphics);
            renderPuzzleOverlay(guiGraphics, mouseX, mouseY);
            return; // Skip all other rendering
        }

        // Normal rendering when puzzle is not active
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Custom tooltips for slots
        boolean customTooltipShown = false;

        // Material slots tooltips (aligned with 2nd, 4th, and 6th inventory columns)
        int[] materialX = {26, 62, 98};
        int materialY = 16;
        for (int i = 0; i < 3; i++) {
            if (mouseX > leftPos + materialX[i] && mouseX < leftPos + materialX[i] + 16
                && mouseY > topPos + materialY && mouseY < topPos + materialY + 16) {
                guiGraphics.renderTooltip(font, Component.translatable("gui.jewelcharms.creation_station.tooltip_material_" + (i + 1)), mouseX, mouseY);
                customTooltipShown = true;
            }
        }

        // Removal input slot tooltip (aligned with 8th column at 134, 16)
        if (mouseX > leftPos + 134 && mouseX < leftPos + 134 + 16 && mouseY > topPos + 16 && mouseY < topPos + 16 + 16) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.jewelcharms.creation_station.tooltip_remove_input"), mouseX, mouseY);
            customTooltipShown = true;
        }

        // Output slot tooltip (aligned with 4th column at 62, 50)
        if (mouseX > leftPos + 62 && mouseX < leftPos + 62 + 16 && mouseY > topPos + 50 && mouseY < topPos + 50 + 16) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.jewelcharms.creation_station.tooltip_output"), mouseX, mouseY);
            customTooltipShown = true;
        }

        // Removal output slots tooltips (aligned with 7th and 9th columns)
        int[] removalOutputX = {116, 152};
        int removalOutputY = 50;
        for (int i = 0; i < 2; i++) {
            if (mouseX > leftPos + removalOutputX[i] && mouseX < leftPos + removalOutputX[i] + 16
                && mouseY > topPos + removalOutputY && mouseY < topPos + removalOutputY + 16) {
                guiGraphics.renderTooltip(font, Component.translatable("gui.jewelcharms.creation_station.tooltip_removal_output_" + (i + 1)), mouseX, mouseY);
                customTooltipShown = true;
            }
        }

        if (!customTooltipShown)
            this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (puzzleActive) {
            return; // Don't render background when puzzle is active
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Don't render labels when puzzle is active
    }

    @Override
    public void init() {
        super.init();
        button_create = Button.builder(Component.translatable("gui.jewelcharms.creation_station.button_create"), e -> {
            startPuzzle();
        }).bounds(this.leftPos + -60, this.topPos + 48, 56, 20).build();
        this.addRenderableWidget(button_create);

        button_remove = Button.builder(Component.translatable("gui.jewelcharms.creation_station.button_remove"), e -> {
            handleRemoval();
        }).bounds(this.leftPos + 181, this.topPos + 48, 56, 20).build();
        this.addRenderableWidget(button_remove);
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

                // Calculate tile size and position - scale to fit screen better
                int maxPuzzleHeight = height - 120; // Leave space for title, info, and skip button
                int maxPuzzleWidth = width - 60; // Leave side margins

                // Calculate the largest tile size that fits
                int maxTileSizeForHeight = maxPuzzleHeight / gridSize;
                int maxTileSizeForWidth = maxPuzzleWidth / gridSize;
                this.tileSize = Math.min(Math.min(maxTileSizeForHeight, maxTileSizeForWidth), 40); // Cap at 40px max

                int puzzleSize = gridSize * tileSize;
                puzzleStartX = (width - puzzleSize) / 2;
                puzzleStartY = (height - puzzleSize) / 2;

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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle puzzle clicks
        if (puzzleActive && puzzleState != null) {
            if (button == 0) {
                // Check skip button first
                if (pendingJewelData != null) {
                    int skipCost = pendingJewelData.getRarity().getSkipCost();
                    int gridSize = puzzleState.getGridSize();
                    int puzzleBottomY = puzzleStartY + gridSize * tileSize;
                    int skipButtonWidth = 100;
                    int skipButtonHeight = 20;
                    int skipButtonX = (width - skipButtonWidth) / 2;
                    int skipButtonY = Math.min(height - 30, puzzleBottomY + 10);

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

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderPuzzleOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        // Dim background
        graphics.fill(0, 0, width, height, 0xC0000000);

        int gridSize = puzzleState.getGridSize();
        int puzzleBottomY = puzzleStartY + gridSize * tileSize;

        // Draw title and info above puzzle
        int titleY = Math.max(10, puzzleStartY - 50);
        graphics.drawCenteredString(font, "Solve the Puzzle!", width / 2, titleY, 0xFFFFFF);
        graphics.drawCenteredString(font, "Moves: " + moveCount, width / 2, titleY + 15, 0xAAAAAA);
        graphics.drawCenteredString(font, "Get all colored tiles into the center!", width / 2, titleY + 30, 0xAAAAAA);

        // Draw skip button below puzzle
        if (pendingJewelData != null) {
            int skipCost = pendingJewelData.getRarity().getSkipCost();
            String skipText = "Skip (" + skipCost + " XP)";
            int skipButtonWidth = 100;
            int skipButtonHeight = 20;
            int skipButtonX = (width - skipButtonWidth) / 2;
            int skipButtonY = Math.min(height - 30, puzzleBottomY + 10);

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
}

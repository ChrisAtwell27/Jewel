package com.jewelcharms.client.screen;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.network.ModNetwork;
import com.jewelcharms.network.MinigameClickPacket;
import com.jewelcharms.network.MinigameCompletePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JewelCreationStationScreen extends AbstractContainerScreen<JewelCreationStationMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JewelCharms.MOD_ID, "textures/gui/jewel_creation_station.png");

    // Minigame state
    private boolean minigameActive = false;
    private int minigameTimer = 0;
    private static final int MINIGAME_DURATION = 100; // 5 seconds (20 ticks per second)
    private static final int GRID_SIZE = 5;
    private static final int CELL_SIZE = 16;
    private List<GridCell> targetCells = new ArrayList<>();
    private List<GridCell> clickedCells = new ArrayList<>();
    private Random random = new Random();
    private int minigameStartX = 0;
    private int minigameStartY = 0;
    private net.minecraft.client.gui.components.Button createButton;

    public JewelCreationStationScreen(JewelCreationStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 256;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        minigameStartX = leftPos + 25;
        minigameStartY = topPos + 65;

        // Add a button to start the minigame (only visible when materials are present)
        createButton = net.minecraft.client.gui.components.Button.builder(
            Component.literal("Create Jewel"),
            button -> {
                if (!minigameActive && menu.getMaterialStacks().size() > 0) {
                    startMinigame();
                }
            })
            .bounds(leftPos + 120, topPos + 50, 80, 20)
            .build();
        this.addRenderableWidget(createButton);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render minigame grid if active
        if (minigameActive) {
            renderMinigame(graphics, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Update button state
        if (createButton != null) {
            createButton.active = !minigameActive && menu.getMaterialStacks().size() > 0;
        }

        // Render minigame timer
        if (minigameActive) {
            int timeLeft = (MINIGAME_DURATION - minigameTimer) / 20;
            graphics.drawString(font, "Time: " + timeLeft + "s", leftPos + 120, topPos + 20, 0xFFFFFF, false);

            int progress = (clickedCells.size() * 100) / targetCells.size();
            graphics.drawString(font, "Progress: " + progress + "%", leftPos + 120, topPos + 35, 0xFFFFFF, false);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (minigameActive) {
            minigameTimer++;
            if (minigameTimer >= MINIGAME_DURATION) {
                endMinigame(false); // Time's up, failed
            }

            // Check if all targets clicked
            if (clickedCells.size() >= targetCells.size()) {
                endMinigame(true); // Success!
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (minigameActive && button == 0) {
            int gridX = (int)((mouseX - minigameStartX) / CELL_SIZE);
            int gridY = (int)((mouseY - minigameStartY) / CELL_SIZE);

            if (gridX >= 0 && gridX < GRID_SIZE && gridY >= 0 && gridY < GRID_SIZE) {
                handleGridClick(gridX, gridY);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderMinigame(GuiGraphics graphics, int mouseX, int mouseY) {
        // Draw grid background
        graphics.fill(minigameStartX - 2, minigameStartY - 2,
                     minigameStartX + GRID_SIZE * CELL_SIZE + 2,
                     minigameStartY + GRID_SIZE * CELL_SIZE + 2,
                     0xFF000000);

        // Draw grid cells
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int cellX = minigameStartX + x * CELL_SIZE;
                int cellY = minigameStartY + y * CELL_SIZE;

                GridCell cell = new GridCell(x, y);
                int color = 0xFF404040; // Default gray

                if (targetCells.contains(cell)) {
                    if (clickedCells.contains(cell)) {
                        color = 0xFF00FF00; // Green - correctly clicked
                    } else {
                        color = 0xFFFFFF00; // Yellow - target
                    }
                } else if (clickedCells.contains(cell)) {
                    color = 0xFFFF0000; // Red - wrong click
                }

                graphics.fill(cellX + 1, cellY + 1, cellX + CELL_SIZE - 1, cellY + CELL_SIZE - 1, color);
            }
        }
    }

    private void handleGridClick(int gridX, int gridY) {
        GridCell clicked = new GridCell(gridX, gridY);

        if (!clickedCells.contains(clicked)) {
            clickedCells.add(clicked);

            // Send packet to server
            ModNetwork.sendToServer(new MinigameClickPacket(
                menu.getBlockPos(),
                gridX,
                gridY,
                targetCells.contains(clicked)
            ));
        }
    }

    public void startMinigame() {
        minigameActive = true;
        minigameTimer = 0;
        clickedCells.clear();
        targetCells.clear();

        // Generate random target cells (5-8 targets)
        int targetCount = 5 + random.nextInt(4);
        while (targetCells.size() < targetCount) {
            GridCell cell = new GridCell(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
            if (!targetCells.contains(cell)) {
                targetCells.add(cell);
            }
        }
    }

    private void endMinigame(boolean success) {
        minigameActive = false;
        minigameTimer = 0;

        // Send completion packet to server
        ModNetwork.sendToServer(new MinigameCompletePacket(menu.getBlockPos(), success));

        if (success) {
            JewelCharms.LOGGER.info("Minigame completed successfully!");
        } else {
            JewelCharms.LOGGER.info("Minigame failed!");
        }

        clickedCells.clear();
        targetCells.clear();
    }

    public boolean isMinigameActive() {
        return minigameActive;
    }

    // Helper class for grid cells
    private static class GridCell {
        private final int x;
        private final int y;

        public GridCell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof GridCell)) return false;
            GridCell other = (GridCell) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
}

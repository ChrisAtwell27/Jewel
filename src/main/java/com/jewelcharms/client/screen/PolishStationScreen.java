package com.jewelcharms.client.screen;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.client.ui.*;
import com.jewelcharms.menu.PolishStationMenu;
import com.jewelcharms.network.ModNetwork;
import com.jewelcharms.network.PuzzleCompletePacket;
import com.jewelcharms.util.JewelData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Polish Station screen - Standard inventory UI with polish minigame
 */
public class PolishStationScreen extends AbstractContainerScreen<PolishStationMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(JewelCharms.MOD_ID, "textures/gui/container/polish_station.png");

    private ButtonWidget startButton;
    private ProgressBarWidget progressBar;
    private LabelWidget statusLabel;

    // Minigame state
    private boolean gameActive = false;
    private boolean buttonHeld = false;
    private float indicatorPosition = 0.0f; // 0.0 to 1.0
    private float indicatorSpeed = 0.02f; // Speed of movement per tick
    private int indicatorDirection = 1; // 1 for right, -1 for left

    // Zone definitions
    private static final float GREEN_ZONE_START = 0.40f;
    private static final float GREEN_ZONE_END = 0.60f;
    private static final float RED_ZONE_THRESHOLD = 0.25f; // Distance from green zone to be "red"

    public PolishStationScreen(PolishStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 166;
        this.imageWidth = 176;
        this.inventoryLabelY = 73;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();

        // Create UI widgets positioned relative to the GUI
        int centerX = leftPos + imageWidth / 2;

        // Status label (above slots)
        statusLabel = new LabelWidget(Component.literal("Insert Rough Jewel"), 0xFF404040);
        statusLabel.setAlignment(LabelWidget.Alignment.CENTER);
        statusLabel.setPosition(centerX - 70, topPos + 20);
        statusLabel.setWidth(140);

        // Progress bar (between input and output slots)
        progressBar = new ProgressBarWidget(120, 14);
        progressBar.setPosition(leftPos + (imageWidth - 120) / 2, topPos + 58);
        progressBar.setFillColor(0xFF4A90E2);
        progressBar.setBackgroundColor(0xFF373737);
        progressBar.setBorderColor(0xFF8B8B8B);
        progressBar.setProgress(0.0f);

        // Start button (below progress bar)
        startButton = new ButtonWidget(120, 20, Component.literal("Hold to Polish"), this::startPolishMinigame);
        startButton.setPosition(leftPos + (imageWidth - 120) / 2, topPos + 96);
        startButton.setColors(0xFF5A5A5A, 0xFF7A7A7A, 0xFF3A3A3A);
        startButton.setTextColor(0xFFFFFFFF);
    }

    private void startPolishMinigame() {
        if (!menu.getInputItem().isEmpty() && !gameActive) {
            gameActive = true;
            buttonHeld = true;
            indicatorPosition = 0.0f;
            indicatorDirection = 1;
            statusLabel.setText(Component.literal("Hold until green!"));

            // Play start sound
            minecraft.player.playSound(
                net.minecraft.sounds.SoundEvents.GRINDSTONE_USE,
                0.8f, 1.0f
            );
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Render standard container background
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Render custom widgets
        if (statusLabel != null) {
            statusLabel.render(graphics, mouseX, mouseY, partialTick);
        }
        if (progressBar != null && !gameActive) {
            progressBar.render(graphics, mouseX, mouseY, partialTick);
        }
        if (startButton != null) {
            startButton.setEnabled(!menu.getInputItem().isEmpty() && !gameActive);
            startButton.render(graphics, mouseX, mouseY, partialTick);
        }

        // Render polish minigame overlay if active
        if (gameActive) {
            renderPolishMinigame(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderPolishMinigame(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Calculate progress bar position
        int barX = leftPos + (imageWidth - 120) / 2;
        int barY = topPos + 58;
        int barWidth = 120;
        int barHeight = 14;

        // Draw enhanced progress bar background
        graphics.fill(barX - 2, barY - 2, barX + barWidth + 2, barY + barHeight + 2, 0xFF000000);
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF373737);

        // Draw color zones
        // Yellow zone (outside red, but not green)
        graphics.fill(barX + 1, barY + 1, barX + barWidth - 1, barY + barHeight - 1, 0xFFFFAA00);

        // Green zone
        int greenStartX = barX + (int)(GREEN_ZONE_START * barWidth);
        int greenEndX = barX + (int)(GREEN_ZONE_END * barWidth);
        graphics.fill(greenStartX, barY + 1, greenEndX, barY + barHeight - 1, 0xFF00CC00);

        // Red zones (edges)
        int redZoneWidth = (int)(RED_ZONE_THRESHOLD * barWidth);
        graphics.fill(barX + 1, barY + 1, barX + redZoneWidth, barY + barHeight - 1, 0xFFCC0000);
        graphics.fill(barX + barWidth - redZoneWidth, barY + 1, barX + barWidth - 1, barY + barHeight - 1, 0xFFCC0000);

        // Draw moving indicator
        int indicatorX = barX + (int)(indicatorPosition * barWidth);
        graphics.fill(indicatorX - 2, barY - 4, indicatorX + 2, barY + barHeight + 4, 0xFFFFFFFF);
        graphics.fill(indicatorX - 1, barY - 3, indicatorX + 1, barY + barHeight + 3, 0xFF000000);

        // Draw border
        graphics.fill(barX, barY, barX + barWidth, barY + 1, 0xFF8B8B8B);
        graphics.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF8B8B8B);
        graphics.fill(barX, barY, barX + 1, barY + barHeight, 0xFF8B8B8B);
        graphics.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFF8B8B8B);

        // Draw instruction text
        String instruction = buttonHeld ? "Release when GREEN!" : "Click to hold!";
        graphics.drawCenteredString(font, instruction, leftPos + imageWidth / 2, topPos + 78, 0xFFFFFF);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        // Update indicator position when game is active
        if (gameActive && buttonHeld) {
            indicatorPosition += indicatorSpeed * indicatorDirection;

            // Bounce at edges
            if (indicatorPosition >= 1.0f) {
                indicatorPosition = 1.0f;
                indicatorDirection = -1;
            } else if (indicatorPosition <= 0.0f) {
                indicatorPosition = 0.0f;
                indicatorDirection = 1;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // If game is active, clicking anywhere means holding
        if (gameActive && button == 0) {
            buttonHeld = true;
            return true;
        }

        // Handle main UI clicks
        if (!gameActive) {
            if (startButton != null && startButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Handle button release during minigame
        if (gameActive && button == 0 && buttonHeld) {
            buttonHeld = false;
            checkPolishResult();
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void checkPolishResult() {
        // Check if indicator is in green zone
        if (indicatorPosition >= GREEN_ZONE_START && indicatorPosition <= GREEN_ZONE_END) {
            // SUCCESS!
            onPolishSuccess();
        } else {
            // Check if in red zone
            float distanceFromGreen = Math.min(
                Math.abs(indicatorPosition - GREEN_ZONE_START),
                Math.abs(indicatorPosition - GREEN_ZONE_END)
            );

            if (distanceFromGreen >= RED_ZONE_THRESHOLD) {
                // FAILURE - in red zone
                onPolishFailure();
            } else {
                // PARTIAL - in yellow zone, can try again
                statusLabel.setText(Component.literal("Try again!"));
                buttonHeld = false;
                // Don't reset gameActive, let them try again
            }
        }
    }

    private void onPolishSuccess() {
        // Play success sound
        minecraft.player.playSound(
            net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
            0.5f, 1.5f
        );

        // Get the rough jewel data
        ItemStack roughJewel = menu.getInputItem();
        if (!roughJewel.isEmpty()) {
            JewelData jewelData = JewelData.fromItemStack(roughJewel);
            if (jewelData != null) {
                // Send polish completion packet to server
                // Using PuzzleCompletePacket with polish=true flag
                String jewelDataString = jewelData.serialize();
                ModNetwork.sendToServer(new PuzzleCompletePacket(
                    menu.getBlockPos(),
                    true, // Indicates polish completion
                    "", // No puzzle state for polish
                    jewelDataString
                ));

                JewelCharms.LOGGER.info("Polish completed! Sent jewel data to server.");
            }
        }

        // Reset state
        gameActive = false;
        buttonHeld = false;
        statusLabel.setText(Component.literal("Success!"));
    }

    private void onPolishFailure() {
        // Play failure sound
        minecraft.player.playSound(
            net.minecraft.sounds.SoundEvents.ANVIL_BREAK,
            0.5f, 0.8f
        );

        // Destroy the rough jewel
        menu.clearInputSlot();

        // Close menu immediately
        onClose();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Render title at the top
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        // Render inventory label
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}

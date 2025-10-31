package com.jewelcharms.client.screen;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.PolishStationMenu;
import com.jewelcharms.network.ModNetwork;
import com.jewelcharms.network.PuzzleCompletePacket;
import com.jewelcharms.util.JewelData;
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

/**
 * Polish Station screen - Standard inventory UI with polish minigame
 */
public class PolishStationScreen extends AbstractContainerScreen<PolishStationMenu> {
    private static final ResourceLocation texture = new ResourceLocation(JewelCharms.MOD_ID, "textures/gui/polish_station.png");

    private final Level world;
    private final int x, y, z;
    private final Player entity;

    Button button_polish;

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

    public PolishStationScreen(PolishStationMenu container, Inventory inventory, Component text) {
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

    @Override
    public void init() {
        super.init();
        button_polish = Button.builder(Component.translatable("gui.jewelcharms.polish_station.button_polish"), e -> {
            startPolishMinigame();
        }).bounds(this.leftPos + 28, this.topPos + 58, 120, 20).build();
        this.addRenderableWidget(button_polish);
    }

    private void startPolishMinigame() {
        if (!menu.getInputItem().isEmpty() && !gameActive) {
            gameActive = true;
            buttonHeld = true;
            indicatorPosition = 0.0f;
            indicatorDirection = 1;

            // Play start sound
            minecraft.player.playSound(
                net.minecraft.sounds.SoundEvents.GRINDSTONE_USE,
                0.8f, 1.0f
            );
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
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
        // Don't render labels
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        // Render polish minigame bar if active (between slot and button)
        if (gameActive) {
            renderPolishMinigame(graphics, mouseX, mouseY, partialTick);
        }

        // Custom tooltips for slots
        boolean customTooltipShown = false;

        // Input slot tooltip (at position 80, 14)
        if (mouseX > leftPos + 80 && mouseX < leftPos + 80 + 16 && mouseY > topPos + 14 && mouseY < topPos + 14 + 16) {
            graphics.renderTooltip(font, Component.translatable("gui.jewelcharms.polish_station.tooltip_input"), mouseX, mouseY);
            customTooltipShown = true;
        }

        if (!customTooltipShown)
            this.renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderPolishMinigame(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Calculate progress bar position (between slot and button)
        // Slot is at y=14, button is at y=58
        // Position bar at y=35 (between them)
        int barX = leftPos + 28;
        int barY = topPos + 35;
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
    }

    private void onPolishFailure() {
        // Play failure sound
        minecraft.player.playSound(
            net.minecraft.sounds.SoundEvents.ANVIL_BREAK,
            0.5f, 0.8f
        );

        // Destroy the rough jewel
        menu.clearInputSlot();

        // Reset state
        gameActive = false;
        buttonHeld = false;

        // Close menu immediately
        onClose();
    }
}

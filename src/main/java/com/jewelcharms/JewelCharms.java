package com.jewelcharms;

import com.jewelcharms.client.screen.JewelCreationStationScreen;
import com.jewelcharms.config.MaterialEffectConfig;
import com.jewelcharms.init.ModBlocks;
import com.jewelcharms.init.ModItems;
import com.jewelcharms.init.ModMenuTypes;
import com.jewelcharms.init.ModRecipeTypes;
import com.jewelcharms.init.ModCreativeTabs;
import com.jewelcharms.network.ModNetwork;
import com.jewelcharms.util.JewelCreationHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JewelCharms.MOD_ID)
public class JewelCharms {
    public static final String MOD_ID = "jewelcharms";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JewelCharms() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // Setup listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Jewel Charms mod loading...");

        // Register network
        event.enqueueWork(() -> {
            ModNetwork.register();

            // Load material effect config
            MaterialEffectConfig config = MaterialEffectConfig.load();
            JewelCreationHelper.setConfig(config);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Jewel Charms client setup...");

        event.enqueueWork(() -> {
            // Register screens
            MenuScreens.register(ModMenuTypes.JEWEL_CREATION_STATION.get(), JewelCreationStationScreen::new);

            // Register item colors
            registerItemColors();
        });
    }

    private void registerItemColors() {
        net.minecraft.client.color.item.ItemColors itemColors =
            net.minecraft.client.Minecraft.getInstance().getItemColors();

        itemColors.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                com.jewelcharms.util.JewelData jewelData =
                    com.jewelcharms.util.JewelData.fromItemStack(stack);
                if (jewelData != null) {
                    return jewelData.getColor();
                }
            }
            return 0xFFFFFF; // White default
        }, ModItems.JEWEL.get());
    }
}

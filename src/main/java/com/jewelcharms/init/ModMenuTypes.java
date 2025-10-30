package com.jewelcharms.init;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.menu.JewelCreationStationMenu;
import com.jewelcharms.menu.PolishStationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JewelCharms.MOD_ID);

    public static final RegistryObject<MenuType<JewelCreationStationMenu>> JEWEL_CREATION_STATION = MENU_TYPES.register("jewel_creation_station",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                return new JewelCreationStationMenu(windowId, inv, data.readBlockPos());
            }));

    public static final RegistryObject<MenuType<PolishStationMenu>> POLISH_STATION = MENU_TYPES.register("polish_station",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                return new PolishStationMenu(windowId, inv, data.readBlockPos());
            }));
}

package com.jewelcharms.init;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.item.JewelItem;
import com.jewelcharms.item.JewelSocketTemplateItem;
import com.jewelcharms.item.RoughJewelItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JewelCharms.MOD_ID);

    // Block items
    public static final RegistryObject<Item> JEWEL_CREATION_STATION_ITEM = ITEMS.register("jewel_creation_station",
            () -> new BlockItem(ModBlocks.JEWEL_CREATION_STATION.get(), new Item.Properties()));

    public static final RegistryObject<Item> POLISH_STATION_ITEM = ITEMS.register("polish_station",
            () -> new BlockItem(ModBlocks.POLISH_STATION.get(), new Item.Properties()));

    // Items
    public static final RegistryObject<Item> JEWEL = ITEMS.register("jewel",
            JewelItem::new);

    public static final RegistryObject<Item> ROUGH_JEWEL = ITEMS.register("rough_jewel",
            RoughJewelItem::new);

    public static final RegistryObject<Item> JEWEL_SOCKET_TEMPLATE = ITEMS.register("jewel_socket_template",
            JewelSocketTemplateItem::new);
}

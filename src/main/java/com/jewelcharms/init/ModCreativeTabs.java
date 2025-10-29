package com.jewelcharms.init;

import com.jewelcharms.JewelCharms;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JewelCharms.MOD_ID);

    public static final RegistryObject<CreativeModeTab> JEWEL_CHARMS_TAB = CREATIVE_MODE_TABS.register("jewelcharms_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.jewelcharms"))
                    .icon(() -> new ItemStack(ModItems.JEWEL.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.JEWEL_CREATION_STATION_ITEM.get());
                        output.accept(ModItems.JEWEL.get());
                        output.accept(ModItems.JEWEL_SOCKET_TEMPLATE.get());
                    })
                    .build());
}

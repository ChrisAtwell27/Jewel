package com.jewelcharms.init;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.block.JewelCreationStationBlock;
import com.jewelcharms.block.PolishStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JewelCharms.MOD_ID);

    public static final RegistryObject<Block> JEWEL_CREATION_STATION = BLOCKS.register("jewel_creation_station",
            JewelCreationStationBlock::new);

    public static final RegistryObject<Block> POLISH_STATION = BLOCKS.register("polish_station",
            PolishStationBlock::new);
}

package com.jewelcharms.init;

import com.jewelcharms.JewelCharms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for custom sound effects
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JewelCharms.MOD_ID);

    // Jewel Effect Sounds
    public static final RegistryObject<SoundEvent> JEWEL_ACTIVATE = register("jewel_activate");
    public static final RegistryObject<SoundEvent> JEWEL_SHIMMER = register("jewel_shimmer");
    public static final RegistryObject<SoundEvent> JEWEL_CHIME = register("jewel_chime");
    public static final RegistryObject<SoundEvent> JEWEL_RESONANCE = register("jewel_resonance");

    // Effect-specific sounds
    public static final RegistryObject<SoundEvent> FORTUNE_TRIGGER = register("fortune_trigger");
    public static final RegistryObject<SoundEvent> VEIN_MINER_TRIGGER = register("vein_miner_trigger");
    public static final RegistryObject<SoundEvent> LIFESTEAL_TRIGGER = register("lifesteal_trigger");
    public static final RegistryObject<SoundEvent> CRITICAL_HIT_TRIGGER = register("critical_hit_trigger");
    public static final RegistryObject<SoundEvent> EXECUTE_TRIGGER = register("execute_trigger");
    public static final RegistryObject<SoundEvent> MAGNETIC_PULSE = register("magnetic_pulse");
    public static final RegistryObject<SoundEvent> VOID_TOUCH_TRIGGER = register("void_touch_trigger");
    public static final RegistryObject<SoundEvent> TELEPORT_DROPS_TRIGGER = register("teleport_drops_trigger");
    public static final RegistryObject<SoundEvent> BUFF_APPLY = register("buff_apply");
    public static final RegistryObject<SoundEvent> BUFF_EXPIRE = register("buff_expire");

    // Crafting sounds
    public static final RegistryObject<SoundEvent> JEWEL_CREATE = register("jewel_create");
    public static final RegistryObject<SoundEvent> JEWEL_POLISH = register("jewel_polish");
    public static final RegistryObject<SoundEvent> JEWEL_ATTACH = register("jewel_attach");
    public static final RegistryObject<SoundEvent> JEWEL_REMOVE = register("jewel_remove");

    // Puzzle sounds
    public static final RegistryObject<SoundEvent> PUZZLE_CLICK = register("puzzle_click");
    public static final RegistryObject<SoundEvent> PUZZLE_COMPLETE = register("puzzle_complete");
    public static final RegistryObject<SoundEvent> PUZZLE_FAIL = register("puzzle_fail");

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation location = new ResourceLocation(JewelCharms.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }
}
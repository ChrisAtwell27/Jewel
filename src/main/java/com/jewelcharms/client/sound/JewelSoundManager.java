package com.jewelcharms.client.sound;

import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages sound effects for jewel triggers
 */
@OnlyIn(Dist.CLIENT)
public class JewelSoundManager {

    private static final Random RANDOM = new Random();
    private static final Map<JewelEffect, RegistryObject<SoundEvent>> EFFECT_SOUNDS = new HashMap<>();

    static {
        // Map specific effects to their unique sounds
        EFFECT_SOUNDS.put(JewelEffect.FORTUNE, ModSounds.FORTUNE_TRIGGER);
        EFFECT_SOUNDS.put(JewelEffect.AOE_MINING, ModSounds.VEIN_MINER_TRIGGER);
        EFFECT_SOUNDS.put(JewelEffect.LIFESTEAL, ModSounds.LIFESTEAL_TRIGGER);
        EFFECT_SOUNDS.put(JewelEffect.CRITICAL_CHANCE, ModSounds.CRITICAL_HIT_TRIGGER);
        EFFECT_SOUNDS.put(JewelEffect.EXECUTE, ModSounds.EXECUTE_TRIGGER);
        EFFECT_SOUNDS.put(JewelEffect.MAGNETIC, ModSounds.MAGNETIC_PULSE);
        EFFECT_SOUNDS.put(JewelEffect.VOID_TOUCH, ModSounds.VOID_TOUCH_TRIGGER);
        EFFECT_SOUNDS.put(JewelEffect.TELEPORT_DROPS, ModSounds.TELEPORT_DROPS_TRIGGER);
    }

    /**
     * Play a sound when a jewel effect triggers
     */
    public static void playEffectSound(Level level, Vec3 pos, JewelEffect effect, float volume, float pitch) {
        if (!level.isClientSide()) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // Get effect-specific sound or use generic
        RegistryObject<SoundEvent> soundEvent = EFFECT_SOUNDS.get(effect);
        if (soundEvent == null) {
            // Use generic jewel activation sound
            soundEvent = getGenericSoundForCategory(effect);
        }

        if (soundEvent != null && soundEvent.get() != null) {
            // Only play if within hearing range (48 blocks)
            if (player.position().distanceToSqr(pos) < 48 * 48) {
                mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
                    soundEvent.get(),
                    volume * getVolumeModifier(effect),
                    pitch + getPitchVariation()
                ));
            }
        }
    }

    /**
     * Play a sound at player position
     */
    public static void playEffectSoundAtPlayer(Player player, JewelEffect effect) {
        if (player.level().isClientSide()) {
            playEffectSound(player.level(), player.position(), effect, 0.5f, 1.0f);
        }
    }

    /**
     * Play a sound at entity position
     */
    public static void playEffectSoundAtEntity(Entity entity, JewelEffect effect) {
        if (entity.level().isClientSide()) {
            playEffectSound(entity.level(), entity.position(), effect, 0.6f, 1.0f);
        }
    }

    /**
     * Play buff application sound
     */
    public static void playBuffApply(Player player) {
        if (player.level().isClientSide()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == player) {
                mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
                    ModSounds.BUFF_APPLY.get(), 0.3f, 1.0f + getPitchVariation()
                ));
            }
        }
    }

    /**
     * Play buff expiration sound
     */
    public static void playBuffExpire(Player player) {
        if (player.level().isClientSide()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == player) {
                mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
                    ModSounds.BUFF_EXPIRE.get(), 0.2f, 0.8f
                ));
            }
        }
    }

    /**
     * Play jewel attachment sound
     */
    public static void playJewelAttach() {
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
            ModSounds.JEWEL_ATTACH.get(), 0.8f, 1.0f
        ));
    }

    /**
     * Play jewel removal sound
     */
    public static void playJewelRemove() {
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
            ModSounds.JEWEL_REMOVE.get(), 0.7f, 0.9f
        ));
    }

    /**
     * Play jewel creation sound
     */
    public static void playJewelCreate() {
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
            ModSounds.JEWEL_CREATE.get(), 1.0f, 1.0f
        ));
    }

    /**
     * Play jewel polish sound
     */
    public static void playJewelPolish() {
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
            ModSounds.JEWEL_POLISH.get(), 0.8f, 1.2f
        ));
    }

    /**
     * Play ambient shimmer sound for held jeweled items
     */
    public static void playAmbientShimmer(Player player, float chance) {
        if (player.level().isClientSide() && RANDOM.nextFloat() < chance) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == player) {
                mc.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(
                    ModSounds.JEWEL_SHIMMER.get(), 0.1f, 1.0f + getPitchVariation()
                ));
            }
        }
    }

    private static RegistryObject<SoundEvent> getGenericSoundForCategory(JewelEffect effect) {
        switch (effect.getCategory()) {
            case TOOL:
            case UNIVERSAL:
                return ModSounds.JEWEL_ACTIVATE;
            case WEAPON:
                return ModSounds.JEWEL_CHIME;
            case PLAYER:
                return ModSounds.JEWEL_SHIMMER;
            case SPECIAL:
                return ModSounds.JEWEL_RESONANCE;
            default:
                return ModSounds.JEWEL_ACTIVATE;
        }
    }

    private static float getVolumeModifier(JewelEffect effect) {
        // Adjust volume based on effect type
        switch (effect) {
            case EXECUTE:
            case VOID_TOUCH:
            case AOE_MINING:
                return 1.2f; // Louder for powerful effects
            case DURABILITY:
            case SELF_REPAIR:
            case RESISTANCE:
                return 0.5f; // Quieter for passive effects
            default:
                return 1.0f;
        }
    }

    private static float getPitchVariation() {
        return (RANDOM.nextFloat() - 0.5f) * 0.2f; // ±0.1 pitch variation
    }
}
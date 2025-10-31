package com.jewelcharms.client.particle;

import com.jewelcharms.effect.JewelEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages particle effects for jewel effect triggers
 */
@OnlyIn(Dist.CLIENT)
public class JewelEffectParticles {

    // Map effects to their particle types
    private static final Map<JewelEffect, ParticleConfig> EFFECT_PARTICLES = new HashMap<>();

    static {
        // Tool Effects
        EFFECT_PARTICLES.put(JewelEffect.MINING_SPEED, new ParticleConfig(ParticleTypes.INSTANT_EFFECT, 5, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.FORTUNE, new ParticleConfig(ParticleTypes.HAPPY_VILLAGER, 8, 0.5f));
        EFFECT_PARTICLES.put(JewelEffect.SILK_TOUCH, new ParticleConfig(ParticleTypes.END_ROD, 3, 0.1f));
        EFFECT_PARTICLES.put(JewelEffect.AUTO_SMELT, new ParticleConfig(ParticleTypes.FLAME, 10, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.AOE_MINING, new ParticleConfig(ParticleTypes.SWEEP_ATTACK, 1, 0.5f));
        EFFECT_PARTICLES.put(JewelEffect.REPLANTING, new ParticleConfig(ParticleTypes.COMPOSTER, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.TREE_FELLING, new ParticleConfig(ParticleTypes.SMOKE, 15, 0.8f));
        EFFECT_PARTICLES.put(JewelEffect.MAGNETIC, new ParticleConfig(ParticleTypes.PORTAL, 8, 0.4f));

        // Weapon Effects
        EFFECT_PARTICLES.put(JewelEffect.DAMAGE, new ParticleConfig(ParticleTypes.CRIT, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.LOOTING, new ParticleConfig(ParticleTypes.TOTEM_OF_UNDYING, 10, 0.5f));
        EFFECT_PARTICLES.put(JewelEffect.FIRE_ASPECT, new ParticleConfig(ParticleTypes.FLAME, 8, 0.4f));
        EFFECT_PARTICLES.put(JewelEffect.KNOCKBACK, new ParticleConfig(ParticleTypes.EXPLOSION, 1, 0.0f));
        EFFECT_PARTICLES.put(JewelEffect.SWEEPING, new ParticleConfig(ParticleTypes.SWEEP_ATTACK, 1, 0.8f));
        EFFECT_PARTICLES.put(JewelEffect.CRITICAL_CHANCE, new ParticleConfig(ParticleTypes.CRIT, 10, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.LIFESTEAL, new ParticleConfig(ParticleTypes.HEART, 3, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.EXECUTE, new ParticleConfig(ParticleTypes.SOUL, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.POISON, new ParticleConfig(ParticleTypes.MYCELIUM, 8, 0.4f));
        EFFECT_PARTICLES.put(JewelEffect.WEAKNESS, new ParticleConfig(ParticleTypes.ASH, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.BEHEADING, new ParticleConfig(ParticleTypes.SOUL_FIRE_FLAME, 5, 0.2f));

        // Universal Effects
        EFFECT_PARTICLES.put(JewelEffect.DURABILITY, new ParticleConfig(ParticleTypes.WAX_ON, 3, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.SELF_REPAIR, new ParticleConfig(ParticleTypes.SCRAPE, 5, 0.1f));
        EFFECT_PARTICLES.put(JewelEffect.EXPERIENCE_BOOST, new ParticleConfig(ParticleTypes.ENCHANT, 10, 0.5f));
        EFFECT_PARTICLES.put(JewelEffect.REACH, new ParticleConfig(ParticleTypes.REVERSE_PORTAL, 5, 0.3f));

        // Player Effects
        EFFECT_PARTICLES.put(JewelEffect.NIGHT_VISION, new ParticleConfig(ParticleTypes.GLOW, 3, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.WATER_BREATHING, new ParticleConfig(ParticleTypes.BUBBLE, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.SPEED, new ParticleConfig(ParticleTypes.POOF, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.HASTE, new ParticleConfig(ParticleTypes.INSTANT_EFFECT, 3, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.RESISTANCE, new ParticleConfig(ParticleTypes.ENCHANTED_HIT, 5, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.REGENERATION, new ParticleConfig(ParticleTypes.HEART, 2, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.JUMP_BOOST, new ParticleConfig(ParticleTypes.CLOUD, 3, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.FIRE_RESISTANCE, new ParticleConfig(ParticleTypes.LAVA, 2, 0.1f));

        // Special Effects
        EFFECT_PARTICLES.put(JewelEffect.VOID_TOUCH, new ParticleConfig(ParticleTypes.DRAGON_BREATH, 10, 0.5f));
        EFFECT_PARTICLES.put(JewelEffect.ABSORPTION, new ParticleConfig(ParticleTypes.TOTEM_OF_UNDYING, 5, 0.3f));
        EFFECT_PARTICLES.put(JewelEffect.TELEPORT_DROPS, new ParticleConfig(ParticleTypes.REVERSE_PORTAL, 8, 0.4f));
        EFFECT_PARTICLES.put(JewelEffect.SOULBOUND, new ParticleConfig(ParticleTypes.SOUL, 3, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.CURSE_BINDING, new ParticleConfig(ParticleTypes.SMOKE, 5, 0.2f));
        EFFECT_PARTICLES.put(JewelEffect.GRAVITY_TETHER, new ParticleConfig(ParticleTypes.REVERSE_PORTAL, 10, 0.6f));
    }

    /**
     * Spawn particles for an effect trigger at entity position
     */
    public static void spawnEffectParticles(Level level, Entity entity, JewelEffect effect) {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            ParticleConfig config = EFFECT_PARTICLES.get(effect);
            if (config != null) {
                Vec3 pos = entity.position();
                spawnParticles(clientLevel, pos, config);
            }
        }
    }

    /**
     * Spawn particles for an effect trigger at specific position
     */
    public static void spawnEffectParticles(Level level, Vec3 pos, JewelEffect effect) {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            ParticleConfig config = EFFECT_PARTICLES.get(effect);
            if (config != null) {
                spawnParticles(clientLevel, pos, config);
            }
        }
    }

    /**
     * Spawn particles for multiple effects (combined jewels)
     */
    public static void spawnMultiEffectParticles(Level level, Vec3 pos, JewelEffect... effects) {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            for (JewelEffect effect : effects) {
                ParticleConfig config = EFFECT_PARTICLES.get(effect);
                if (config != null) {
                    spawnParticles(clientLevel, pos, new ParticleConfig(config.particleType, config.count / 2, config.spread));
                }
            }
        }
    }

    /**
     * Spawn continuous particles for active effects (like player buffs)
     */
    public static void spawnContinuousParticles(Player player, JewelEffect effect) {
        if (player.level().isClientSide() && player.level() instanceof ClientLevel clientLevel) {
            ParticleConfig config = EFFECT_PARTICLES.get(effect);
            if (config != null) {
                RandomSource random = player.getRandom();
                if (random.nextFloat() < 0.1f) { // 10% chance per tick
                    Vec3 pos = player.position().add(
                        random.nextGaussian() * 0.3,
                        player.getBbHeight() / 2 + random.nextGaussian() * 0.3,
                        random.nextGaussian() * 0.3
                    );
                    spawnParticles(clientLevel, pos, new ParticleConfig(config.particleType, 1, 0.1f));
                }
            }
        }
    }

    /**
     * Spawn a burst of particles for critical effects
     */
    public static void spawnCriticalBurst(Level level, Vec3 pos, JewelEffect effect) {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            ParticleConfig config = EFFECT_PARTICLES.get(effect);
            if (config != null) {
                // Triple particle count for critical burst
                spawnParticles(clientLevel, pos, new ParticleConfig(config.particleType, config.count * 3, config.spread * 1.5f));
            }
        }
    }

    private static void spawnParticles(ClientLevel level, Vec3 pos, ParticleConfig config) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < config.count; i++) {
            double x = pos.x + random.nextGaussian() * config.spread;
            double y = pos.y + random.nextGaussian() * config.spread + 0.5;
            double z = pos.z + random.nextGaussian() * config.spread;
            double vx = random.nextGaussian() * 0.02;
            double vy = random.nextGaussian() * 0.02 + 0.05;
            double vz = random.nextGaussian() * 0.02;
            level.addParticle(config.particleType, x, y, z, vx, vy, vz);
        }
    }

    private static class ParticleConfig {
        final SimpleParticleType particleType;
        final int count;
        final float spread;

        ParticleConfig(SimpleParticleType particleType, int count, float spread) {
            this.particleType = particleType;
            this.count = count;
            this.spread = spread;
        }
    }
}
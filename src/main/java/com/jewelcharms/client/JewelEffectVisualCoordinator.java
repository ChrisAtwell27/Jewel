package com.jewelcharms.client;

import com.jewelcharms.client.particle.JewelEffectParticles;
import com.jewelcharms.client.sound.JewelSoundManager;
import com.jewelcharms.effect.JewelEffect;
import com.jewelcharms.network.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

/**
 * Coordinates visual and audio effects for jewel triggers
 * Provides server-safe methods that delegate to client-only implementations
 */
public class JewelEffectVisualCoordinator {

    /**
     * Trigger visual and audio effects when a jewel effect activates
     * Safe to call from server - will only execute on client
     */
    public static void triggerEffect(Level level, Vec3 pos, JewelEffect effect) {
        if (!level.isClientSide()) {
            // Send packet to nearby clients to trigger effects
            // This would need a packet implementation
            return;
        }

        // Execute on client side only
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            triggerClientEffect(level, pos, effect);
        });
    }

    /**
     * Trigger effects at entity position
     */
    public static void triggerEffectAtEntity(Entity entity, JewelEffect effect) {
        if (entity.level().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                triggerClientEffect(entity.level(), entity.position(), effect);
            });
        }
    }

    /**
     * Trigger effects at block position
     */
    public static void triggerEffectAtBlock(Level level, BlockPos pos, JewelEffect effect) {
        Vec3 vec = Vec3.atCenterOf(pos);
        triggerEffect(level, vec, effect);
    }

    /**
     * Trigger multiple effects (for combined jewels)
     */
    public static void triggerMultipleEffects(Level level, Vec3 pos, JewelEffect... effects) {
        if (!level.isClientSide()) {
            return;
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            for (JewelEffect effect : effects) {
                triggerClientEffect(level, pos, effect);
            }
        });
    }

    /**
     * Trigger critical/powerful version of effect
     */
    public static void triggerCriticalEffect(Level level, Vec3 pos, JewelEffect effect) {
        if (!level.isClientSide()) {
            return;
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            // Enhanced particles
            JewelEffectParticles.spawnCriticalBurst(level, pos, effect);
            // Louder sound
            JewelSoundManager.playEffectSound(level, pos, effect, 1.0f, 1.2f);
        });
    }

    /**
     * Trigger continuous effects for player buffs
     */
    public static void triggerContinuousEffect(Player player, JewelEffect effect) {
        if (player.level().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                JewelEffectParticles.spawnContinuousParticles(player, effect);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void triggerClientEffect(Level level, Vec3 pos, JewelEffect effect) {
        // Spawn particles
        JewelEffectParticles.spawnEffectParticles(level, pos, effect);
        // Play sound
        JewelSoundManager.playEffectSound(level, pos, effect, 0.7f, 1.0f);
    }

    // ========== SPECIFIC EFFECT TRIGGERS ==========

    /**
     * Fortune effect trigger - extra sparkles
     */
    public static void triggerFortuneEffect(Level level, BlockPos pos, int fortuneLevel) {
        if (level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Vec3 vec = Vec3.atCenterOf(pos);
                for (int i = 0; i < fortuneLevel; i++) {
                    JewelEffectParticles.spawnEffectParticles(level, vec, JewelEffect.FORTUNE);
                }
                JewelSoundManager.playEffectSound(level, vec, JewelEffect.FORTUNE, 0.5f + fortuneLevel * 0.1f, 1.0f);
            });
        }
    }

    /**
     * Auto-smelt effect trigger - fire particles
     */
    public static void triggerAutoSmeltEffect(Level level, BlockPos pos) {
        triggerEffectAtBlock(level, pos, JewelEffect.AUTO_SMELT);
    }

    /**
     * Vein miner effect - sweep attack particles
     */
    public static void triggerVeinMinerEffect(Level level, BlockPos pos, int blockCount) {
        if (level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Vec3 vec = Vec3.atCenterOf(pos);
                JewelEffectParticles.spawnCriticalBurst(level, vec, JewelEffect.AOE_MINING);
                float volume = Math.min(0.3f + blockCount * 0.05f, 1.0f);
                JewelSoundManager.playEffectSound(level, vec, JewelEffect.AOE_MINING, volume, 0.8f);
            });
        }
    }

    /**
     * Lifesteal effect - healing particles
     */
    public static void triggerLifestealEffect(Entity attacker, Entity target, float healAmount) {
        if (attacker.level().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                JewelEffectParticles.spawnEffectParticles(attacker.level(), attacker.position(), JewelEffect.LIFESTEAL);
                JewelEffectParticles.spawnEffectParticles(target.level(), target.position(), JewelEffect.LIFESTEAL);
                JewelSoundManager.playEffectSoundAtEntity(attacker, JewelEffect.LIFESTEAL);
            });
        }
    }

    /**
     * Execute effect - death particles
     */
    public static void triggerExecuteEffect(Entity target) {
        if (target.level().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                JewelEffectParticles.spawnCriticalBurst(target.level(), target.position(), JewelEffect.EXECUTE);
                JewelSoundManager.playEffectSound(target.level(), target.position(), JewelEffect.EXECUTE, 1.5f, 0.5f);
            });
        }
    }

    /**
     * Buff application effect
     */
    public static void triggerBuffApplication(Player player, JewelEffect effect) {
        if (player.level().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                JewelEffectParticles.spawnEffectParticles(player.level(), player.position(), effect);
                JewelSoundManager.playBuffApply(player);
            });
        }
    }

    /**
     * Gravity tether effect
     */
    public static void triggerGravityTetherEffect(Entity item, Player player) {
        if (item.level().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Create particle trail from item to player
                Vec3 itemPos = item.position();
                Vec3 playerPos = player.position();
                Vec3 direction = playerPos.subtract(itemPos).normalize();

                for (int i = 0; i < 10; i++) {
                    Vec3 particlePos = itemPos.add(direction.scale(i * 0.5));
                    JewelEffectParticles.spawnEffectParticles(item.level(), particlePos, JewelEffect.GRAVITY_TETHER);
                }

                if (item.tickCount % 20 == 0) { // Play sound every second
                    JewelSoundManager.playEffectSound(item.level(), itemPos, JewelEffect.GRAVITY_TETHER, 0.3f, 1.5f);
                }
            });
        }
    }

    /**
     * Void touch effect - destruction particles
     */
    public static void triggerVoidTouchEffect(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Vec3 vec = Vec3.atCenterOf(pos);
                JewelEffectParticles.spawnCriticalBurst(level, vec, JewelEffect.VOID_TOUCH);
                JewelSoundManager.playEffectSound(level, vec, JewelEffect.VOID_TOUCH, 0.8f, 0.7f);
            });
        }
    }
}
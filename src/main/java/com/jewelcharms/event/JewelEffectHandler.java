package com.jewelcharms.event;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.effect.*;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import java.util.*;

@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JewelEffectHandler {

    private static final Random RANDOM = new Random();
    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("6b5e7a3c-8f9d-4e2a-b1c3-5d6e7f8a9b0c");

    // ========== UTILITY METHODS ==========

    private static boolean isTool(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof PickaxeItem || item instanceof AxeItem ||
               item instanceof ShovelItem || item instanceof HoeItem;
    }

    private static boolean isWeapon(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof TridentItem || item instanceof BowItem;
    }

    private static void damageToolWithUnbreaking(ItemStack tool, Player player, Map<JewelEffect, Integer> effects) {
        // Check for Unbreaking effect
        if (effects.containsKey(JewelEffect.DURABILITY)) {
            int unbreakingLevel = effects.get(JewelEffect.DURABILITY);
            // Chance to NOT damage: (unbreakingLevel / (unbreakingLevel + 1))
            // Level 1: 50% chance, Level 2: 66% chance, Level 3: 75% chance
            float chance = 1.0f - (1.0f / (unbreakingLevel + 1));
            if (RANDOM.nextFloat() < chance) {
                return; // Don't damage the tool
            }
        }
        tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(net.minecraft.world.entity.EquipmentSlot.MAINHAND));
    }

    private static Map<JewelEffect, Integer> getApplicableEffects(ItemStack stack) {
        List<ToolJewelData.AttachedJewel> jewels = ToolJewelData.getAttachedJewels(stack);
        Map<JewelEffect, Integer> totalEffects = new HashMap<>();

        boolean isWeapon = isWeapon(stack);
        boolean isTool = isTool(stack);

        for (ToolJewelData.AttachedJewel jewel : jewels) {
            for (Map.Entry<JewelEffect, Integer> entry : jewel.getEffects().entrySet()) {
                JewelEffect effect = entry.getKey();

                // Check if effect is applicable to this item type
                if ((isWeapon && effect.isApplicableToWeapon()) ||
                    (isTool && effect.isApplicableToTool())) {
                    totalEffects.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
        }

        return totalEffects;
    }

    // ========== MINING & TOOL EFFECTS ==========

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty() || !isTool(tool)) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(tool);
        if (effects.isEmpty()) {
            return;
        }

        // Mining Speed
        if (effects.containsKey(JewelEffect.MINING_SPEED)) {
            int level = effects.get(JewelEffect.MINING_SPEED);
            float speedMultiplier = 1.0f + (level * 0.25f); // 25% per level
            event.setNewSpeed(event.getOriginalSpeed() * speedMultiplier);
        }

        // Apply Haste effect
        if (effects.containsKey(JewelEffect.HASTE) && player instanceof ServerPlayer) {
            int level = effects.get(JewelEffect.HASTE);
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, level - 1, false, false));
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (tool.isEmpty() || !isTool(tool) || level.isClientSide) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(tool);
        if (effects.isEmpty()) {
            return;
        }

        // Silk Touch - drop the block itself
        if (effects.containsKey(JewelEffect.SILK_TOUCH)) {
            event.setCanceled(true);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            Block.popResource(level, pos, new ItemStack(state.getBlock()));
            tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(net.minecraft.world.entity.EquipmentSlot.MAINHAND));
            return; // Don't apply other effects if silk touch is active
        }

        // Void Touch (destroys blocks, no drops)
        if (effects.containsKey(JewelEffect.VOID_TOUCH)) {
            event.setCanceled(true);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        // Fortune - multiply drops (handled via Block.getExpDrop modification)
        if (effects.containsKey(JewelEffect.FORTUNE)) {
            int fortuneLevel = effects.get(JewelEffect.FORTUNE);
            handleFortune(level, pos, state, player, fortuneLevel);
        }

        // Auto-Smelt
        if (effects.containsKey(JewelEffect.AUTO_SMELT)) {
            handleAutoSmelt(level, pos, state, player);
        }

        // Vein Miner / AOE Mining
        if (effects.containsKey(JewelEffect.AOE_MINING)) {
            int range = effects.get(JewelEffect.AOE_MINING);
            handleVeinMiner(level, pos, state, player, tool, range);
        }

        // Tree Felling
        if (effects.containsKey(JewelEffect.TREE_FELLING) && state.is(BlockTags.LOGS)) {
            handleTreeFelling(level, pos, player, tool);
        }

        // Auto-Replant
        if (effects.containsKey(JewelEffect.REPLANTING) && state.getBlock() instanceof CropBlock) {
            handleAutoReplant(level, pos, state);
        }

        // Item Magnet
        if (effects.containsKey(JewelEffect.MAGNETIC)) {
            int range = effects.get(JewelEffect.MAGNETIC);
            handleMagneticItems(level, pos, player, range);
        }

        // Teleport Drops (Ender Pocket)
        if (effects.containsKey(JewelEffect.TELEPORT_DROPS)) {
            handleTeleportDrops(level, pos, player);
        }

        // === NEW UNIQUE ABILITIES ===

        // Quantum Harvest - swap crop with another crop in the world
        if (effects.containsKey(JewelEffect.QUANTUM_HARVEST) && state.getBlock() instanceof CropBlock) {
            handleQuantumHarvest((ServerLevel) level, pos, state, player);
        }

        // Biome Resonance - apply biome-specific effects
        if (effects.containsKey(JewelEffect.BIOME_RESONANCE)) {
            int biomeLevel = effects.get(JewelEffect.BIOME_RESONANCE);
            handleBiomeResonance(level, pos, player, tool, biomeLevel);
        }

        // Domino Effect - progressive drop multiplier
        if (effects.containsKey(JewelEffect.DOMINO_EFFECT)) {
            handleDominoEffect(level, pos, state, player);
        }

        // Sound Harvesting - generate musical notes and bonus loot
        if (effects.containsKey(JewelEffect.SOUND_HARVESTING)) {
            int soundLevel = effects.get(JewelEffect.SOUND_HARVESTING);
            handleSoundHarvesting(level, pos, state, player, soundLevel);
        }
    }

    // ========== COMBAT & WEAPON EFFECTS ==========

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(weapon);
        if (effects.isEmpty()) {
            return;
        }

        LivingEntity target = event.getEntity();
        float damage = event.getAmount();

        // === NEW UNIQUE ABILITIES - COMBAT MODIFIERS ===

        // Moon Phase Sensitivity - bonus damage based on moon phase
        if (effects.containsKey(JewelEffect.MOON_PHASE_SENSITIVITY) && player.level() instanceof ServerLevel serverLevel) {
            int moonLevel = effects.get(JewelEffect.MOON_PHASE_SENSITIVITY);
            float moonBonus = getMoonPhaseBonus(serverLevel, moonLevel);
            damage *= moonBonus;
        }

        // Biome Resonance - bonus damage based on biome
        if (effects.containsKey(JewelEffect.BIOME_RESONANCE) && isWeapon(weapon)) {
            int biomeLevel = effects.get(JewelEffect.BIOME_RESONANCE);
            damage = applyBiomeResonanceCombat(player.level(), player.blockPosition(), damage, target, biomeLevel);
        }

        // Weather Siphon - bonus damage based on charge
        if (effects.containsKey(JewelEffect.WEATHER_SIPHON)) {
            float chargeMultiplier = WeatherSiphonTracker.getDamageMultiplier(weapon);
            damage *= chargeMultiplier;
        }

        // Damage / Sharpness
        if (effects.containsKey(JewelEffect.DAMAGE)) {
            int level = effects.get(JewelEffect.DAMAGE);
            damage += level * 1.25f; // +1.25 damage per level
        }

        // Critical Strike
        if (effects.containsKey(JewelEffect.CRITICAL_CHANCE)) {
            int level = effects.get(JewelEffect.CRITICAL_CHANCE);
            float critChance = level * 0.1f; // 10% per level
            if (RANDOM.nextFloat() < critChance) {
                damage *= 1.5f; // 50% bonus damage
                JewelCharms.LOGGER.debug("Critical hit! Damage: {}", damage);
            }
        }

        // Execute (extra damage to low health enemies)
        if (effects.containsKey(JewelEffect.EXECUTE)) {
            float healthPercent = target.getHealth() / target.getMaxHealth();
            if (healthPercent < 0.3f) { // Below 30% health
                int level = effects.get(JewelEffect.EXECUTE);
                damage += level * 3.0f; // Significant bonus damage
            }
        }

        // Life Steal
        if (effects.containsKey(JewelEffect.LIFESTEAL) && player instanceof ServerPlayer) {
            int level = effects.get(JewelEffect.LIFESTEAL);
            float healAmount = damage * (level * 0.1f); // 10% per level
            player.heal(healAmount);
        }

        // Fire Aspect
        if (effects.containsKey(JewelEffect.FIRE_ASPECT)) {
            int level = effects.get(JewelEffect.FIRE_ASPECT);
            target.setSecondsOnFire(level * 4); // 4 seconds per level
        }

        // Knockback
        if (effects.containsKey(JewelEffect.KNOCKBACK)) {
            int level = effects.get(JewelEffect.KNOCKBACK);
            target.knockback(level * 0.5f, player.getX() - target.getX(), player.getZ() - target.getZ());
        }

        // Sweeping Edge - enhance sweep attack damage
        if (effects.containsKey(JewelEffect.SWEEPING) && weapon.getItem() instanceof SwordItem) {
            int sweepLevel = effects.get(JewelEffect.SWEEPING);
            // Check if this is a sweep attack (player on ground, not sprinting, has attacked recently)
            if (player.onGround() && !player.isSprinting()) {
                // Add extra damage for sweep attacks
                float sweepBonus = sweepLevel * 1.0f; // +1 damage per level
                damage += sweepBonus;

                // Apply damage to nearby entities (sweep radius)
                double sweepRadius = 1.0 + (sweepLevel * 0.5); // Increase radius with level
                AABB sweepArea = target.getBoundingBox().inflate(sweepRadius, 0.25, sweepRadius);
                java.util.List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                    LivingEntity.class, sweepArea,
                    entity -> entity != target && entity != player && !entity.isAlliedTo(player)
                );

                for (LivingEntity nearbyEntity : nearbyEntities) {
                    nearbyEntity.hurt(player.damageSources().playerAttack(player), sweepBonus);
                }
            }
        }

        // Poison
        if (effects.containsKey(JewelEffect.POISON)) {
            int level = effects.get(JewelEffect.POISON);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, level - 1));
        }

        // Weakness
        if (effects.containsKey(JewelEffect.WEAKNESS)) {
            int level = effects.get(JewelEffect.WEAKNESS);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, level - 1));
        }

        event.setAmount(damage);
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(weapon);
        if (effects.isEmpty()) {
            return;
        }

        LivingEntity target = event.getEntity();

        // Looting (extra mob drops)
        if (effects.containsKey(JewelEffect.LOOTING)) {
            int lootingLevel = effects.get(JewelEffect.LOOTING);
            handleLooting(target, lootingLevel);
        }

        // Beheading (increased head drop chance)
        if (effects.containsKey(JewelEffect.BEHEADING)) {
            int level = effects.get(JewelEffect.BEHEADING);
            float headChance = level * 0.05f; // 5% per level
            if (RANDOM.nextFloat() < headChance) {
                handleBeheading(target);
            }
        }

        // Gravity Tether - make drops orbit player
        if (effects.containsKey(JewelEffect.GRAVITY_TETHER)) {
            handleGravityTether(target, player);
        }
    }

    // ========== XP BOOST ==========

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty()) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(heldItem);
        if (effects.containsKey(JewelEffect.EXPERIENCE_BOOST)) {
            int level = effects.get(JewelEffect.EXPERIENCE_BOOST);
            ExperienceOrb orb = event.getOrb();
            int originalXp = orb.value;
            int bonusXp = originalXp * level; // Multiply XP by level
            orb.value = originalXp + bonusXp;
        }
    }

    // ========== CURSE OF BINDING (Prevent Item Removal) ==========

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemStack tossedItem = event.getEntity().getItem();
        if (tossedItem.isEmpty()) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(tossedItem);
        if (effects.containsKey(JewelEffect.CURSE_BINDING)) {
            // Cancel the toss event
            event.setCanceled(true);

            // Send message to player
            Player player = event.getPlayer();
            if (player != null) {
                player.displayClientMessage(
                    Component.literal("This item is cursed and cannot be dropped!").withStyle(net.minecraft.ChatFormatting.RED),
                    true // Action bar
                );
            }
        }
    }

    // ========== SOULBOUND (Keep Items on Death) ==========

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            // Copy soulbound items to new player
            for (int i = 0; i < oldPlayer.getInventory().getContainerSize(); i++) {
                ItemStack stack = oldPlayer.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    Map<JewelEffect, Integer> effects = getApplicableEffects(stack);
                    if (effects.containsKey(JewelEffect.SOULBOUND)) {
                        // Keep this item
                        newPlayer.getInventory().setItem(i, stack.copy());
                    }
                }
            }
        }
    }

    // ========== PLAYER EFFECTS (When Holding) ==========

    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;

        // Update Gravity Tether orbiting items (regardless of held item)
        if (!player.level().isClientSide) {
            GravityTetherTracker.updateOrbitingItems(player);
        }

        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty()) {
            return;
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(heldItem);
        if (effects.isEmpty()) {
            return;
        }

        // Weather Siphon - update charge based on weather
        if (effects.containsKey(JewelEffect.WEATHER_SIPHON) && !player.level().isClientSide) {
            Level level = player.level();
            BlockPos pos = player.blockPosition();
            boolean isRaining = level.isRaining();
            boolean isThundering = level.isThundering();
            boolean canSeeSky = level.canSeeSky(pos);
            boolean isSunny = !isRaining && level.isDay() && canSeeSky;

            WeatherSiphonTracker.updateCharge(heldItem, isRaining && canSeeSky, isThundering && canSeeSky, isSunny);
        }

        // Apply player effects every 20 ticks (1 second)
        if (player.tickCount % 20 != 0) {
            return;
        }

        // Night Vision
        if (effects.containsKey(JewelEffect.NIGHT_VISION)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false, false));
        }

        // Water Breathing
        if (effects.containsKey(JewelEffect.WATER_BREATHING)) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0, false, false, false));
        }

        // Speed
        if (effects.containsKey(JewelEffect.SPEED)) {
            int level = effects.get(JewelEffect.SPEED);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, level - 1, false, false, false));
        }

        // Resistance
        if (effects.containsKey(JewelEffect.RESISTANCE)) {
            int level = effects.get(JewelEffect.RESISTANCE);
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, level - 1, false, false, false));
        }

        // Regeneration
        if (effects.containsKey(JewelEffect.REGENERATION)) {
            int level = effects.get(JewelEffect.REGENERATION);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, level - 1, false, false, false));
        }

        // Jump Boost
        if (effects.containsKey(JewelEffect.JUMP_BOOST)) {
            int level = effects.get(JewelEffect.JUMP_BOOST);
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, level - 1, false, false, false));
        }

        // Fire Resistance
        if (effects.containsKey(JewelEffect.FIRE_RESISTANCE)) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0, false, false, false));
        }

        // Absorption
        if (effects.containsKey(JewelEffect.ABSORPTION)) {
            int level = effects.get(JewelEffect.ABSORPTION);
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, level - 1, false, false, false));
        }

        // Self-Repair (Mending)
        if (effects.containsKey(JewelEffect.SELF_REPAIR) && heldItem.isDamaged()) {
            if (player.tickCount % 100 == 0) { // Every 5 seconds
                int repairAmount = effects.get(JewelEffect.SELF_REPAIR);
                heldItem.setDamageValue(Math.max(0, heldItem.getDamageValue() - repairAmount));
            }
        }

        // Extended Reach - apply attribute modifiers
        AttributeInstance blockReach = player.getAttribute(ForgeMod.BLOCK_REACH.get());
        AttributeInstance entityReach = player.getAttribute(ForgeMod.ENTITY_REACH.get());

        if (effects.containsKey(JewelEffect.REACH)) {
            int level = effects.get(JewelEffect.REACH);
            double reachBonus = level * 1.0; // +1 block per level

            // Add or update reach modifier
            if (blockReach != null && blockReach.getModifier(REACH_MODIFIER_UUID) == null) {
                blockReach.addTransientModifier(new AttributeModifier(REACH_MODIFIER_UUID, "Jewel Reach Bonus", reachBonus, AttributeModifier.Operation.ADDITION));
            }
            if (entityReach != null && entityReach.getModifier(REACH_MODIFIER_UUID) == null) {
                entityReach.addTransientModifier(new AttributeModifier(REACH_MODIFIER_UUID, "Jewel Reach Bonus", reachBonus, AttributeModifier.Operation.ADDITION));
            }
        } else {
            // Remove reach modifier if effect is no longer present
            if (blockReach != null && blockReach.getModifier(REACH_MODIFIER_UUID) != null) {
                blockReach.removeModifier(REACH_MODIFIER_UUID);
            }
            if (entityReach != null && entityReach.getModifier(REACH_MODIFIER_UUID) != null) {
                entityReach.removeModifier(REACH_MODIFIER_UUID);
            }
        }
    }

    // ========== HELPER METHODS FOR SPECIAL EFFECTS ==========

    private static void handleFortune(Level level, BlockPos pos, BlockState state, Player player, int fortuneLevel) {
        // Fortune multiplies drops for certain blocks
        Block block = state.getBlock();

        // Blocks that benefit from fortune
        Map<Block, Item> fortuneBlocks = Map.ofEntries(
            Map.entry(Blocks.COAL_ORE, Items.COAL),
            Map.entry(Blocks.DEEPSLATE_COAL_ORE, Items.COAL),
            Map.entry(Blocks.DIAMOND_ORE, Items.DIAMOND),
            Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, Items.DIAMOND),
            Map.entry(Blocks.EMERALD_ORE, Items.EMERALD),
            Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, Items.EMERALD),
            Map.entry(Blocks.LAPIS_ORE, Items.LAPIS_LAZULI),
            Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, Items.LAPIS_LAZULI),
            Map.entry(Blocks.REDSTONE_ORE, Items.REDSTONE),
            Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, Items.REDSTONE),
            Map.entry(Blocks.NETHER_QUARTZ_ORE, Items.QUARTZ)
        );

        if (fortuneBlocks.containsKey(block)) {
            // Add extra drops based on fortune level
            int extraDrops = RANDOM.nextInt(fortuneLevel + 1);
            for (int i = 0; i < extraDrops; i++) {
                Block.popResource(level, pos, new ItemStack(fortuneBlocks.get(block)));
            }
        }
    }

    private static void handleAutoSmelt(Level level, BlockPos pos, BlockState state, Player player) {
        // This is a simplified version - would need proper furnace recipe lookup
        Block block = state.getBlock();

        Map<Block, Item> smeltMap = Map.of(
            Blocks.IRON_ORE, Items.IRON_INGOT,
            Blocks.DEEPSLATE_IRON_ORE, Items.IRON_INGOT,
            Blocks.GOLD_ORE, Items.GOLD_INGOT,
            Blocks.DEEPSLATE_GOLD_ORE, Items.GOLD_INGOT,
            Blocks.COPPER_ORE, Items.COPPER_INGOT,
            Blocks.DEEPSLATE_COPPER_ORE, Items.COPPER_INGOT,
            Blocks.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP
        );

        if (smeltMap.containsKey(block)) {
            ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(smeltMap.get(block)));
            level.addFreshEntity(drop);
        }
    }

    private static void handleVeinMiner(Level level, BlockPos pos, BlockState state, Player player, ItemStack tool, int range) {
        // Mine adjacent blocks of the same type
        Set<BlockPos> toMine = new HashSet<>();
        Block targetBlock = state.getBlock();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                        toMine.add(checkPos);
                        if (toMine.size() >= 8) break; // Limit to prevent lag
                    }
                }
            }
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(tool);
        for (BlockPos minePos : toMine) {
            level.destroyBlock(minePos, true, player);
            damageToolWithUnbreaking(tool, player, effects);
        }
    }

    private static void handleTreeFelling(Level level, BlockPos pos, Player player, ItemStack tool) {
        // Find and break all connected logs
        Set<BlockPos> logs = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();
        toCheck.add(pos);

        while (!toCheck.isEmpty() && logs.size() < 100) { // Limit to prevent lag
            BlockPos checkPos = toCheck.poll();
            if (logs.contains(checkPos)) continue;

            BlockState state = level.getBlockState(checkPos);
            if (state.is(BlockTags.LOGS)) {
                logs.add(checkPos);

                // Check adjacent blocks
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            toCheck.add(checkPos.offset(x, y, z));
                        }
                    }
                }
            }
        }

        Map<JewelEffect, Integer> effects = getApplicableEffects(tool);
        for (BlockPos logPos : logs) {
            if (!logPos.equals(pos)) {
                level.destroyBlock(logPos, true, player);
                damageToolWithUnbreaking(tool, player, effects);
            }
        }
    }

    private static void handleAutoReplant(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock crop) {
            level.setBlock(pos, crop.defaultBlockState(), 3);
        }
    }

    private static void handleMagneticItems(Level level, BlockPos pos, Player player, int range) {
        AABB area = new AABB(pos).inflate(range);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity item : items) {
            item.setPos(player.getX(), player.getY(), player.getZ());
        }
    }

    private static void handleTeleportDrops(Level level, BlockPos pos, Player player) {
        // Teleport drops directly to player inventory
        AABB area = new AABB(pos).inflate(1);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity item : items) {
            player.getInventory().add(item.getItem());
            item.discard();
        }
    }

    private static void handleLooting(LivingEntity target, int lootingLevel) {
        // Spawn extra common drops from mobs based on looting level
        Level level = target.level();
        Map<String, Item> mobDrops = Map.of(
            "zombie", Items.ROTTEN_FLESH,
            "skeleton", Items.BONE,
            "spider", Items.STRING,
            "enderman", Items.ENDER_PEARL,
            "blaze", Items.BLAZE_ROD,
            "creeper", Items.GUNPOWDER
        );

        String mobType = target.getType().toString();
        if (mobDrops.containsKey(mobType)) {
            // Add 0-lootingLevel extra drops
            int extraDrops = RANDOM.nextInt(lootingLevel + 1);
            for (int i = 0; i < extraDrops; i++) {
                ItemEntity drop = new ItemEntity(level, target.getX(), target.getY(), target.getZ(),
                    new ItemStack(mobDrops.get(mobType)));
                level.addFreshEntity(drop);
            }
        }
    }

    private static void handleBeheading(LivingEntity target) {
        // Drop appropriate head based on entity type
        // This is a simplified version
        ItemStack head = null;

        switch (target.getType().toString()) {
            case "skeleton" -> head = new ItemStack(Items.SKELETON_SKULL);
            case "zombie" -> head = new ItemStack(Items.ZOMBIE_HEAD);
            case "creeper" -> head = new ItemStack(Items.CREEPER_HEAD);
            case "wither_skeleton" -> head = new ItemStack(Items.WITHER_SKELETON_SKULL);
            case "player" -> head = new ItemStack(Items.PLAYER_HEAD);
        }

        if (head != null) {
            ItemEntity drop = new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), head);
            target.level().addFreshEntity(drop);
        }
    }

    // ========== NEW UNIQUE ABILITY HELPERS ==========

    /**
     * Quantum Harvest - Swap crop with another crop elsewhere in the world
     */
    private static void handleQuantumHarvest(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        if (!(state.getBlock() instanceof CropBlock crop)) {
            return;
        }

        // Find another crop within a large radius (100 blocks)
        BlockPos searchCenter = pos;
        List<BlockPos> cropPositions = new ArrayList<>();

        // Search for other crops in a 100-block radius
        for (int x = -50; x <= 50; x += 5) {
            for (int z = -50; z <= 50; z += 5) {
                for (int y = -10; y <= 10; y += 2) {
                    BlockPos checkPos = searchCenter.offset(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (checkState.getBlock() instanceof CropBlock && !checkPos.equals(pos)) {
                        cropPositions.add(checkPos);
                        if (cropPositions.size() >= 20) break; // Limit search
                    }
                }
                if (cropPositions.size() >= 20) break;
            }
            if (cropPositions.size() >= 20) break;
        }

        if (!cropPositions.isEmpty()) {
            // Pick a random crop to swap with
            BlockPos swapPos = cropPositions.get(RANDOM.nextInt(cropPositions.size()));
            BlockState swapState = level.getBlockState(swapPos);

            // Harvest both crops
            level.destroyBlock(pos, true, player);
            level.destroyBlock(swapPos, true, player);

            // Send message to player
            player.displayClientMessage(
                Component.literal("Quantum Harvest! Swapped crops at " + swapPos.toShortString())
                    .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE),
                true
            );
        }
    }

    /**
     * Biome Resonance - Apply biome-specific effects when breaking blocks
     */
    private static void handleBiomeResonance(Level level, BlockPos pos, Player player, ItemStack tool, int biomeLevel) {
        Holder<Biome> biomeHolder = level.getBiome(pos);
        Biome biome = biomeHolder.value();

        // Get biome properties to determine type
        boolean isCold = biome.coldEnoughToSnow(pos);
        boolean isHot = biome.getBaseTemperature() > 1.0f;
        boolean isOcean = biomeHolder.is(net.minecraft.tags.BiomeTags.IS_OCEAN);
        boolean isForest = biomeHolder.is(net.minecraft.tags.BiomeTags.IS_FOREST);

        // Apply biome-specific effects
        if (isCold) {
            // Cold biomes: Freeze nearby water, slowness on nearby mobs
            freezeNearbyWater(level, pos, biomeLevel);
            applySlownessToNearbyMobs(level, pos, player, biomeLevel);
        } else if (isHot) {
            // Hot biomes: Set fire to nearby blocks/mobs
            igniteNearbyEntities(level, pos, player, biomeLevel);
        } else if (isOcean) {
            // Ocean biomes: Water breathing, dolphins grace
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 100, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100, 0, false, false));
        } else if (isForest) {
            // Forest biomes: Extra plant drops, regeneration
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, false));
        }
    }

    /**
     * Apply Biome Resonance effects to combat
     */
    private static float applyBiomeResonanceCombat(Level level, BlockPos pos, float damage, LivingEntity target, int biomeLevel) {
        Holder<Biome> biomeHolder = level.getBiome(pos);
        Biome biome = biomeHolder.value();

        boolean isCold = biome.coldEnoughToSnow(pos);
        boolean isHot = biome.getBaseTemperature() > 1.0f;
        boolean isOcean = biomeHolder.is(net.minecraft.tags.BiomeTags.IS_OCEAN);

        // Apply biome-specific combat effects
        if (isCold) {
            // Cold biomes: Ice damage, slowness
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, biomeLevel - 1));
            damage *= 1.0f + (biomeLevel * 0.15f); // +15% per level
        } else if (isHot) {
            // Hot biomes: Fire damage
            target.setSecondsOnFire(biomeLevel * 3);
            damage *= 1.0f + (biomeLevel * 0.2f); // +20% per level
        } else if (isOcean) {
            // Ocean biomes: Lightning effect (if raining)
            if (level.isRaining() && level.canSeeSky(target.blockPosition())) {
                damage *= 1.0f + (biomeLevel * 0.25f); // +25% per level
            }
        }

        return damage;
    }

    /**
     * Get moon phase bonus multiplier (0.5x to 2.0x)
     */
    private static float getMoonPhaseBonus(ServerLevel level, int moonLevel) {
        int moonPhase = level.getMoonPhase(); // 0-7

        // Full moon (phase 0) = 2.0x damage
        // New moon (phase 4) = 0.5x damage
        // Scale based on moon level
        float baseMultiplier = switch (moonPhase) {
            case 0 -> 2.0f;  // Full moon
            case 1, 7 -> 1.5f; // Waning/Waxing Gibbous
            case 2, 6 -> 1.2f; // Quarter
            case 3, 5 -> 0.8f; // Crescent
            case 4 -> 0.5f;  // New moon
            default -> 1.0f;
        };

        // Scale the effect based on jewel level
        float levelMultiplier = 1.0f + ((baseMultiplier - 1.0f) * moonLevel * 0.5f);
        return Math.max(0.5f, Math.min(3.0f, levelMultiplier));
    }

    /**
     * Domino Effect - Progressive drop multiplier
     */
    private static void handleDominoEffect(Level level, BlockPos pos, BlockState state, Player player) {
        Block brokenBlock = state.getBlock();
        int multiplier = DominoEffectTracker.onBlockBreak(player.getUUID(), brokenBlock);

        if (multiplier > 1) {
            // Drop extra items based on multiplier
            List<ItemEntity> drops = new ArrayList<>();
            AABB area = new AABB(pos).inflate(2);
            drops.addAll(level.getEntitiesOfClass(ItemEntity.class, area));

            // Duplicate drops based on multiplier
            for (ItemEntity drop : drops) {
                for (int i = 1; i < multiplier; i++) {
                    ItemEntity extraDrop = new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(),
                        drop.getItem().copy());
                    level.addFreshEntity(extraDrop);
                }
            }

            // Show chain count to player
            player.displayClientMessage(
                Component.literal("Chain x" + multiplier + "!")
                    .withStyle(net.minecraft.ChatFormatting.GOLD),
                true
            );
        }
    }

    /**
     * Sound Harvesting - Generate musical notes and bonus loot
     */
    private static void handleSoundHarvesting(Level level, BlockPos pos, BlockState state, Player player, int soundLevel) {
        Block brokenBlock = state.getBlock();
        int note = SoundHarvestingTracker.onBlockBreak(player.getUUID(), brokenBlock);

        if (note >= 0) {
            // Play musical note
            level.playSound(null, pos, SoundHarvestingTracker.getSoundForNote(note),
                SoundSource.PLAYERS, 1.0f, SoundHarvestingTracker.getPitchForNote(note));

            // Check for melody completion
            int melodyIndex = SoundHarvestingTracker.checkForMelodyMatch(player.getUUID());
            if (melodyIndex >= 0) {
                // Melody completed! Grant bonus loot
                int rewardMultiplier = SoundHarvestingTracker.getRewardMultiplier(melodyIndex);

                // Drop bonus items based on what was mined
                for (int i = 0; i < rewardMultiplier * soundLevel; i++) {
                    // Drop random valuable item
                    Item bonusItem = switch (RANDOM.nextInt(6)) {
                        case 0 -> Items.DIAMOND;
                        case 1 -> Items.EMERALD;
                        case 2 -> Items.GOLD_INGOT;
                        case 3 -> Items.IRON_INGOT;
                        case 4 -> Items.LAPIS_LAZULI;
                        default -> Items.COAL;
                    };

                    ItemEntity bonusDrop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        new ItemStack(bonusItem));
                    level.addFreshEntity(bonusDrop);
                }

                // Notify player
                player.displayClientMessage(
                    Component.literal("♪ Melody Complete! ♪")
                        .withStyle(net.minecraft.ChatFormatting.AQUA),
                    true
                );

                // Play success sound
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                    SoundSource.PLAYERS, 1.0f, 2.0f);
            } else {
                // Show current note
                int sequenceLength = SoundHarvestingTracker.getSequenceLength(player.getUUID());
                player.displayClientMessage(
                    Component.literal("♪ " + SoundHarvestingTracker.getNoteName(note) + " (" + sequenceLength + "/7)")
                        .withStyle(net.minecraft.ChatFormatting.GRAY),
                    true
                );
            }
        }
    }

    /**
     * Gravity Tether - Make mob drops orbit the player
     */
    private static void handleGravityTether(LivingEntity target, Player player) {
        // Wait a tick for drops to spawn, then capture them
        Level level = target.level();
        BlockPos pos = target.blockPosition();

        // Use a scheduled task to capture drops after they spawn
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getServer().execute(() -> {
                AABB area = new AABB(pos).inflate(5);
                List<ItemEntity> drops = level.getEntitiesOfClass(ItemEntity.class, area);

                for (ItemEntity drop : drops) {
                    // Add to orbiting items
                    GravityTetherTracker.addOrbitingItem(player, drop);
                }

                if (!drops.isEmpty()) {
                    player.displayClientMessage(
                        Component.literal("Gravity Tether: " + drops.size() + " items captured")
                            .withStyle(net.minecraft.ChatFormatting.AQUA),
                        true
                    );
                }
            });
        }
    }

    // Helper methods for Biome Resonance effects

    private static void freezeNearbyWater(Level level, BlockPos pos, int range) {
        for (int x = -range; x <= range; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (checkState.is(Blocks.WATER)) {
                        level.setBlock(checkPos, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void applySlownessToNearbyMobs(Level level, BlockPos pos, Player player, int range) {
        AABB area = new AABB(pos).inflate(range * 2);
        List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity != player && !entity.isAlliedTo(player));

        for (LivingEntity mob : mobs) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, range - 1));
        }
    }

    private static void igniteNearbyEntities(Level level, BlockPos pos, Player player, int range) {
        AABB area = new AABB(pos).inflate(range * 2);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity != player && !entity.isAlliedTo(player));

        for (LivingEntity entity : entities) {
            entity.setSecondsOnFire(range * 2);
        }
    }
}

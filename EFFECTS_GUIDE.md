# Jewel Charms - Complete Effects Guide

This guide documents all available jewel effects, their categories, and how they work.

## Effect Categories

Effects are categorized to work contextually based on the item type:

- **TOOL**: Only works on mining/utility tools (Pickaxe, Axe, Shovel, Hoe)
- **WEAPON**: Only works on combat weapons (Sword, Trident, Bow)
- **UNIVERSAL**: Works on both tools and weapons
- **PLAYER**: Grants potion effects when held (works on any item)
- **SPECIAL**: Unique mechanics with special behaviors

---

## Tool-Focused Effects (TOOL Category)

### Mining Speed
- **ID**: `mining_speed`
- **Display**: Mining Speed
- **Effect**: Increases block breaking speed by 25% per level
- **Default Material**: Diamond (Level 2)

### Fortune
- **ID**: `fortune`
- **Display**: Fortune
- **Effect**: Increases drop quantities from blocks
- **Default Material**: Emerald (Level 1)
- **Note**: Currently implemented as framework - needs Fortune mechanic integration

### Silk Touch
- **ID**: `silk_touch`
- **Display**: Silk Touch
- **Effect**: Blocks drop themselves instead of their normal drops
- **Default Material**: Amethyst Shard (Level 1)
- **Note**: Currently implemented as framework - needs Silk Touch mechanic integration

### Auto-Smelt
- **ID**: `auto_smelt`
- **Display**: Auto-Smelt
- **Effect**: Automatically smelts ores into ingots when mined
- **Default Material**: Blaze Powder (Level 1)
- **Supported Ores**: Iron, Gold, Copper, Ancient Debris

### Vein Miner (AOE Mining)
- **ID**: `aoe_mining`
- **Display**: Vein Miner
- **Effect**: Breaks adjacent blocks of the same type (range based on level)
- **Default Material**: Coal (Level 1)
- **Limit**: Maximum 8 blocks to prevent lag

### Auto-Replant
- **ID**: `replanting`
- **Display**: Auto-Replant
- **Effect**: Automatically replants crops after harvest
- **Default Material**: Wheat Seeds (Level 1)
- **Best For**: Hoes

### Tree Capitator
- **ID**: `tree_felling`
- **Display**: Tree Capitator
- **Effect**: Breaks entire tree when breaking one log
- **Default Material**: Oak Sapling (Level 1)
- **Best For**: Axes
- **Limit**: Maximum 100 logs to prevent lag

### Item Magnet
- **ID**: `magnetic`
- **Display**: Item Magnet
- **Effect**: Pulls dropped items toward player (range based on level)
- **Default Material**: Iron Ingot (Level 2)

---

## Weapon-Focused Effects (WEAPON Category)

### Sharpness
- **ID**: `damage`
- **Display**: Sharpness
- **Effect**: Increases attack damage by 1.25 per level
- **Default Material**: Quartz (Level 2)

### Looting
- **ID**: `looting`
- **Display**: Looting
- **Effect**: Increases mob drop quantities and rarity
- **Default Material**: Lapis Lazuli (Level 1)
- **Note**: Currently implemented as framework - needs Looting mechanic integration

### Fire Aspect
- **ID**: `fire_aspect`
- **Display**: Fire Aspect
- **Effect**: Sets target on fire (4 seconds per level)
- **Default Material**: Blaze Rod (Level 2)

### Knockback
- **ID**: `knockback`
- **Display**: Knockback
- **Effect**: Increases knockback strength (0.5 per level)
- **Default Material**: Slime Ball (Level 2)

### Sweeping Edge
- **ID**: `sweeping`
- **Display**: Sweeping Edge
- **Effect**: Increases sweeping attack damage
- **Default Material**: None (add your own!)
- **Note**: Requires implementation

### Critical Strike
- **ID**: `critical_chance`
- **Display**: Critical Strike
- **Effect**: 10% chance per level to deal 50% extra damage
- **Default Material**: Eye of Ender (Level 2)

### Life Steal
- **ID**: `lifesteal`
- **Display**: Life Steal
- **Effect**: Heals player for 10% of damage dealt per level
- **Default Material**: Glistering Melon Slice (Level 2)

### Execute
- **ID**: `execute`
- **Display**: Execute
- **Effect**: Deals extra damage (3.0 per level) to enemies below 30% health
- **Default Material**: Wither Skeleton Skull (Level 1)
- **Perfect For**: Finishing low-health enemies

### Venomous
- **ID**: `poison`
- **Display**: Venomous
- **Effect**: Inflicts Poison status effect on hit
- **Default Material**: Spider Eye (Level 2)
- **Duration**: 5 seconds

### Enfeebling Strike
- **ID**: `weakness`
- **Display**: Enfeebling Strike
- **Effect**: Inflicts Weakness status effect on hit
- **Default Material**: Fermented Spider Eye (Level 2)
- **Duration**: 5 seconds

### Beheading
- **ID**: `beheading`
- **Display**: Beheading
- **Effect**: 5% chance per level to drop mob heads
- **Default Material**: Zombie Head (Level 1)
- **Supported Mobs**: Skeleton, Zombie, Creeper, Wither Skeleton, Player

---

## Universal Effects (UNIVERSAL Category)

### Unbreaking
- **ID**: `durability`
- **Display**: Unbreaking
- **Effect**: Increases tool/weapon durability
- **Default Material**: Netherite Ingot (Level 5)
- **Note**: Requires implementation

### Mending
- **ID**: `self_repair`
- **Display**: Mending
- **Effect**: Repairs tool by a set amount every 5 seconds
- **Default Material**: Ghast Tear (Level 2)
- **Repair Rate**: 1-2 durability per level

### XP Boost
- **ID**: `experience_boost`
- **Display**: XP Boost
- **Effect**: Multiplies experience gained
- **Default Material**: Gold Ingot (Level 2)
- **Note**: Requires implementation

### Extended Reach
- **ID**: `reach`
- **Display**: Extended Reach
- **Effect**: Increases block/entity interaction range
- **Default Material**: Ender Pearl (Level 2)
- **Note**: Requires implementation

---

## Player Effects (PLAYER Category)

These effects grant potion effects while holding the item.

### Night Vision
- **ID**: `night_vision`
- **Display**: Night Vision
- **Effect**: Grants Night Vision potion effect
- **Default Material**: Glowstone Dust (Level 1)
- **Duration**: 20 seconds (refreshed every second)

### Aqua Affinity
- **ID**: `water_breathing`
- **Display**: Aqua Affinity
- **Effect**: Grants Water Breathing potion effect
- **Default Material**: Prismarine Shard (Level 1)
- **Duration**: 20 seconds (refreshed every second)

### Swiftness
- **ID**: `speed`
- **Display**: Swiftness
- **Effect**: Grants Speed potion effect (level based on jewel level)
- **Default Material**: Prismarine Shard (Level 1)
- **Duration**: 2 seconds (refreshed every second)

### Haste
- **ID**: `haste`
- **Display**: Haste
- **Effect**: Grants Haste potion effect while mining
- **Default Material**: Redstone (Level 1)
- **Duration**: 1 second (applied during mining)

### Resistance
- **ID**: `resistance`
- **Display**: Resistance
- **Effect**: Grants Resistance potion effect
- **Default Material**: Obsidian (Level 2)
- **Duration**: 2 seconds (refreshed every second)

### Regeneration
- **ID**: `regeneration`
- **Display**: Regeneration
- **Effect**: Grants Regeneration potion effect
- **Default Material**: Golden Apple (Level 2)
- **Duration**: 2 seconds (refreshed every second)

### Leaping
- **ID**: `jump_boost`
- **Display**: Leaping
- **Effect**: Grants Jump Boost potion effect
- **Default Material**: Rabbit Foot (Level 2)
- **Duration**: 2 seconds (refreshed every second)

### Fire Resistance
- **ID**: `fire_resistance`
- **Display**: Fire Resistance
- **Effect**: Grants Fire Resistance potion effect
- **Default Material**: Magma Cream (Level 1)
- **Duration**: 20 seconds (refreshed every second)

---

## Special Effects (SPECIAL Category)

### Void Touch
- **ID**: `void_touch`
- **Display**: Void Touch
- **Effect**: Destroys blocks instantly with no drops
- **Default Material**: Barrier (Level 1)
- **Use Case**: Clearing unwanted blocks, terraforming

### Absorption
- **ID**: `absorption`
- **Display**: Absorption
- **Effect**: Grants Absorption hearts while holding
- **Default Material**: Nether Star (Level 3)
- **Duration**: 20 seconds (refreshed every second)

### Ender Pocket
- **ID**: `teleport_drops`
- **Display**: Ender Pocket
- **Effect**: Drops are teleported directly to player inventory
- **Default Material**: Chorus Fruit (Level 1)
- **Perfect For**: Mining without inventory clutter

### Soulbound
- **ID**: `soulbound`
- **Display**: Soulbound
- **Effect**: Item is kept on death
- **Default Material**: Totem of Undying (Level 1)
- **Note**: Requires implementation

### Curse of Binding
- **ID**: `curse_binding`
- **Display**: Curse of Binding
- **Effect**: Item cannot be removed from inventory
- **Default Material**: None (add at your own risk!)
- **Note**: Requires implementation

---

## Effect Stacking

When multiple jewels with the same effect are attached to an item:
- **Levels ADD together**: Diamond (Mining Speed 2) + Diamond (Mining Speed 2) = Mining Speed 4
- **Effects MULTIPLY**: Mining Speed 4 = 100% faster (4 × 25% per level)

Maximum effect levels depend on the jewel combinations you use!

---

## Context-Aware Behavior

The mod intelligently applies effects based on item type:

### Example 1: Pickaxe with Sharpness Jewel
- Pickaxe is a TOOL
- Sharpness is WEAPON category
- **Effect does NOT apply** (wrong category)

### Example 2: Pickaxe with Mining Speed Jewel
- Pickaxe is a TOOL
- Mining Speed is TOOL category
- **Effect applies normally**

### Example 3: Sword with Life Steal Jewel
- Sword is a WEAPON
- Life Steal is WEAPON category
- **Effect applies normally**

### Example 4: Pickaxe with Mending Jewel
- Pickaxe is a TOOL
- Mending is UNIVERSAL category
- **Effect applies** (universal works on both)

### Example 5: Any Item with Night Vision Jewel
- Night Vision is PLAYER category
- **Effect applies** (player effects work on any item)

---

## Creating Custom Material Mappings

Edit `config/jewelcharms/material_effects.json`:

```json
{
  "materials": [
    {
      "materialId": "minecraft:your_item",
      "effects": {
        "effect_id": level
      },
      "color": 16711680
    }
  ]
}
```

### Example: Create a "Killer Sword" Material

```json
{
  "materialId": "minecraft:diamond_sword",
  "effects": {
    "damage": 5,
    "critical_chance": 3,
    "lifesteal": 2,
    "execute": 1
  },
  "color": 16711680
}
```

Combine this with a diamond sword in the Jewel Creation Station to create an ultimate combat jewel!

---

## Balancing Tips

- **Tool Effects**: Focus on efficiency (Mining Speed, Fortune, Vein Miner)
- **Weapon Effects**: Focus on damage and survival (Sharpness, Life Steal, Critical Strike)
- **Combination Ideas**:
  - Mining Tool: Diamond + Emerald + Coal = Speed + Fortune + Vein Miner
  - Combat Weapon: Quartz + Eye of Ender + Glistering Melon = Damage + Crits + Life Steal
  - Utility: Ender Pearl + Glowstone + Prismarine = Reach + Night Vision + Speed

---

## Effect Implementation Status

✅ = Fully Implemented | ⚠️ = Framework Only | ❌ = Not Implemented

### Tool Effects
- ✅ Mining Speed
- ⚠️ Fortune (framework only)
- ⚠️ Silk Touch (framework only)
- ✅ Auto-Smelt
- ✅ Vein Miner
- ✅ Auto-Replant
- ✅ Tree Capitator
- ✅ Item Magnet

### Weapon Effects
- ✅ Sharpness
- ⚠️ Looting (framework only)
- ✅ Fire Aspect
- ✅ Knockback
- ❌ Sweeping Edge
- ✅ Critical Strike
- ✅ Life Steal
- ✅ Execute
- ✅ Venomous
- ✅ Enfeebling Strike
- ✅ Beheading

### Universal Effects
- ❌ Unbreaking
- ✅ Mending
- ❌ XP Boost
- ❌ Extended Reach

### Player Effects
- ✅ All player effects work

### Special Effects
- ✅ Void Touch
- ✅ Absorption
- ✅ Ender Pocket
- ❌ Soulbound
- ❌ Curse of Binding

---

## Future Effect Ideas

Want to expand the mod? Consider adding:
- **Thorns**: Reflects damage to attackers
- **Explosive**: Explosions on block break/hit
- **Freezing**: Slows enemies
- **Lightning**: Summons lightning strikes
- **Gravity**: Pulls/pushes enemies
- **Teleport**: Random teleportation
- **Multishot**: Multiple projectiles
- **Piercing**: Arrows pierce through
- **Chain Lightning**: Damage spreads to nearby enemies

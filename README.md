# Jewel Charms Mod

A Minecraft 1.20.1 Forge mod that adds customizable jewel charms to tools, inspired by Valorant gun buddies.

## Overview

Jewel Charms allows players to craft jewels from various materials and attach them to tools for special gameplay effects. Each jewel can be made from multiple materials, with effects and colors blending based on the materials used.

## Features

### Core Mechanics
- **Jewel Creation Station**: A custom crafting station with a grid-based minigame for creating jewels
- **Material System**: Configurable JSON-based system to define which materials provide which effects
- **Jewel Attachment**: Attach up to 2 jewels per tool using a Smithing Table with a Jewel Socket Template
- **Jewel Removal**: Remove jewels from tools at the Jewel Creation Station without destroying the tool
- **Visual Overlays**: Colored overlays on tools showing attached jewels (positioned on opposite sides)
- **40+ Pre-defined Effects**: Context-aware effects for tools, weapons, and special abilities
- **Smart Effect System**: Effects automatically apply based on item type (sword vs pickaxe)

### Items & Blocks
- **Jewel Creation Station** - Crafted with 3 Iron Blocks, 1 Crafting Table, and 1 Diamond
- **Jewel Socket Template** - Found in village blacksmith/toolsmith chests, can be duplicated with 7 Gold + 1 Diamond
- **Jewel** - Created at the Jewel Creation Station by combining materials

## Project Structure

```
src/main/java/com/jewelcharms/
├── JewelCharms.java                    # Main mod class
├── block/
│   └── JewelCreationStationBlock.java  # Jewel crafting station block
├── client/
│   ├── event/
│   │   ├── ClientEvents.java           # Client-side rendering events
│   │   └── TooltipHandler.java         # Tooltip handler for tools with jewels
│   ├── renderer/
│   │   └── ToolJewelRenderer.java      # Renders jewel overlays on tools
│   └── screen/
│       └── JewelCreationStationScreen.java  # GUI with minigame
├── config/
│   ├── MaterialEffectConfig.java       # Config system for material mappings
│   └── MaterialMapping.java            # Material to effect mapping data
├── effect/
│   └── JewelEffect.java                # Enum of all available effects
├── event/
│   └── JewelEffectHandler.java         # Applies jewel effects to tools
├── init/
│   ├── ModBlocks.java                  # Block registry
│   ├── ModItems.java                   # Item registry
│   ├── ModMenuTypes.java               # Menu/Container registry
│   ├── ModRecipeTypes.java             # Recipe serializer registry
│   └── ModCreativeTabs.java            # Creative tab setup
├── item/
│   ├── JewelItem.java                  # Jewel item with NBT data
│   └── JewelSocketTemplateItem.java    # Smithing template
├── menu/
│   └── JewelCreationStationMenu.java   # Container menu for GUI
├── network/
│   ├── ModNetwork.java                 # Network registration
│   ├── MinigameClickPacket.java        # Packet for minigame clicks
│   └── MinigameStartPacket.java        # Packet to start minigame
├── recipe/
│   ├── JewelAttachmentRecipe.java      # Custom smithing recipe
│   └── JewelAttachmentRecipeSerializer.java
└── util/
    ├── JewelData.java                  # Jewel NBT data handling
    ├── JewelCreationHelper.java        # Helper for creating jewels
    └── ToolJewelData.java              # Tool jewel attachment handling
```

## Configuration

The mod creates a config file at `config/jewelcharms/material_effects.json` on first run. This file defines which materials map to which effects.

### Default Materials (Examples)

**Tool-Focused:**
- **Diamond** → Mining Speed +2 (Tool)
- **Emerald** → Fortune +1 (Tool)
- **Coal** → Vein Miner +1 (Tool - AOE Mining)
- **Oak Sapling** → Tree Capitator +1 (Tool - Axe)

**Weapon-Focused:**
- **Quartz** → Sharpness +2 (Weapon)
- **Eye of Ender** → Critical Strike +2 (Weapon)
- **Glistering Melon** → Life Steal +2 (Weapon)
- **Lapis** → Looting +1 (Weapon)

**Universal:**
- **Netherite** → Durability +5, Speed +2, Damage +2
- **Gold** → XP Boost +2
- **Ghast Tear** → Self-Repair +2

### Adding Custom Materials
Edit the config file to add any item:

```json
{
  "materials": [
    {
      "materialId": "minecraft:your_item",
      "effects": {
        "mining_speed": 3,
        "damage": 2
      },
      "color": 16711680
    }
  ]
}
```

## Available Effects

### Tool Effects (Only work on Pickaxe, Axe, Shovel, Hoe)
- ✅ `mining_speed` - Increases mining speed (25% per level)
- ✅ `fortune` - Fortune effect (framework)
- ✅ `silk_touch` - Silk Touch effect (framework)
- ✅ `auto_smelt` - Auto-smelts ores into ingots
- ✅ `aoe_mining` - Vein Miner - breaks adjacent blocks
- ✅ `replanting` - Auto-replants crops
- ✅ `tree_felling` - Tree Capitator - breaks entire tree
- ✅ `magnetic` - Pulls items to player

### Weapon Effects (Only work on Sword, Trident, Bow)
- ✅ `damage` - Sharpness (1.25 damage per level)
- ✅ `looting` - Looting (framework)
- ✅ `fire_aspect` - Sets target on fire (4s per level)
- ✅ `knockback` - Increased knockback
- ✅ `sweeping` - Sweeping Edge (framework)
- ✅ `critical_chance` - 10% crit chance per level (50% bonus damage)
- ✅ `lifesteal` - Heal for 10% of damage dealt
- ✅ `execute` - Bonus damage to low health enemies
- ✅ `poison` - Inflicts poison on hit
- ✅ `weakness` - Inflicts weakness on hit
- ✅ `beheading` - 5% chance to drop heads

### Universal Effects (Work on both Tools and Weapons)
- ⚠️ `durability` - Unbreaking (framework)
- ✅ `self_repair` - Repairs tool over time
- ⚠️ `experience_boost` - XP multiplier (framework)
- ⚠️ `reach` - Extended reach (framework)

### Player Effects (Grant potion effects when held)
- ✅ `night_vision` - Constant night vision
- ✅ `water_breathing` - Underwater breathing
- ✅ `speed` - Movement speed boost
- ✅ `haste` - Mining haste
- ✅ `resistance` - Damage resistance
- ✅ `regeneration` - Health regeneration
- ✅ `jump_boost` - Jump higher
- ✅ `fire_resistance` - Immune to fire

### Special Effects (Unique mechanics)
- ✅ `void_touch` - Destroys blocks with no drops
- ✅ `absorption` - Absorption hearts
- ✅ `teleport_drops` - Drops go to inventory
- ⚠️ `soulbound` - Keep on death (framework)
- ⚠️ `curse_binding` - Can't remove (framework)

**Legend:** ✅ = Fully Working | ⚠️ = Framework Only

For complete effect details, see [EFFECTS_GUIDE.md](EFFECTS_GUIDE.md)

## How to Use

1. **Craft a Jewel Creation Station**: Use 3 Iron Blocks, 1 Crafting Table, and 1 Diamond
2. **Create Jewels**: Place valid materials in the station and complete the minigame
3. **Find a Jewel Socket Template**: Search village blacksmith/toolsmith chests
4. **Attach Jewels**: Use a Smithing Table with the template, your tool, and a jewel
5. **Remove Jewels**: Place the tool in the Jewel Creation Station's removal slot

## What Still Needs to be Done

### Critical - Required for Functionality
1. **Textures** (`.png` files needed):
   - `assets/jewelcharms/textures/block/jewel_creation_station.png` (16x16 block texture)
   - `assets/jewelcharms/textures/item/jewel.png` (16x16 jewel item)
   - `assets/jewelcharms/textures/item/jewel_socket_template.png` (16x16 template)
   - `assets/jewelcharms/textures/gui/jewel_creation_station.png` (256x256 GUI background)

### Optional - Framework Effects Need Implementation
Some effects have the framework in place but need additional implementation:
- **Fortune/Looting**: Requires loot table modification
- **Silk Touch**: Requires block drop override
- **Durability/Unbreaking**: Requires durability damage hooks
- **XP Boost**: Requires experience event handling
- **Reach**: Requires player reach distance modification
- **Soulbound**: Requires death event handling
- **Sweeping Edge**: Requires attack event enhancement

### Nice to Have - Enhancements
- Add sound effects for jewel crafting and attachment
- Particle effects when using tools with jewels
- Better visual representation of jewels on tools (3D models instead of overlays)
- JEI integration to show material effects
- Datapack support for easier customization
- Advancement system for collecting jewels
- Jewel rarity tiers
- More complex minigame patterns
- Config options for minigame difficulty

## Building the Mod

Run the following command to build:
```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`

## Testing

Run in development environment:
```bash
./gradlew runClient
```

## Notes for Texture Creation

- **Jewel Creation Station**: Should look like a crafting table with metallic/gem elements
- **Jewel**: Should be a gemstone that can be tinted (the color will be applied via code)
- **Jewel Socket Template**: Should resemble other smithing templates (paper-like with a pattern)
- **GUI**: Needs slots for 5 materials, 1 output, removal section, and space for the minigame grid

## License

All Rights Reserved (Update as needed)

## Credits

Created for Minecraft 1.20.1 with Forge 47.3.0

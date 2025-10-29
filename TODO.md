# Jewel Charms - TODO List

## Critical Tasks (Required for Functionality)

### 1. Create Textures
The mod will not work properly without these textures. All textures should be 16x16 PNG files unless otherwise specified.

- [ ] **Block Texture**: `src/main/resources/assets/jewelcharms/textures/block/jewel_creation_station.png`
  - Size: 16x16
  - Style: Should look like a specialized crafting station (mix of crafting table and metallic/gem elements)
  - Can use existing Minecraft blocks as reference

- [ ] **Jewel Item**: `src/main/resources/assets/jewelcharms/textures/item/jewel.png`
  - Size: 16x16
  - Style: Simple gemstone design
  - Note: The color will be applied programmatically, so use a neutral/white base

- [ ] **Template Item**: `src/main/resources/assets/jewelcharms/textures/item/jewel_socket_template.png`
  - Size: 16x16
  - Style: Similar to vanilla smithing templates (paper-like with a pattern)
  - Can reference vanilla smithing templates for style

- [ ] **GUI Background**: `src/main/resources/assets/jewelcharms/textures/gui/jewel_creation_station.png`
  - Size: 256x256
  - Layout needed:
    - 5 material input slots (horizontal row)
    - 1 jewel output slot
    - Removal section (1 input, 2 output slots)
    - Space for 5x5 minigame grid (80x80 pixels recommended)
    - Player inventory section
  - Can use vanilla GUI textures as a base

### 2. Fix Minigame Integration
Currently the minigame is purely visual. Need to:

- [ ] Add a "Start Crafting" button in the GUI
- [ ] Implement server-side minigame validation
- [ ] Create packet to send minigame completion to server
- [ ] Server creates jewel upon successful completion
- [ ] Handle minigame failure (materials returned or lost?)

**Files to modify:**
- `JewelCreationStationScreen.java` - Add button
- `JewelCreationStationMenu.java` - Add jewel creation logic
- Create new packet: `MinigameCompletePacket.java`

### 3. Implement Missing Effect Handlers
Currently only Mining Speed, Haste, and Damage are working. Need handlers for:

- [ ] **Fortune** - Modify block drops
- [ ] **Silk Touch** - Alternative drop behavior
- [ ] **Auto-Smelt** - Smelt drops automatically
- [ ] **Reach** - Increase player reach distance
- [ ] **AOE Mining** - Break multiple blocks
- [ ] **Self-Repair** - Repair tool over time
- [ ] **Looting** - Increase mob drops
- [ ] **Fire Aspect** - Set mobs on fire
- [ ] **Knockback** - Increase knockback
- [ ] **Sweeping** - Sweeping edge effect
- [ ] **Critical Chance** - Random critical hits
- [ ] **Experience Boost** - XP multiplier
- [ ] **Night Vision** - Constant night vision
- [ ] **Water Breathing** - Underwater breathing
- [ ] **Speed** - Movement speed
- [ ] **Resistance** - Damage reduction

**Files to modify:**
- `JewelEffectHandler.java` - Add event handlers for each effect
- May need to create mixins for some effects (Fortune, Silk Touch, Auto-Smelt)

## Medium Priority Tasks

### 4. Add Mod Icon
- [ ] Create `pack.png` in mod root (256x256)
- [ ] Create `logo.png` in `src/main/resources/` (referenced in mods.toml)

### 5. Test All Systems
- [ ] Build the mod with `./gradlew build`
- [ ] Test in development: `./gradlew runClient`
- [ ] Verify jewel creation works
- [ ] Verify jewel attachment to tools
- [ ] Verify jewel removal
- [ ] Test all default material mappings
- [ ] Check tooltips display correctly
- [ ] Verify visual overlays render

### 6. Balance and Polish
- [ ] Tune minigame difficulty (target count, time limit)
- [ ] Balance effect strengths in config
- [ ] Test with modded tools (compatibility)
- [ ] Add more default materials to config
- [ ] Adjust jewel overlay positioning/size

## Nice to Have (Future Enhancements)

### 7. Sound Effects
- [ ] Jewel crafting sound
- [ ] Jewel attachment sound
- [ ] Minigame click sound
- [ ] Minigame success/fail sounds

### 8. Visual Enhancements
- [ ] Particle effects when using tools with jewels
- [ ] Better jewel rendering (3D models instead of 2D overlays)
- [ ] Animated jewels on tools
- [ ] Custom item renderer for jewels

### 9. Integration
- [ ] JEI support (show material effects in JEI)
- [ ] REI support
- [ ] Create datapack examples
- [ ] Add more loot table locations for templates

### 10. Progression System
- [ ] Advancements for creating jewels
- [ ] Advancements for attaching jewels
- [ ] Collection advancement (create all jewel types)
- [ ] Achievement for max-level tool (2 powerful jewels)

### 11. Advanced Features
- [ ] Jewel rarity tiers (common, rare, epic, legendary)
- [ ] Colored names for jewels based on rarity
- [ ] Enchantment glint for high-tier jewels
- [ ] Multiple minigame variations
- [ ] Configurable minigame difficulty
- [ ] Special "unique" jewels with fixed recipes

### 12. Documentation
- [ ] Create wiki pages
- [ ] Add in-game documentation (Patchouli book?)
- [ ] Create video tutorial
- [ ] Add more code comments

## Known Issues
- [ ] Loot table injection might not work correctly (test thoroughly)
- [ ] Visual overlays may not work in all contexts (item frames, dropped items)
- [ ] Recipe book integration not implemented
- [ ] No multiplayer testing done yet

## Testing Checklist
Once textures are added, test these scenarios:

- [ ] Craft Jewel Creation Station
- [ ] Place Jewel Creation Station
- [ ] Open GUI
- [ ] Add materials to slots
- [ ] Start minigame
- [ ] Complete minigame successfully
- [ ] Receive jewel
- [ ] Find template in village chest
- [ ] Duplicate template recipe
- [ ] Attach jewel to pickaxe at smithing table
- [ ] Verify tooltip shows jewel info
- [ ] Verify visual overlay appears
- [ ] Test mining speed with jewel
- [ ] Attach second jewel
- [ ] Remove jewels at creation station
- [ ] Verify effects work in survival
- [ ] Test with various tools (pickaxe, axe, sword, etc.)

## Resources Needed
- Image editing software (for textures): Paint.NET, GIMP, Photoshop, Aseprite
- Minecraft texture reference: https://mcasset.cloud/
- Vanilla GUI textures for reference
- Color picker for hex codes (for material colors in config)

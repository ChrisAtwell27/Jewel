# Jewel Charms - Texture Creation Guide

This guide explains exactly what textures are needed and how to create them.

## Required Textures

### 1. Jewel Creation Station Block
**Path**: `src/main/resources/assets/jewelcharms/textures/block/jewel_creation_station.png`
**Size**: 16x16 pixels
**Format**: PNG with transparency

**Description**: The texture for the jewel crafting station block.

**Suggestions**:
- Base it on a crafting table texture
- Add metallic or gem-like accents
- Use iron/diamond color palette
- Make it look like a specialized workbench

**Quick Solution**: You can temporarily use vanilla `crafting_table.png` or `smithing_table.png` as a placeholder.

---

### 2. Jewel Item
**Path**: `src/main/resources/assets/jewelcharms/textures/item/jewel.png`
**Size**: 16x16 pixels
**Format**: PNG with transparency

**Description**: The jewel item texture. This will be tinted with colors from the materials used.

**Important**:
- Use a neutral white/light gray color
- The mod will programmatically tint it based on jewel materials
- Make it look like a gemstone or crystal

**Suggestions**:
- Simple diamond-like shape
- Add some facets/shine
- Keep it relatively small (8-12 pixels tall/wide)
- Use vanilla diamond/emerald as reference

**Quick Solution**: Use vanilla `diamond.png` as a temporary placeholder.

---

### 3. Jewel Socket Template
**Path**: `src/main/resources/assets/jewelcharms/textures/item/jewel_socket_template.png`
**Size**: 16x16 pixels
**Format**: PNG with transparency

**Description**: The smithing template used to attach jewels to tools.

**Important**:
- Should match vanilla smithing template style
- Paper/parchment look with a pattern

**Suggestions**:
- Look at vanilla smithing templates for reference
- Use tan/beige colors
- Add a gem or socket pattern in the center
- Keep the paper/template aesthetic

**Quick Solution**: Copy a vanilla smithing template texture (like `netherite_upgrade_smithing_template.png`).

---

### 4. GUI Background
**Path**: `src/main/resources/assets/jewelcharms/textures/gui/jewel_creation_station.png`
**Size**: 256x256 pixels
**Format**: PNG with transparency

**Description**: The GUI background for the Jewel Creation Station.

## GUI Layout Specifications

The GUI needs the following elements at specific positions (coordinates from top-left):

### Section 1: Jewel Creation (Top)
- **Material Slots** (5 slots, horizontal)
  - Position: x=30, y=35
  - Each slot: 18x18 pixels
  - Spacing: 18 pixels apart
  - Total width: 90 pixels (5 slots × 18)

- **Output Slot** (1 slot)
  - Position: x=120, y=75
  - Size: 18x18 pixels

### Section 2: Minigame Area
- **Grid Space**
  - Position: x=25, y=65
  - Size: 80x80 pixels (for 5×5 grid, 16 pixels per cell)
  - Background: Dark gray/black
  - Border: 2 pixels around grid

### Section 3: Jewel Removal (Bottom)
- **Input Slot** (tool with jewels)
  - Position: x=30, y=130
  - Size: 18x18 pixels

- **Output Slot 1** (removed jewel)
  - Position: x=80, y=130
  - Size: 18x18 pixels

- **Output Slot 2** (removed jewel)
  - Position: x=110, y=130
  - Size: 18x18 pixels

### Section 4: Player Inventory
- **Main Inventory** (3 rows × 9 columns)
  - Start Position: x=8, y=166
  - Each slot: 18x18 pixels
  - Spacing: 18 pixels apart

- **Hotbar** (1 row × 9 columns)
  - Start Position: x=8, y=224
  - Each slot: 18x18 pixels
  - Spacing: 18 pixels apart

### Visual Elements Needed
- **Slot backgrounds**: 18x18 pixel gray squares (use vanilla GUI slot)
- **Panel backgrounds**: Use vanilla GUI gray background
- **Borders**: 2-pixel dark gray borders around sections
- **Title space**: Reserve top 15 pixels for "Jewel Creation Station" title

### Creating the GUI Texture

**Option 1: Manual Creation**
1. Create a 256x256 PNG
2. Use vanilla `assets/minecraft/textures/gui/container/generic_54.png` as a base
3. Draw slot positions as specified above
4. Add decorative borders and backgrounds
5. Leave space for the minigame grid

**Option 2: Quick Placeholder**
1. Copy vanilla `brewing_stand.png` or `enchanting_table.png`
2. Modify slot positions as needed
3. This won't be perfect but will work for testing

**Option 3: Template Included**
The code is currently set up to work with this layout. You can create a simple gray background with slot outlines and refine later.

## Slot Visual Reference

Vanilla Minecraft uses this pattern for slots:
```
████████████████  (18×18 pixels total)
█▓▓▓▓▓▓▓▓▓▓▓▓▓▓█
█▓            ▓█
█▓            ▓█
█▓            ▓█  (Center 16×16 for item)
█▓            ▓█
█▓            ▓█
█▓▓▓▓▓▓▓▓▓▓▓▓▓▓█
████████████████
```

## Testing Your Textures

After adding textures:

1. Place files in correct locations
2. Run `./gradlew runClient`
3. Look for the items/blocks in creative inventory
4. Check if GUI opens correctly when placing block
5. Verify slots align with click areas

## Temporary Solution

If you want to test the mod immediately without creating textures:

**For blocks/items (16x16):**
Create a solid colored square:
- Jewel Creation Station: Gray (#808080)
- Jewel: White (#FFFFFF)
- Template: Beige (#D2B48C)

**For GUI (256x256):**
Create a gray background (#C6C6C6) with black slot outlines at the positions listed above.

This won't look good but will let you test functionality.

## Recommended Tools

- **Simple**: Paint.NET (free, Windows)
- **Advanced**: GIMP (free, cross-platform)
- **Professional**: Aseprite (paid, best for pixel art)
- **Online**: Pixilart.com (free, browser-based)

## Resources

- Vanilla textures: https://mcasset.cloud/
- Minecraft GUI template: Search "Minecraft GUI template 256x256"
- Color picker: Use any online hex color picker
- Reference mods: Look at GUI mods on CurseForge for examples

# Recommended Libraries for Jewel Charms

This document outlines optional libraries that would enhance the mod's functionality and user experience.

---

## 🌟 Highly Recommended

### 1. JEI (Just Enough Items) Integration
**Why**: Show material effects in-game, recipe integration

**Benefits**:
- Players can see what effects each material provides
- Hover over materials to see "Used in Jewel Crafting"
- Show jewel attachment recipes
- Display effect descriptions in JEI

**Implementation Complexity**: ⭐⭐☆☆☆ (Medium)

**Add to build.gradle**:
```gradle
dependencies {
    // JEI
    compileOnly fg.deobf("mezz.jei:jei-1.20.1-forge-api:15.2.0.27")
    runtimeOnly fg.deobf("mezz.jei:jei-1.20.1-forge:15.2.0.27")
}
```

**What you'd implement**:
- Material category showing all materials and their effects
- Recipe category for jewel attachment
- Tooltip integration showing effect details

**Player Experience**:
- Look up any material in JEI
- See: "Diamond → Mining Speed +2, Color: #5DADE2"
- Click to see all jewel recipes using that material

---

### 2. Patchouli (In-Game Guidebook)
**Why**: Beautiful in-game documentation

**Benefits**:
- Complete in-game guide for all effects
- Step-by-step tutorials
- Material encyclopedia
- Effect combinations guide
- No need to tab out or read external docs

**Implementation Complexity**: ⭐⭐⭐☆☆ (Medium-High)

**Add to build.gradle**:
```gradle
dependencies {
    // Patchouli
    implementation fg.deobf("vazkii.patchouli:Patchouli:1.20.1-84-FORGE")
}
```

**What you'd create**:
- JSON book definition (`data/jewelcharms/patchouli_books/guide/book.json`)
- Category pages (Getting Started, Effects, Materials, etc.)
- Entry pages for each effect
- Recipe integrations

**Player Experience**:
- Craft "Jewel Charms Guide" book
- Browse categories: Tools, Weapons, Special Effects
- Click on "Life Steal" → See description, materials, examples
- Interactive recipe viewing

---

### 3. Cloth Config API (Config Screen)
**Why**: In-game config editing without file editing

**Benefits**:
- Edit material effects in-game
- Add custom materials via GUI
- Adjust effect strengths without restarting
- User-friendly config management
- Mod menu integration

**Implementation Complexity**: ⭐⭐⭐☆☆ (Medium-High)

**Add to build.gradle**:
```gradle
dependencies {
    // Cloth Config
    modImplementation("me.shedaniel.cloth:cloth-config-forge:11.1.106")
}
```

**What you'd implement**:
- Config screen accessible from mod menu
- Material list editor
- Effect strength sliders
- Color picker for jewel colors
- Save/Load buttons

**Player Experience**:
- Mods menu → Jewel Charms → Configure
- Add material: "Copper Ingot → Fortune +1"
- Adjust: "Netherite damage: 3 → 5"
- Save and reload

---

## 💡 Nice to Have

### 4. Configured (Alternative Config UI)
**Why**: Simpler config UI than Cloth Config

**Benefits**:
- Auto-generates config screens
- Less code to write
- Clean, simple interface

**Implementation Complexity**: ⭐⭐☆☆☆ (Medium)

**Add to build.gradle**:
```gradle
dependencies {
    // Configured
    implementation fg.deobf("com.mrcrayfish:configured:2.2.3-1.20.1")
}
```

**Note**: Choose either Cloth Config OR Configured, not both.

---

### 5. Curios API (Extended Inventory)
**Why**: If you want jewels to be wearable/equipable separately

**Benefits**:
- Add "Charm" slots for passive jewels
- Jewels that grant effects when worn (not held)
- More inventory management options

**Implementation Complexity**: ⭐⭐⭐⭐☆ (High)

**Add to build.gradle**:
```gradle
dependencies {
    // Curios
    implementation fg.deobf("top.theillusivec4.curios:curios-forge:5.3.5+1.20.1")
}
```

**Use Case**:
- Passive jewels in dedicated slots
- Tool jewels + Wearable jewels simultaneously
- Charm jewelry system

**Note**: Only add if you want to expand beyond tool/weapon jewels.

---

### 6. Kotlin for Forge (If you prefer Kotlin)
**Why**: Write cleaner, more concise code

**Benefits**:
- Less boilerplate
- Null safety
- Extension functions
- Modern language features

**Implementation Complexity**: ⭐⭐⭐⭐⭐ (High - requires refactoring)

**Add to build.gradle**:
```gradle
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.9.0'
}

dependencies {
    implementation 'thedarkcolour:kotlinforforge:4.4.0'
}
```

**Note**: Would require rewriting existing Java code. Only worth it if you prefer Kotlin.

---

## 🔧 Advanced/Technical

### 7. Mixin Support (Already Available)
**Why**: Advanced bytecode manipulation for complex effects

**Benefits**:
- Implement Fortune/Looting properly (modify loot tables)
- Implement Silk Touch (override drop behavior)
- Implement Durability/Unbreaking (hook damage events)
- Deep game mechanic integration

**Implementation Complexity**: ⭐⭐⭐⭐⭐ (Very High)

**Already included in Forge**, but requires:
- Create `mixins.jewelcharms.json` config
- Write mixin classes targeting vanilla code
- Understand bytecode injection

**Example Use**:
```java
@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "getDrops", at = @At("RETURN"))
    private void onGetDrops(CallbackInfoReturnable<List<ItemStack>> cir) {
        // Modify drops for Fortune effect
    }
}
```

---

## 📊 Priority Recommendations

### For Best Player Experience:
1. **JEI** (Essential) - Makes mod discoverable
2. **Patchouli** (Highly Recommended) - In-game docs
3. **Cloth Config** (Recommended) - Easy customization

### For Development:
1. **JEI** - Easy to implement, huge value
2. **Mixins** - For completing framework effects
3. **Patchouli** - Time-consuming but impressive

---

## Implementation Order

**Phase 1: Core Functionality** (Current - Complete!)
- ✅ Basic mod mechanics
- ✅ Effect system
- ✅ Config file

**Phase 2: User Experience**
- Add JEI integration (2-4 hours)
- Add Cloth Config screen (4-6 hours)

**Phase 3: Documentation**
- Add Patchouli guidebook (6-8 hours)
- Create all book entries

**Phase 4: Advanced Features**
- Implement Mixins for missing effects (10-15 hours)
- Add Curios support if desired (4-6 hours)

---

## Detailed: JEI Integration Example

Here's what JEI integration would look like:

### 1. Add Dependency
```gradle
dependencies {
    compileOnly fg.deobf("mezz.jei:jei-1.20.1-forge-api:15.2.0.27")
    runtimeOnly fg.deobf("mezz.jei:jei-1.20.1-forge:15.2.0.27")
}
```

### 2. Create JEI Plugin
```java
@JeiPlugin
public class JewelCharmsJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JewelCharms.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Register jewel crafting category
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Add all material → effect recipes
        MaterialEffectConfig config = getConfig();
        for (MaterialMapping mapping : config.getAllMappings()) {
            // Show material with its effects
        }
    }
}
```

### 3. Material Info Category
Players can:
- Search "Diamond" in JEI
- See tooltip: "Jewel Material: Mining Speed +2"
- Click to see all jewels that use diamond
- View effect descriptions

---

## Detailed: Patchouli Integration Example

### Book Structure
```
data/jewelcharms/patchouli_books/guide/
├── book.json                    # Book definition
├── en_us/
│   ├── categories/
│   │   ├── basics.json         # Getting Started
│   │   ├── tools.json          # Tool Effects
│   │   ├── weapons.json        # Weapon Effects
│   │   └── special.json        # Special Effects
│   └── entries/
│       ├── getting_started/
│       │   ├── introduction.json
│       │   ├── crafting.json
│       │   └── attachment.json
│       ├── tools/
│       │   ├── mining_speed.json
│       │   ├── fortune.json
│       │   └── vein_miner.json
│       └── weapons/
│           ├── sharpness.json
│           ├── lifesteal.json
│           └── critical_strike.json
```

### Example Entry (Mining Speed)
```json
{
  "name": "Mining Speed",
  "icon": "minecraft:diamond_pickaxe",
  "category": "jewelcharms:tools",
  "pages": [
    {
      "type": "text",
      "text": "Mining Speed increases block breaking speed by 25% per level.$(br2)Works on: Pickaxe, Axe, Shovel, Hoe"
    },
    {
      "type": "crafting",
      "recipe": "jewelcharms:diamond_jewel",
      "text": "Use Diamond to create a Mining Speed jewel"
    },
    {
      "type": "text",
      "title": "Examples",
      "text": "Level 1: +25% speed$(br)Level 2: +50% speed$(br)Level 4: +100% speed (2x faster!)"
    }
  ]
}
```

---

## Cost/Benefit Analysis

| Library | Development Time | Player Value | Maintenance |
|---------|-----------------|--------------|-------------|
| **JEI** | 2-4 hours | ⭐⭐⭐⭐⭐ | Low |
| **Patchouli** | 6-8 hours | ⭐⭐⭐⭐☆ | Low |
| **Cloth Config** | 4-6 hours | ⭐⭐⭐⭐☆ | Low |
| **Curios** | 4-6 hours | ⭐⭐⭐☆☆ | Medium |
| **Mixins** | 10-15 hours | ⭐⭐⭐⭐☆ | High |

---

## My Recommendation

**Start with JEI** because:
- ✅ Quick to implement (2-4 hours)
- ✅ Massive player value
- ✅ Makes your mod feel professional
- ✅ Low maintenance
- ✅ Almost every modpack has JEI

**Then add Cloth Config** because:
- ✅ Players can customize without file editing
- ✅ Reduces support questions
- ✅ Adds polish

**Finally, add Patchouli** if you want:
- ✅ Beautiful in-game documentation
- ✅ Professional appearance
- ✅ Reduced need for external wiki

---

## Should You Add Them Now?

**My suggestion**:

1. **First**: Get the mod working with textures
2. **Second**: Test all features work correctly
3. **Third**: Add JEI integration
4. **Fourth**: Add Cloth Config
5. **Later**: Consider Patchouli for v2.0

Don't add too many dependencies before the core mod is tested!

---

## Questions to Consider

**Do you want players to...**
- Look up recipes in-game? → Add **JEI**
- Edit configs in-game? → Add **Cloth Config**
- Read guides in-game? → Add **Patchouli**
- Wear jewels separately? → Add **Curios**
- See ALL effects working? → Use **Mixins**

Each library solves specific problems. Choose based on your vision for the mod!

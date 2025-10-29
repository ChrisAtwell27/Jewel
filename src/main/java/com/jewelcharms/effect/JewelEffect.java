package com.jewelcharms.effect;

import java.util.HashMap;
import java.util.Map;

public enum JewelEffect {
    // Tool-Focused Effects (Mining, Utility)
    MINING_SPEED("mining_speed", "Mining Speed", EffectCategory.TOOL),
    FORTUNE("fortune", "Fortune", EffectCategory.TOOL),
    SILK_TOUCH("silk_touch", "Silk Touch", EffectCategory.TOOL),
    AUTO_SMELT("auto_smelt", "Auto-Smelt", EffectCategory.TOOL),
    AOE_MINING("aoe_mining", "Vein Miner", EffectCategory.TOOL),
    REPLANTING("replanting", "Auto-Replant", EffectCategory.TOOL),
    TREE_FELLING("tree_felling", "Tree Capitator", EffectCategory.TOOL),
    MAGNETIC("magnetic", "Item Magnet", EffectCategory.TOOL),

    // Weapon-Focused Effects (Combat)
    DAMAGE("damage", "Sharpness", EffectCategory.WEAPON),
    LOOTING("looting", "Looting", EffectCategory.WEAPON),
    FIRE_ASPECT("fire_aspect", "Fire Aspect", EffectCategory.WEAPON),
    KNOCKBACK("knockback", "Knockback", EffectCategory.WEAPON),
    SWEEPING("sweeping", "Sweeping Edge", EffectCategory.WEAPON),
    CRITICAL_CHANCE("critical_chance", "Critical Strike", EffectCategory.WEAPON),
    LIFESTEAL("lifesteal", "Life Steal", EffectCategory.WEAPON),
    EXECUTE("execute", "Execute", EffectCategory.WEAPON),
    POISON("poison", "Venomous", EffectCategory.WEAPON),
    WEAKNESS("weakness", "Enfeebling Strike", EffectCategory.WEAPON),
    BEHEADING("beheading", "Beheading", EffectCategory.WEAPON),

    // Universal Effects (Both)
    DURABILITY("durability", "Unbreaking", EffectCategory.UNIVERSAL),
    SELF_REPAIR("self_repair", "Mending", EffectCategory.UNIVERSAL),
    EXPERIENCE_BOOST("experience_boost", "XP Boost", EffectCategory.UNIVERSAL),
    REACH("reach", "Extended Reach", EffectCategory.UNIVERSAL),

    // Player Effects (Granted when holding)
    NIGHT_VISION("night_vision", "Night Vision", EffectCategory.PLAYER),
    WATER_BREATHING("water_breathing", "Aqua Affinity", EffectCategory.PLAYER),
    SPEED("speed", "Swiftness", EffectCategory.PLAYER),
    HASTE("haste", "Haste", EffectCategory.PLAYER),
    RESISTANCE("resistance", "Resistance", EffectCategory.PLAYER),
    REGENERATION("regeneration", "Regeneration", EffectCategory.PLAYER),
    JUMP_BOOST("jump_boost", "Leaping", EffectCategory.PLAYER),
    FIRE_RESISTANCE("fire_resistance", "Fire Resistance", EffectCategory.PLAYER),

    // Special/Unique Effects
    VOID_TOUCH("void_touch", "Void Touch", EffectCategory.SPECIAL),
    ABSORPTION("absorption", "Absorption", EffectCategory.SPECIAL),
    TELEPORT_DROPS("teleport_drops", "Ender Pocket", EffectCategory.SPECIAL),
    SOULBOUND("soulbound", "Soulbound", EffectCategory.SPECIAL),
    CURSE_BINDING("curse_binding", "Curse of Binding", EffectCategory.SPECIAL);

    public enum EffectCategory {
        TOOL,      // Only works on tools (pickaxe, axe, shovel, hoe)
        WEAPON,    // Only works on weapons (sword, axe when used as weapon)
        UNIVERSAL, // Works on both tools and weapons
        PLAYER,    // Grants player effects when held
        SPECIAL    // Unique mechanics
    }

    private static final Map<String, JewelEffect> ID_MAP = new HashMap<>();

    static {
        for (JewelEffect effect : values()) {
            ID_MAP.put(effect.id, effect);
        }
    }

    private final String id;
    private final String displayName;
    private final EffectCategory category;

    JewelEffect(String id, String displayName, EffectCategory category) {
        this.id = id;
        this.displayName = displayName;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EffectCategory getCategory() {
        return category;
    }

    public boolean isApplicableToWeapon() {
        return category == EffectCategory.WEAPON || category == EffectCategory.UNIVERSAL || category == EffectCategory.PLAYER || category == EffectCategory.SPECIAL;
    }

    public boolean isApplicableToTool() {
        return category == EffectCategory.TOOL || category == EffectCategory.UNIVERSAL || category == EffectCategory.PLAYER || category == EffectCategory.SPECIAL;
    }

    public static JewelEffect fromId(String id) {
        return ID_MAP.get(id);
    }
}

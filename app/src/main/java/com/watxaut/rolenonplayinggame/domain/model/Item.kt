package com.watxaut.rolenonplayinggame.domain.model

/**
 * Represents an item that can be acquired, equipped, or sold.
 * Based on TONE_AND_STYLE_GUIDE.md - Item description guidelines
 * and FUNCTIONAL_DESIGN_DOCUMENT.md - Loot system
 */
data class Item(
    val id: String,
    val name: String,
    val description: String,
    val itemType: ItemType,
    val rarity: ItemRarity,
    val levelRequirement: Int = 1,

    // Stats (only for equipment)
    val strength: Int = 0,
    val intelligence: Int = 0,
    val agility: Int = 0,
    val luck: Int = 0,
    val charisma: Int = 0,
    val vitality: Int = 0,

    // Sell value
    val goldValue: Int = 1,

    // Equipment slot (if equipment)
    val equipmentSlot: EquipmentSlot? = null,

    // Flavor text and lore
    val flavorText: String? = null
) {
    /**
     * Get total stat bonus from this item
     */
    fun getTotalStatBonus(): Int {
        return strength + intelligence + agility + luck + charisma + vitality
    }

    /**
     * Check if item can be equipped by character level
     */
    fun canEquip(characterLevel: Int): Boolean {
        return characterLevel >= levelRequirement && equipmentSlot != null
    }
}

/**
 * Item rarity tiers
 * Per design: Common items can be humorous, legendary items are serious
 */
enum class ItemRarity(
    val displayName: String,
    val dropChance: Double, // Base drop chance multiplier
    val colorHex: String    // For UI display
) {
    JUNK("Junk", 0.4, "#8C8C8C"),           // 40% - Vendor trash
    COMMON("Common", 0.35, "#FFFFFF"),       // 35% - Basic items
    UNCOMMON("Uncommon", 0.15, "#1EFF00"),   // 15% - Better than common
    RARE("Rare", 0.07, "#0070DD"),           // 7% - Significant upgrades
    EPIC("Epic", 0.025, "#A335EE"),          // 2.5% - Powerful items
    LEGENDARY("Legendary", 0.005, "#FF8000") // 0.5% - Legendary items with lore
}

/**
 * Types of items
 */
enum class ItemType {
    WEAPON,      // Swords, axes, staves, bows, etc.
    ARMOR,       // Chest, legs, boots, etc.
    ACCESSORY,   // Rings, amulets, trinkets
    CONSUMABLE,  // Potions, food, scrolls
    MATERIAL,    // Crafting materials
    QUEST,       // Quest-specific items
    JUNK         // Vendor trash
}

/**
 * Equipment slots for items
 */
enum class EquipmentSlot(val displayName: String) {
    WEAPON("Weapon"),
    HEAD("Head"),
    CHEST("Chest"),
    LEGS("Legs"),
    FEET("Feet"),
    HANDS("Hands"),
    RING("Ring"),
    AMULET("Amulet"),
    TRINKET("Trinket")
}

/**
 * Stat types for referencing stats
 */
enum class StatType {
    STRENGTH,
    INTELLIGENCE,
    AGILITY,
    LUCK,
    CHARISMA,
    VITALITY
}

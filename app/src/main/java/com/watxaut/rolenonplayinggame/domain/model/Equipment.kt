package com.watxaut.rolenonplayinggame.domain.model

/**
 * Equipment that can be equipped by characters to increase stats.
 *
 * Per the game design:
 * - Characters can equip: 1-2 weapons (class dependent), armor, gloves, head armor, accessories
 * - Equipment increases stats (STR, INT, AGI, VIT, LUK, CHA)
 * - Monsters have <10% chance to drop common items, 1% for rare items
 * - Characters auto-equip better items
 */
data class Equipment(
    val id: String,
    val name: String,
    val description: String,
    val slot: EquipmentSlot,
    val rarity: Rarity,

    // Stat bonuses
    val strengthBonus: Int = 0,
    val intelligenceBonus: Int = 0,
    val agilityBonus: Int = 0,
    val vitalityBonus: Int = 0,
    val luckBonus: Int = 0,
    val charismaBonus: Int = 0,

    // Class affinity (for tie-breaking in auto-equip)
    val preferredClass: JobClass? = null
) {
    /**
     * Get total stat bonus value for comparison.
     */
    fun getTotalStatBonus(): Int {
        return strengthBonus + intelligenceBonus + agilityBonus +
               vitalityBonus + luckBonus + charismaBonus
    }

    /**
     * Check if this equipment is better than another for a given character.
     * Better means: higher total stats > class-relevant stats > new item
     */
    fun isBetterThan(other: Equipment?, character: Character): Boolean {
        if (other == null) return true

        val myStats = getTotalStatBonus()
        val otherStats = other.getTotalStatBonus()

        // 1. Compare total stats
        if (myStats > otherStats) return true
        if (myStats < otherStats) return false

        // 2. Tie - check class affinity
        val myClassMatch = preferredClass == character.jobClass
        val otherClassMatch = other.preferredClass == character.jobClass

        if (myClassMatch && !otherClassMatch) return true
        if (!myClassMatch && otherClassMatch) return false

        // 3. Still tied - new item wins
        return true
    }
}

/**
 * Equipment slots that characters can equip items into.
 */
enum class EquipmentSlot(val displayName: String, val maxSlots: Int) {
    WEAPON_MAIN("Main Weapon", 1),
    WEAPON_OFF("Off-hand Weapon", 1),   // Optional for dual-wielding classes
    ARMOR("Armor", 1),
    GLOVES("Gloves", 1),
    HEAD("Head Armor", 1),
    ACCESSORY("Accessory", 1);          // Rings, necklaces, etc.

    /**
     * Check if a character class can dual-wield (have both main and off-hand weapons).
     */
    companion object {
        fun canDualWield(jobClass: JobClass): Boolean {
            return when (jobClass) {
                JobClass.ASSASSIN, JobClass.ROGUE, JobClass.RANGER -> true
                else -> false
            }
        }
    }
}

/**
 * Rarity levels for equipment.
 */
enum class Rarity(
    val displayName: String,
    val dropChance: Double,  // Chance for monsters to drop (0.0 to 1.0)
    val color: Long          // Color for UI display
) {
    COMMON("Common", 0.09, 0xFFFFFFFF),      // 9% chance (white)
    RARE("Rare", 0.01, 0xFFFFD700);          // 1% chance (gold)

    fun getColorInt(): Int {
        return color.toInt()
    }
}

/**
 * Character equipment loadout - all equipped items.
 */
data class EquipmentLoadout(
    val weaponMain: Equipment? = null,
    val weaponOff: Equipment? = null,
    val armor: Equipment? = null,
    val gloves: Equipment? = null,
    val head: Equipment? = null,
    val accessory: Equipment? = null
) {
    /**
     * Get all equipped items as a list.
     */
    fun getAllEquipped(): List<Equipment> {
        return listOfNotNull(weaponMain, weaponOff, armor, gloves, head, accessory)
    }

    /**
     * Get total stat bonuses from all equipped items.
     */
    fun getTotalBonuses(): EquipmentBonuses {
        val equipped = getAllEquipped()
        return EquipmentBonuses(
            strength = equipped.sumOf { it.strengthBonus },
            intelligence = equipped.sumOf { it.intelligenceBonus },
            agility = equipped.sumOf { it.agilityBonus },
            vitality = equipped.sumOf { it.vitalityBonus },
            luck = equipped.sumOf { it.luckBonus },
            charisma = equipped.sumOf { it.charismaBonus }
        )
    }

    /**
     * Equip an item in the appropriate slot, returning a new loadout.
     */
    fun equip(item: Equipment): EquipmentLoadout {
        return when (item.slot) {
            EquipmentSlot.WEAPON_MAIN -> copy(weaponMain = item)
            EquipmentSlot.WEAPON_OFF -> copy(weaponOff = item)
            EquipmentSlot.ARMOR -> copy(armor = item)
            EquipmentSlot.GLOVES -> copy(gloves = item)
            EquipmentSlot.HEAD -> copy(head = item)
            EquipmentSlot.ACCESSORY -> copy(accessory = item)
        }
    }

    /**
     * Get currently equipped item for a slot.
     */
    fun getEquipped(slot: EquipmentSlot): Equipment? {
        return when (slot) {
            EquipmentSlot.WEAPON_MAIN -> weaponMain
            EquipmentSlot.WEAPON_OFF -> weaponOff
            EquipmentSlot.ARMOR -> armor
            EquipmentSlot.GLOVES -> gloves
            EquipmentSlot.HEAD -> head
            EquipmentSlot.ACCESSORY -> accessory
        }
    }
}

/**
 * Total stat bonuses from equipment.
 */
data class EquipmentBonuses(
    val strength: Int = 0,
    val intelligence: Int = 0,
    val agility: Int = 0,
    val vitality: Int = 0,
    val luck: Int = 0,
    val charisma: Int = 0
)

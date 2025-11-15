package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.domain.model.EquipmentSlot
import com.watxaut.rolenonplayinggame.domain.model.Item
import com.watxaut.rolenonplayinggame.domain.model.ItemRarity
import com.watxaut.rolenonplayinggame.domain.model.ItemType
import kotlin.random.Random

/**
 * Comprehensive Item Database for Aethermoor.
 * Based on TONE_AND_STYLE_GUIDE.md:
 * - Common/Junk items: Can be humorous
 * - Legendary items: Serious with deep lore
 *
 * Phase 3 - World & Content Implementation
 */
object ItemDatabase {

    // ==================== WEAPONS ====================

    private val weapons = listOf(
        // Common Weapons
        Item(
            id = "weapon_rusty_sword",
            name = "Rusty Sword",
            description = "It's seen better days. Many, many days ago.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.COMMON,
            levelRequirement = 1,
            strength = 2,
            goldValue = 5,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN
        ),
        Item(
            id = "weapon_wooden_staff",
            name = "Wooden Staff",
            description = "Just a stick, really. But a magical stick!",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.COMMON,
            levelRequirement = 1,
            intelligence = 3,
            goldValue = 6,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN
        ),
        Item(
            id = "weapon_hunters_bow",
            name = "Hunter's Bow",
            description = "Standard issue for aspiring archers.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.COMMON,
            levelRequirement = 1,
            agility = 3,
            goldValue = 8,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN
        ),

        // Uncommon Weapons
        Item(
            id = "weapon_iron_blade",
            name = "Iron Blade",
            description = "A reliable weapon forged from good steel.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.UNCOMMON,
            levelRequirement = 5,
            strength = 5,
            vitality = 1,
            goldValue = 25,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN
        ),
        Item(
            id = "weapon_apprentice_wand",
            name = "Apprentice's Wand",
            description = "Channels magical energy with surprising efficiency.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.UNCOMMON,
            levelRequirement = 5,
            intelligence = 6,
            goldValue = 30,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN
        ),

        // Rare Weapons
        Item(
            id = "weapon_silvered_longsword",
            name = "Silvered Longsword",
            description = "Blessed by Havenmoor's priests. Particularly effective against undead.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.RARE,
            levelRequirement = 15,
            strength = 10,
            charisma = 2,
            goldValue = 150,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN
        ),
        Item(
            id = "weapon_heartwood_staff",
            name = "Heartwood Staff",
            description = "Carved from the Heart Tree's fallen branch. Pulses with ancient magic.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.RARE,
            levelRequirement = 20,
            intelligence = 12,
            vitality = 3,
            goldValue = 200,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN,
            flavorText = "The forest remembers."
        ),

        // Epic Weapons
        Item(
            id = "weapon_frostbite",
            name = "Frostbite",
            description = "A blade of pure ice that never melts. Forged in the Frostpeak peaks.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.EPIC,
            levelRequirement = 30,
            strength = 18,
            intelligence = 8,
            agility = 4,
            goldValue = 800,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN,
            flavorText = "Cold as the heart of winter."
        ),
        Item(
            id = "weapon_stormcaller",
            name = "Stormcaller",
            description = "A staff that crackles with lightning. The storm obeys its wielder.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.EPIC,
            levelRequirement = 35,
            intelligence = 20,
            agility = 6,
            luck = 4,
            goldValue = 1000,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN,
            flavorText = "Thunder answers the call."
        ),

        // Legendary Weapons
        Item(
            id = "weapon_sundering_blade",
            name = "Sundering Blade",
            description = "Forged in the fires of the Sundering itself. Said to be unbreakable.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.LEGENDARY,
            levelRequirement = 45,
            strength = 30,
            vitality = 10,
            charisma = 5,
            goldValue = 5000,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN,
            flavorText = "When the world shattered, this blade was born from the chaos."
        ),
        Item(
            id = "weapon_tidecaller_trident",
            name = "Tidecaller's Trident",
            description = "The legendary weapon of the Tide Caller. Commands the very ocean.",
            itemType = ItemType.WEAPON,
            rarity = ItemRarity.LEGENDARY,
            levelRequirement = 40,
            strength = 22,
            intelligence = 18,
            agility = 10,
            goldValue = 4500,
            equipmentSlot = EquipmentSlot.WEAPON_MAIN,
            flavorText = "The sea knows no master, save one."
        )
    )

    // ==================== ARMOR ====================

    private val armor = listOf(
        // Common Armor
        Item(
            id = "armor_leather_vest",
            name = "Leather Vest",
            description = "Basic protection for new adventurers.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.COMMON,
            levelRequirement = 1,
            vitality = 2,
            goldValue = 5,
            equipmentSlot = EquipmentSlot.ARMOR
        ),
        Item(
            id = "armor_cloth_robe",
            name = "Cloth Robe",
            description = "Simple robes for aspiring mages.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.COMMON,
            levelRequirement = 1,
            intelligence = 2,
            goldValue = 6,
            equipmentSlot = EquipmentSlot.ARMOR
        ),

        // Uncommon Armor
        Item(
            id = "armor_iron_chainmail",
            name = "Iron Chainmail",
            description = "Solid protection that won't slow you down much.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.UNCOMMON,
            levelRequirement = 8,
            vitality = 5,
            strength = 2,
            goldValue = 40,
            equipmentSlot = EquipmentSlot.ARMOR
        ),
        Item(
            id = "armor_enchanted_robes",
            name = "Enchanted Robes",
            description = "Woven with threads of magic. Surprisingly durable.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.UNCOMMON,
            levelRequirement = 8,
            intelligence = 6,
            vitality = 2,
            goldValue = 50,
            equipmentSlot = EquipmentSlot.ARMOR
        ),

        // Rare Armor
        Item(
            id = "armor_thornwood_guardian",
            name = "Thornwood Guardian Plate",
            description = "Armor crafted by the Rangers of Thornwood. Light yet incredibly strong.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.RARE,
            levelRequirement = 18,
            vitality = 10,
            agility = 5,
            goldValue = 250,
            equipmentSlot = EquipmentSlot.ARMOR
        ),

        // Epic Armor
        Item(
            id = "armor_dragonscale_mail",
            name = "Dragonscale Mail",
            description = "Crafted from the scales of Hoarfang's fallen kin. Nearly impenetrable.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.EPIC,
            levelRequirement = 35,
            vitality = 20,
            strength = 8,
            goldValue = 1200,
            equipmentSlot = EquipmentSlot.ARMOR,
            flavorText = "The dragon's fury, made defense."
        ),

        // Legendary Armor
        Item(
            id = "armor_wardens_aegis",
            name = "Warden's Aegis",
            description = "The legendary armor said to protect Havenmoor itself.",
            itemType = ItemType.ARMOR,
            rarity = ItemRarity.LEGENDARY,
            levelRequirement = 50,
            vitality = 35,
            strength = 12,
            intelligence = 12,
            goldValue = 10000,
            equipmentSlot = EquipmentSlot.ARMOR,
            flavorText = "While this armor stands, Havenmoor shall never fall."
        )
    )

    // ==================== ACCESSORIES ====================

    private val accessories = listOf(
        // Uncommon Accessories
        Item(
            id = "accessory_silver_ring",
            name = "Silver Ring",
            description = "A simple band of silver. Brings good fortune.",
            itemType = ItemType.ACCESSORY,
            rarity = ItemRarity.UNCOMMON,
            levelRequirement = 5,
            luck = 3,
            goldValue = 30,
            equipmentSlot = EquipmentSlot.ACCESSORY
        ),
        Item(
            id = "accessory_scholars_pendant",
            name = "Scholar's Pendant",
            description = "Worn by students of the arcane arts.",
            itemType = ItemType.ACCESSORY,
            rarity = ItemRarity.UNCOMMON,
            levelRequirement = 5,
            intelligence = 4,
            goldValue = 35,
            equipmentSlot = EquipmentSlot.ACCESSORY
        ),

        // Rare Accessories
        Item(
            id = "accessory_moonwell_charm",
            name = "Moonwell Charm",
            description = "A charm blessed in the waters of Moonwell Glade.",
            itemType = ItemType.ACCESSORY,
            rarity = ItemRarity.RARE,
            levelRequirement = 15,
            luck = 6,
            agility = 3,
            goldValue = 180,
            equipmentSlot = EquipmentSlot.ACCESSORY,
            flavorText = "Moonlight guides the fortunate."
        ),

        // Epic Accessories
        Item(
            id = "accessory_storm_crown_fragment",
            name = "Storm Crown Fragment",
            description = "A shard of the legendary Storm Crown. Pulses with electrical energy.",
            itemType = ItemType.ACCESSORY,
            rarity = ItemRarity.EPIC,
            levelRequirement = 40,
            intelligence = 15,
            agility = 10,
            luck = 8,
            goldValue = 1500,
            equipmentSlot = EquipmentSlot.ACCESSORY,
            flavorText = "A piece of the storm itself."
        ),

        // Legendary Accessories
        Item(
            id = "accessory_first_wanderers_compass",
            name = "First Wanderer's Compass",
            description = "The compass that guided the First Wanderer to Aethermoor.",
            itemType = ItemType.ACCESSORY,
            rarity = ItemRarity.LEGENDARY,
            levelRequirement = 1,
            luck = 20,
            charisma = 15,
            goldValue = 0, // Cannot be sold
            equipmentSlot = EquipmentSlot.ACCESSORY,
            flavorText = "All paths lead to destiny."
        )
    )

    // ==================== CONSUMABLES ====================

    private val consumables = listOf(
        Item(
            id = "consumable_health_potion",
            name = "Health Potion",
            description = "Restores 50 HP. Tastes like cherry.",
            itemType = ItemType.CONSUMABLE,
            rarity = ItemRarity.COMMON,
            goldValue = 10
        ),
        Item(
            id = "consumable_greater_health_potion",
            name = "Greater Health Potion",
            description = "Restores 150 HP. Tastes significantly better.",
            itemType = ItemType.CONSUMABLE,
            rarity = ItemRarity.UNCOMMON,
            goldValue = 30
        ),
        Item(
            id = "consumable_elixir_of_strength",
            name = "Elixir of Strength",
            description = "Temporarily increases Strength by 5 for one hour.",
            itemType = ItemType.CONSUMABLE,
            rarity = ItemRarity.RARE,
            goldValue = 50
        )
    )

    // ==================== JUNK ITEMS ====================

    private val junkItems = listOf(
        Item(
            id = "junk_rusty_can",
            name = "Rusty Can",
            description = "Someone's old lunch? Hard to tell anymore.",
            itemType = ItemType.JUNK,
            rarity = ItemRarity.JUNK,
            goldValue = 1
        ),
        Item(
            id = "junk_broken_sword_hilt",
            name = "Broken Sword Hilt",
            description = "The blade is long gone. You're left with the handle of disappointment.",
            itemType = ItemType.JUNK,
            rarity = ItemRarity.JUNK,
            goldValue = 2
        ),
        Item(
            id = "junk_mysterious_goo",
            name = "Mysterious Goo",
            description = "It's... goopy. And mysterious. Do NOT taste it.",
            itemType = ItemType.JUNK,
            rarity = ItemRarity.JUNK,
            goldValue = 1,
            flavorText = "Seriously, don't."
        ),
        Item(
            id = "junk_goblin_toenail",
            name = "Goblin Toenail",
            description = "Why would you even pick this up?",
            itemType = ItemType.JUNK,
            rarity = ItemRarity.JUNK,
            goldValue = 1
        )
    )

    /**
     * Get all items
     */
    fun getAllItems(): List<Item> {
        return weapons + armor + accessories + consumables + junkItems
    }

    /**
     * Get item by ID
     */
    fun getItemById(id: String): Item? {
        return getAllItems().find { it.id == id }
    }

    /**
     * Generate random loot based on character level and luck
     */
    fun generateRandomLoot(
        characterLevel: Int,
        luckStat: Int,
        enemyType: com.watxaut.rolenonplayinggame.core.combat.EnemyType
    ): Item? {
        // Calculate drop chance based on enemy type
        val baseDropChance = when (enemyType) {
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.NORMAL -> 0.3
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.ELITE -> 0.6
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.BOSS -> 0.95
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.WORLD_BOSS -> 1.0
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.LEGENDARY -> 1.0
        }

        // Luck increases drop chance (1% per luck point)
        val adjustedDropChance = baseDropChance + (luckStat * 0.01)

        if (Random.nextDouble() > adjustedDropChance) {
            return null // No drop
        }

        // Determine rarity (luck affects this too)
        val rarity = determineRarity(luckStat, enemyType)

        // Get appropriate items for level and rarity
        val appropriateItems = getAllItems().filter {
            it.rarity == rarity &&
            it.levelRequirement <= characterLevel + 5 &&
            it.itemType != ItemType.CONSUMABLE // Consumables handled separately
        }

        return appropriateItems.randomOrNull()
    }

    /**
     * Determine item rarity based on luck and enemy type
     */
    private fun determineRarity(
        luckStat: Int,
        enemyType: com.watxaut.rolenonplayinggame.core.combat.EnemyType
    ): ItemRarity {
        val roll = Random.nextDouble()
        val luckBonus = luckStat * 0.005 // 0.5% per luck point

        // Bosses have better loot tables
        val rarityThresholds = when (enemyType) {
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.WORLD_BOSS -> listOf(0.0, 0.1, 0.3, 0.6, 0.85)
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.BOSS -> listOf(0.05, 0.2, 0.45, 0.75, 0.95)
            com.watxaut.rolenonplayinggame.core.combat.EnemyType.ELITE -> listOf(0.15, 0.4, 0.7, 0.9, 0.98)
            else -> listOf(0.4, 0.75, 0.9, 0.97, 0.995)
        }

        val adjustedRoll = roll - luckBonus

        return when {
            adjustedRoll < rarityThresholds[0] -> ItemRarity.LEGENDARY
            adjustedRoll < rarityThresholds[1] -> ItemRarity.EPIC
            adjustedRoll < rarityThresholds[2] -> ItemRarity.RARE
            adjustedRoll < rarityThresholds[3] -> ItemRarity.UNCOMMON
            adjustedRoll < rarityThresholds[4] -> ItemRarity.COMMON
            else -> ItemRarity.JUNK
        }
    }

    /**
     * Get starting equipment for a character
     */
    fun getStartingEquipment(): List<Item> {
        return listOf(
            getItemById("weapon_rusty_sword")!!,
            getItemById("armor_leather_vest")!!
        )
    }

    /**
     * Get items by rarity
     */
    fun getItemsByRarity(rarity: ItemRarity): List<Item> {
        return getAllItems().filter { it.rarity == rarity }
    }

    /**
     * Get items by type
     */
    fun getItemsByType(type: ItemType): List<Item> {
        return getAllItems().filter { it.itemType == type }
    }

    /**
     * Get weapons appropriate for character level
     */
    fun getWeaponsForLevel(level: Int): List<Item> {
        return weapons.filter {
            it.levelRequirement <= level &&
            it.levelRequirement >= level - 10
        }
    }
}

package com.watxaut.rolenonplayinggame.domain.model

/**
 * Secondary Mission - fun side quests that can be discovered during gameplay.
 * Per user requirements:
 * - 100 different secondary missions
 * - 1% chance of encountering a secondary mission
 * - Hero can do multiple secondary missions simultaneously
 * - Win conditions vary (get item, visit city, etc.)
 * - Rewards: always XP and gold, 20% equipment, 2% rare equipment
 */
data class SecondaryMission(
    val id: String,
    val name: String,
    val description: String,
    val category: SecondaryMissionCategory,

    // Win condition
    val winCondition: SecondaryWinCondition,

    // Rewards
    val baseExperience: Long,
    val baseGold: Int,
    val equipmentChance: Float = 0.20f, // 20% chance
    val rareEquipmentChance: Float = 0.02f, // 2% chance

    // Flavor text for completion
    val completionText: String,

    // Discovery chance per action
    val discoveryChance: Float = 0.01f // 1% chance
)

/**
 * Categories for organizing secondary missions
 */
enum class SecondaryMissionCategory {
    EXPLORATION,    // Visit locations
    COLLECTION,     // Collect items
    COMBAT,         // Defeat enemies
    SOCIAL,         // Interact with NPCs
    SURVIVAL,       // Survive challenges
    LUCK,           // Luck-based challenges
    ACHIEVEMENT     // Meta achievements
}

/**
 * Win conditions for secondary missions
 */
sealed class SecondaryWinCondition {
    data class VisitLocation(val locationId: String, val locationName: String) : SecondaryWinCondition()
    data class CollectItem(val itemId: String, val itemName: String, val quantity: Int = 1) : SecondaryWinCondition()
    data class DefeatEnemy(val enemyType: String, val count: Int) : SecondaryWinCondition()
    data class ReachLevel(val level: Int) : SecondaryWinCondition()
    data class AccumulateGold(val goldAmount: Int) : SecondaryWinCondition()
    data class EquipRarity(val rarity: Rarity) : SecondaryWinCondition()
    data class UseStat(val stat: StatType, val minimumValue: Int) : SecondaryWinCondition()
    data class SurviveDays(val days: Int) : SecondaryWinCondition()
    data class CriticalHits(val count: Int) : SecondaryWinCondition()
    data class TradeWithNPC(val npcType: String, val times: Int) : SecondaryWinCondition()

    /**
     * Get display text for the win condition
     */
    fun getDisplayText(): String = when (this) {
        is VisitLocation -> "Visit $locationName"
        is CollectItem -> "Collect $quantity x $itemName"
        is DefeatEnemy -> "Defeat $count $enemyType"
        is ReachLevel -> "Reach Level $level"
        is AccumulateGold -> "Accumulate $goldAmount gold"
        is EquipRarity -> "Equip a ${rarity.displayName} item"
        is UseStat -> "Have ${stat.name} stat at least $minimumValue"
        is SurviveDays -> "Survive $days days"
        is CriticalHits -> "Land $count critical hits"
        is TradeWithNPC -> "Trade with $npcType $times times"
    }

    /**
     * Check if condition is met based on current game state
     */
    fun isMet(character: Character, context: MissionContext): Boolean = when (this) {
        is VisitLocation -> locationId in character.discoveredLocations
        is CollectItem -> context.hasItem(itemId, quantity)
        is DefeatEnemy -> context.getEnemyKillCount(enemyType) >= count
        is ReachLevel -> character.level >= level
        is AccumulateGold -> character.gold >= goldAmount
        is EquipRarity -> context.hasEquippedRarity(rarity)
        is UseStat -> character.getTotalStats().getStat(stat) >= minimumValue
        is SurviveDays -> context.daysSurvived >= days
        is CriticalHits -> context.criticalHits >= count
        is TradeWithNPC -> context.getTradeCount(npcType) >= times
    }
}

/**
 * Context for checking mission conditions
 * This would be populated by the game state
 */
data class MissionContext(
    val inventory: Map<String, Int> = emptyMap(),
    val enemyKills: Map<String, Int> = emptyMap(),
    val equippedItems: List<Equipment> = emptyList(),
    val daysSurvived: Int = 0,
    val criticalHits: Int = 0,
    val npcTrades: Map<String, Int> = emptyMap()
) {
    fun hasItem(itemId: String, quantity: Int): Boolean {
        return (inventory[itemId] ?: 0) >= quantity
    }

    fun getEnemyKillCount(enemyType: String): Int {
        return enemyKills[enemyType] ?: 0
    }

    fun hasEquippedRarity(rarity: Rarity): Boolean {
        return equippedItems.any { it.rarity == rarity }
    }

    fun getTradeCount(npcType: String): Int {
        return npcTrades[npcType] ?: 0
    }
}

/**
 * Progress tracking for a secondary mission
 */
data class SecondaryMissionProgress(
    val missionId: String,
    val status: SecondaryMissionStatus,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val currentProgress: String = "" // For display purposes
) {
    fun isComplete(): Boolean = status == SecondaryMissionStatus.COMPLETED
    fun isOngoing(): Boolean = status == SecondaryMissionStatus.ONGOING
}

enum class SecondaryMissionStatus {
    ONGOING,
    COMPLETED,
    ABANDONED // Future: if missions can be abandoned
}

/**
 * Helper extension to get stat from CharacterStats
 */
private fun CharacterStats.getStat(stat: StatType): Int = when (stat) {
    StatType.STRENGTH -> strength
    StatType.INTELLIGENCE -> intelligence
    StatType.AGILITY -> agility
    StatType.LUCK -> luck
    StatType.CHARISMA -> charisma
    StatType.VITALITY -> vitality
}

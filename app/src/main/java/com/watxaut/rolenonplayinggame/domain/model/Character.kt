package com.watxaut.rolenonplayinggame.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Core character model representing a player's autonomous character
 */
data class Character(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val level: Int = 1,
    val experience: Long = 0,

    // Core stats
    val strength: Int = 1,
    val intelligence: Int = 1,
    val agility: Int = 1,
    val luck: Int = 1,
    val charisma: Int = 1,
    val vitality: Int = 1,

    // Current state
    val currentHp: Int,
    val maxHp: Int,
    val currentLocation: String = "heartlands_havenmoor", // Location ID (snake_case)

    // Personality (hidden from player initially)
    val personalityTraits: PersonalityTraits,

    // Job class
    val jobClass: JobClass,

    // Resources
    val gold: Long = 0,

    // Inventory and equipment
    val inventory: List<String> = emptyList(),
    val equipment: EquipmentLoadout = EquipmentLoadout(),

    // Discovered locations (stored as location IDs)
    val discoveredLocations: List<String> = listOf("heartlands_havenmoor"),

    // Active quests (stored as JSON)
    val activeQuests: List<String> = emptyList(),

    // Mission tracking
    val activePrincipalMissionId: String? = null,
    val principalMissionStartedAt: Instant? = null,
    val principalMissionCompletedCount: Int = 0,

    // Timestamps
    val createdAt: Instant = Instant.now(),
    val lastActiveAt: Instant = Instant.now()
) {
    /**
     * Calculate experience needed for next level
     * Uses exponential curve: 100 * (level ^ 1.5)
     * Matches the TypeScript implementation in offline simulation
     */
    fun experienceForNextLevel(): Long {
        return (100 * Math.pow(level.toDouble(), 1.5)).toLong()
    }

    /**
     * Check if character should level up
     */
    fun shouldLevelUp(): Boolean {
        return experience >= experienceForNextLevel()
    }

    /**
     * Get health percentage
     */
    fun getHealthPercentage(): Float {
        return currentHp.toFloat() / maxHp.toFloat()
    }

    /**
     * Check if character is in danger (low health)
     */
    fun isInDanger(): Boolean {
        return getHealthPercentage() < 0.3f
    }

    /**
     * Get stat value by type
     */
    fun getStat(statType: StatType): Int {
        return when (statType) {
            StatType.STRENGTH -> strength
            StatType.INTELLIGENCE -> intelligence
            StatType.AGILITY -> agility
            StatType.LUCK -> luck
            StatType.CHARISMA -> charisma
            StatType.VITALITY -> vitality
        }
    }

    /**
     * Calculate max HP based on vitality (including equipment bonuses)
     */
    fun calculateMaxHp(): Int {
        val bonuses = equipment.getTotalBonuses()
        val totalVit = vitality + bonuses.vitality
        return 50 + (totalVit * 10) + (level * 5)
    }

    /**
     * Get total stats including equipment bonuses.
     */
    fun getTotalStats(): CharacterStats {
        val bonuses = equipment.getTotalBonuses()
        return CharacterStats(
            strength = strength + bonuses.strength,
            intelligence = intelligence + bonuses.intelligence,
            agility = agility + bonuses.agility,
            luck = luck + bonuses.luck,
            charisma = charisma + bonuses.charisma,
            vitality = vitality + bonuses.vitality
        )
    }

    companion object {
        /**
         * Create a new character with initial stat allocation
         */
        fun create(
            userId: String,
            name: String,
            jobClass: JobClass,
            initialStats: Map<StatType, Int>
        ): Character {
            val vitality = initialStats[StatType.VITALITY] ?: 1
            val maxHp = 50 + (vitality * 10)

            val personalityTraits = PersonalityTraits.forJobClass(jobClass)

            return Character(
                userId = userId,
                name = name,
                jobClass = jobClass,
                strength = initialStats[StatType.STRENGTH] ?: 1,
                intelligence = initialStats[StatType.INTELLIGENCE] ?: 1,
                agility = initialStats[StatType.AGILITY] ?: 1,
                luck = initialStats[StatType.LUCK] ?: 1,
                charisma = initialStats[StatType.CHARISMA] ?: 1,
                vitality = vitality,
                currentHp = maxHp,
                maxHp = maxHp,
                personalityTraits = personalityTraits
            )
        }
    }
}

/**
 * Character stats container
 */
data class CharacterStats(
    val strength: Int,
    val intelligence: Int,
    val agility: Int,
    val luck: Int,
    val charisma: Int,
    val vitality: Int
) {
    fun getTotalPoints(): Int {
        return strength + intelligence + agility + luck + charisma + vitality
    }

    fun toMap(): Map<StatType, Int> {
        return mapOf(
            StatType.STRENGTH to strength,
            StatType.INTELLIGENCE to intelligence,
            StatType.AGILITY to agility,
            StatType.LUCK to luck,
            StatType.CHARISMA to charisma,
            StatType.VITALITY to vitality
        )
    }

    companion object {
        /**
         * Create default starting stats (10 points total, minimum 1 per stat)
         */
        fun default(): CharacterStats {
            return CharacterStats(
                strength = 1,
                intelligence = 1,
                agility = 1,
                luck = 1,
                charisma = 1,
                vitality = 5  // Extra points in vitality for survivability
            )
        }
    }
}

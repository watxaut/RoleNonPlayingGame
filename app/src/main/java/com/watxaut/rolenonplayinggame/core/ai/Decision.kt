package com.watxaut.rolenonplayinggame.core.ai

/**
 * Represents a decision made by the autonomous AI
 */
sealed class Decision {
    /**
     * Character decides to rest and heal
     */
    data class Rest(val location: String) : Decision()

    /**
     * Character decides to heal at an inn
     */
    data class HealAtInn(val location: String, val cost: Long) : Decision()

    /**
     * Character decides to explore a location
     */
    data class Explore(val targetLocation: String) : Decision()

    /**
     * Character decides to engage in combat
     */
    data class Combat(val enemyType: String, val estimatedDifficulty: Int) : Decision()

    /**
     * Character decides to flee from danger
     */
    data class Flee(val reason: String) : Decision()

    /**
     * Character decides to accept a quest
     */
    data class AcceptQuest(val questId: String, val questName: String) : Decision()

    /**
     * Character decides to continue working on active quest
     */
    data class ContinueQuest(val questId: String) : Decision()

    /**
     * Character decides to idle/wait
     */
    data object Idle : Decision()

    /**
     * Character decides to buy items
     */
    data class Shop(val location: String, val itemType: String) : Decision()

    /**
     * Character decides to approach another character for interaction
     */
    data class Encounter(val otherCharacterId: String, val otherCharacterName: String) : Decision()
}

/**
 * Priority levels for AI decision-making.
 * Higher priority decisions are evaluated first.
 */
enum class DecisionPriority(val level: Int) {
    // Week 2: Only implementing top 3 priorities
    SURVIVAL(1),        // HP < 30%, flee or heal immediately
    CRITICAL_NEEDS(2),  // Low resources, need to rest or shop
    ACTIVE_QUEST(3),    // Continue working on current quest

    // Week 3+: Additional priorities
    OPPORTUNITY(4),     // Evaluate risks/rewards of nearby opportunities
    RESOURCE_GATHERING(5), // Hunt monsters, gather loot
    EXPLORATION(6),     // Discover new areas
    SOCIAL(7),          // Interact with nearby characters
    IDLE(8);            // Nothing urgent to do

    companion object {
        /**
         * Get priorities for Week 2 implementation (1-3 only)
         */
        fun getBasicPriorities(): List<DecisionPriority> {
            return listOf(SURVIVAL, CRITICAL_NEEDS, ACTIVE_QUEST)
        }
    }
}

/**
 * Context information for decision-making
 */
data class DecisionContext(
    val currentLocation: String,
    val currentHp: Int,
    val maxHp: Int,
    val gold: Long,
    val level: Int,
    val hasActiveQuest: Boolean = false,
    val nearbyLocations: List<String> = emptyList(),
    val canRest: Boolean = true,
    val hasInnAccess: Boolean = false,
    val innHealCost: Long = 10L,
    val nearbyCharacters: List<NearbyCharacterInfo> = emptyList() // Social feature
) {
    fun getHealthPercentage(): Float = currentHp.toFloat() / maxHp.toFloat()
    fun isInDanger(): Boolean = getHealthPercentage() < 0.3f
    fun isLowResources(): Boolean = gold < 50L
    fun hasNearbyCharacters(): Boolean = nearbyCharacters.isNotEmpty()
}

/**
 * Simple data class for nearby character information in decision context
 */
data class NearbyCharacterInfo(
    val id: String,
    val name: String,
    val level: Int,
    val personalitySocial: Float
)

package com.watxaut.rolenonplayinggame.domain.model

import java.time.Instant

/**
 * Activity represents a single action or event in a character's history.
 * Used for the activity log that players observe.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md: Activity logging with last 500 entries,
 * event categorization, and timestamp management.
 */
data class Activity(
    val id: Long = 0,
    val characterId: String,
    val timestamp: Instant,
    val type: ActivityType,
    val description: String,
    val isMajorEvent: Boolean = false,
    val rewards: ActivityRewards? = null,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Types of activities that can occur in the game.
 * Based on TECHNICAL_IMPLEMENTATION_DOCUMENT.md section on activity logging.
 */
enum class ActivityType {
    COMBAT,           // Fighting monsters or other characters
    EXPLORATION,      // Discovering new locations
    QUEST,            // Quest-related activities
    SOCIAL,           // Interactions with other characters
    REST,             // Healing and recovery
    SHOPPING,         // Buying/selling items
    LEVEL_UP,         // Character advancement
    DEATH,            // Character death and respawn
    LOOT,             // Finding items
    TRAVEL,           // Moving between locations
    IDLE              // No significant action
}

/**
 * Rewards gained from an activity.
 * Nullable - not all activities grant rewards.
 */
data class ActivityRewards(
    val experience: Int = 0,
    val gold: Int = 0,
    val items: List<String> = emptyList() // Item IDs (to be implemented in future weeks)
)

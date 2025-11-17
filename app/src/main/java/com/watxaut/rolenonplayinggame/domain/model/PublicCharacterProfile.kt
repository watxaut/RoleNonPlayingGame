package com.watxaut.rolenonplayinggame.domain.model

import java.time.Instant

/**
 * Public profile information for a character (visible to all players)
 * Does NOT include personality traits (those remain hidden)
 */
data class PublicCharacterProfile(
    val id: String,
    val name: String,
    val level: Int,
    val jobClass: JobClass,
    val createdAt: Instant,
    val currentLocation: String,

    // Stats (public for profile comparison)
    val strength: Int,
    val intelligence: Int,
    val agility: Int,
    val luck: Int,
    val charisma: Int,
    val vitality: Int,

    // Achievements and statistics
    val achievementCount: Int = 0,
    val totalKills: Int = 0,
    val totalEncounters: Int = 0,

    // Display computed properties
    val powerLevel: Int = (strength + intelligence + agility + vitality) / 4 + level * 2
) {
    /**
     * Get job class color for UI display
     */
    fun getJobClassColor(): Long {
        return jobClass.getColor()
    }

    /**
     * Get formatted creation date
     */
    fun getAccountAge(): String {
        val now = Instant.now()
        val daysSinceCreation = java.time.Duration.between(createdAt, now).toDays()

        return when {
            daysSinceCreation < 1 -> "Created today"
            daysSinceCreation == 1L -> "Created yesterday"
            daysSinceCreation < 7 -> "Created $daysSinceCreation days ago"
            daysSinceCreation < 30 -> "Created ${daysSinceCreation / 7} weeks ago"
            else -> "Created ${daysSinceCreation / 30} months ago"
        }
    }

    /**
     * Get formatted stats summary
     */
    fun getStatsSum(): Int {
        return strength + intelligence + agility + luck + charisma + vitality
    }
}

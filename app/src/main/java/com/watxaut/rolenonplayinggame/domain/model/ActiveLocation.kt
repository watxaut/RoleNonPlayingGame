package com.watxaut.rolenonplayinggame.domain.model

import java.time.Instant

/**
 * Represents a character's current active location for encounter matching
 */
data class ActiveLocation(
    val characterId: String,
    val location: String,
    val isAvailableForEncounters: Boolean = true,
    val lastUpdate: Instant = Instant.now()
) {
    /**
     * Check if this location is still fresh (within last 5 minutes)
     */
    fun isFresh(): Boolean {
        val fiveMinutesAgo = Instant.now().minusSeconds(5 * 60)
        return lastUpdate.isAfter(fiveMinutesAgo)
    }

    /**
     * Check if character is stale (inactive for more than 10 minutes)
     */
    fun isStale(): Boolean {
        val tenMinutesAgo = Instant.now().minusSeconds(10 * 60)
        return lastUpdate.isBefore(tenMinutesAgo)
    }
}

/**
 * Character present in a location (for encounter matching)
 */
data class NearbyCharacter(
    val id: String,
    val name: String,
    val level: Int,
    val jobClass: JobClass,
    val personalitySocial: Float,
    val location: String,
    val lastSeen: Instant
) {
    /**
     * Calculate level difference with another character
     */
    fun levelDifferenceWith(otherLevel: Int): Int {
        return kotlin.math.abs(level - otherLevel)
    }

    /**
     * Check if character is friendly (high social personality)
     */
    fun isFriendly(): Boolean {
        return personalitySocial > 0.6f
    }
}

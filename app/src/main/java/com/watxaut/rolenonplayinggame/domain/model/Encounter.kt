package com.watxaut.rolenonplayinggame.domain.model

import java.time.Instant

/**
 * Represents a social encounter between two characters
 */
data class Encounter(
    val id: String,
    val character1Id: String,
    val character2Id: String,
    val location: String,
    val encounterType: EncounterType,
    val status: EncounterStatus,
    val outcome: EncounterOutcome? = null,
    val createdAt: Instant,
    val completedAt: Instant? = null
)

/**
 * Type of encounter between characters
 */
enum class EncounterType {
    GREETING,
    TRADE,
    PARTY,
    COMBAT,
    IGNORE;

    fun getDisplayName(): String = when (this) {
        GREETING -> "Friendly Greeting"
        TRADE -> "Trade"
        PARTY -> "Party Formation"
        COMBAT -> "Combat"
        IGNORE -> "Passed By"
    }

    fun getDescription(): String = when (this) {
        GREETING -> "Characters exchanged greetings"
        TRADE -> "Characters traded items or gold"
        PARTY -> "Characters formed a temporary party"
        COMBAT -> "Characters engaged in combat"
        IGNORE -> "Characters passed without interaction"
    }
}

/**
 * Status of an encounter
 */
enum class EncounterStatus {
    INITIATED,
    NEGOTIATING,
    COMPLETED,
    FAILED
}

/**
 * Outcome of an encounter
 */
data class EncounterOutcome(
    val success: Boolean,
    val description: String,
    val result: String? = null,
    val relationship: String? = null,
    val goldChange: Long? = null,
    val damageDealt: Int? = null,
    val itemsExchanged: List<String>? = null,
    val partyDurationMinutes: Int? = null,
    val metadata: Map<String, Any>? = null
)

/**
 * Encounter history entry with other character info
 */
data class EncounterHistoryEntry(
    val encounterId: String,
    val otherCharacterId: String,
    val otherCharacterName: String,
    val otherCharacterLevel: Int,
    val encounterType: EncounterType,
    val location: String,
    val outcome: EncounterOutcome,
    val timestamp: Instant
)

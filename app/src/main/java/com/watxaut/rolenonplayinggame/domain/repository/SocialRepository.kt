package com.watxaut.rolenonplayinggame.domain.repository

import com.watxaut.rolenonplayinggame.domain.model.*

/**
 * Repository interface for social/multiplayer features
 */
interface SocialRepository {

    // Active Location Management
    suspend fun updateActiveLocation(
        characterId: String,
        location: String,
        isAvailable: Boolean = true
    ): Result<Unit>

    suspend fun removeActiveLocation(characterId: String): Result<Unit>

    suspend fun findNearbyCharacters(
        characterId: String,
        location: String,
        maxResults: Int = 10
    ): Result<List<NearbyCharacter>>

    // Encounter Management
    suspend fun coordinateEncounter(
        character1Id: String,
        character2Id: String,
        location: String
    ): Result<Encounter>

    suspend fun getEncounterHistory(
        characterId: String,
        limit: Int = 50
    ): Result<List<EncounterHistoryEntry>>

    // Public Character Profiles
    suspend fun getPublicProfile(characterId: String): Result<PublicCharacterProfile>

    suspend fun getTopCharacters(limit: Int = 20): Result<List<PublicCharacterProfile>>
}

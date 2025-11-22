package com.watxaut.rolenonplayinggame.data.repository

import android.util.Log
import com.watxaut.rolenonplayinggame.domain.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for social/multiplayer features
 * Handles encounters, active locations, and public character profiles
 */
@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : com.watxaut.rolenonplayinggame.domain.repository.SocialRepository {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val TAG = "SocialRepository"
        private const val TABLE_ENCOUNTERS = "encounters"
        private const val TABLE_ACTIVE_LOCATIONS = "active_locations"
        private const val VIEW_PUBLIC_PROFILES = "public_character_profiles"
        private const val FUNCTION_COORDINATE_ENCOUNTER = "coordinate-encounter"
    }

    // ==========================================
    // Active Location Management
    // ==========================================

    /**
     * Update character's active location for encounter matching
     */
    override suspend fun updateActiveLocation(
        characterId: String,
        location: String,
        isAvailable: Boolean = true
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val locationData = mapOf(
                "character_id" to characterId,
                "location" to location,
                "is_available_for_encounters" to isAvailable,
                "last_update" to Instant.now().toString()
            )

            supabase.from(TABLE_ACTIVE_LOCATIONS)
                .upsert(locationData)

            Log.d(TAG, "Updated active location for $characterId to $location")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update active location", e)
            Result.failure(e)
        }
    }

    /**
     * Remove character from active locations (e.g., when app closes)
     */
    override suspend fun removeActiveLocation(characterId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.from(TABLE_ACTIVE_LOCATIONS)
                .delete {
                    filter {
                        eq("character_id", characterId)
                    }
                }

            Log.d(TAG, "Removed active location for $characterId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove active location", e)
            Result.failure(e)
        }
    }

    /**
     * Find nearby characters in the same location
     */
    override suspend fun findNearbyCharacters(
        characterId: String,
        location: String,
        maxResults: Int = 10
    ): Result<List<NearbyCharacter>> = withContext(Dispatchers.IO) {
        try {
            @Serializable
            data class NearbyCharacterData(
                val id: String,
                val name: String,
                val level: Int,
                val job_class: String,
                val personality_social: Float
            )

            // Query active_locations joined with characters
            val response = supabase.from(TABLE_ACTIVE_LOCATIONS)
                .select(
                    columns = Columns.raw("""
                        character_id,
                        location,
                        last_update,
                        characters!inner (
                            id,
                            name,
                            level,
                            job_class,
                            personality_social
                        )
                    """.trimIndent())
                ) {
                    filter {
                        eq("location", location)
                        neq("character_id", characterId)
                        eq("is_available_for_encounters", true)
                    }
                    limit(maxResults.toLong())
                }

            // Parse and transform results
            val nearbyCharacters = mutableListOf<NearbyCharacter>()
            // TODO: Parse the complex response structure
            // For now, return empty list - this requires more complex JSON parsing

            Log.d(TAG, "Found ${nearbyCharacters.size} nearby characters")
            Result.success(nearbyCharacters)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find nearby characters", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // Encounter Management
    // ==========================================

    /**
     * Coordinate an encounter between two characters (calls Edge Function)
     */
    override suspend fun coordinateEncounter(
        character1Id: String,
        character2Id: String,
        location: String
    ): Result<Encounter> = withContext(Dispatchers.IO) {
        try {
            @Serializable
            data class EncounterRequest(
                val character1Id: String,
                val character2Id: String,
                val location: String
            )

            @Serializable
            data class EncounterResponse(
                val success: Boolean,
                val encounter: EncounterData,
                val outcome: Map<String, Any?>,
                val interaction_type: String
            )

            @Serializable
            data class EncounterData(
                val id: String,
                val character1_id: String,
                val character2_id: String,
                val location: String,
                val encounter_type: String,
                val status: String,
                val created_at: String,
                val completed_at: String?
            )

            val request = EncounterRequest(
                character1Id = character1Id,
                character2Id = character2Id,
                location = location
            )

            val response = supabase.functions.invoke(
                function = FUNCTION_COORDINATE_ENCOUNTER,
                body = request
            )

            val encounterResponse = json.decodeFromString<EncounterResponse>(response.body?.toString() ?: "{}")

            val encounter = Encounter(
                id = encounterResponse.encounter.id,
                character1Id = encounterResponse.encounter.character1_id,
                character2Id = encounterResponse.encounter.character2_id,
                location = encounterResponse.encounter.location,
                encounterType = EncounterType.valueOf(encounterResponse.encounter.encounter_type.uppercase()),
                status = EncounterStatus.valueOf(encounterResponse.encounter.status.uppercase()),
                outcome = EncounterOutcome(
                    success = encounterResponse.outcome["success"] as? Boolean ?: false,
                    description = encounterResponse.outcome["description"] as? String ?: "Unknown outcome"
                ),
                createdAt = Instant.parse(encounterResponse.encounter.created_at),
                completedAt = encounterResponse.encounter.completed_at?.let { Instant.parse(it) }
            )

            Log.d(TAG, "Successfully coordinated encounter: ${encounter.id}")
            Result.success(encounter)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to coordinate encounter", e)
            Result.failure(e)
        }
    }

    /**
     * Get encounter history for a character
     */
    override suspend fun getEncounterHistory(
        characterId: String,
        limit: Int = 50
    ): Result<List<EncounterHistoryEntry>> = withContext(Dispatchers.IO) {
        try {
            @Serializable
            data class EncounterHistoryData(
                val id: String,
                val character1_id: String,
                val character2_id: String,
                val location: String,
                val encounter_type: String,
                val outcome: Map<String, Any?>?,
                val created_at: String
            )

            val encountersData = supabase.from(TABLE_ENCOUNTERS)
                .select() {
                    filter {
                        or("character1_id.eq.$characterId,character2_id.eq.$characterId")
                        eq("status", "completed")
                    }
                    order("created_at", order = Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<EncounterHistoryData>()

            // Transform to EncounterHistoryEntry
            // TODO: Need to fetch other character names - requires joining
            val history = encountersData.map { data ->
                EncounterHistoryEntry(
                    encounterId = data.id,
                    otherCharacterId = if (data.character1_id == characterId) data.character2_id else data.character1_id,
                    otherCharacterName = "Unknown", // TODO: Fetch from characters table
                    otherCharacterLevel = 1, // TODO: Fetch from characters table
                    encounterType = EncounterType.valueOf(data.encounter_type.uppercase()),
                    location = data.location,
                    outcome = EncounterOutcome(
                        success = data.outcome?.get("success") as? Boolean ?: false,
                        description = data.outcome?.get("description") as? String ?: "No description"
                    ),
                    timestamp = Instant.parse(data.created_at)
                )
            }

            Log.d(TAG, "Retrieved ${history.size} encounter history entries")
            Result.success(history)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get encounter history", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // Public Character Profiles
    // ==========================================

    /**
     * Get public profile for a character
     */
    override suspend fun getPublicProfile(characterId: String): Result<PublicCharacterProfile> = withContext(Dispatchers.IO) {
        try {
            @Serializable
            data class PublicProfileData(
                val id: String,
                val name: String,
                val level: Int,
                val job_class: String,
                val created_at: String,
                val current_location: String,
                val strength: Int,
                val intelligence: Int,
                val agility: Int,
                val luck: Int,
                val charisma: Int,
                val vitality: Int,
                val achievement_count: Int? = 0,
                val total_kills: Int? = 0,
                val total_encounters: Int? = 0
            )

            val profileData = supabase.from(VIEW_PUBLIC_PROFILES)
                .select() {
                    filter {
                        eq("id", characterId)
                    }
                }
                .decodeSingle<PublicProfileData>()

            val profile = PublicCharacterProfile(
                id = profileData.id,
                name = profileData.name,
                level = profileData.level,
                jobClass = JobClass.valueOf(profileData.job_class.uppercase()),
                createdAt = Instant.parse(profileData.created_at),
                currentLocation = profileData.current_location,
                strength = profileData.strength,
                intelligence = profileData.intelligence,
                agility = profileData.agility,
                luck = profileData.luck,
                charisma = profileData.charisma,
                vitality = profileData.vitality,
                achievementCount = profileData.achievement_count ?: 0,
                totalKills = profileData.total_kills ?: 0,
                totalEncounters = profileData.total_encounters ?: 0
            )

            Log.d(TAG, "Retrieved public profile for ${profile.name}")
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get public profile", e)
            Result.failure(e)
        }
    }

    /**
     * Get top characters from leaderboard (for social discovery)
     */
    override suspend fun getTopCharacters(limit: Int = 20): Result<List<PublicCharacterProfile>> = withContext(Dispatchers.IO) {
        try {
            @Serializable
            data class PublicProfileData(
                val id: String,
                val name: String,
                val level: Int,
                val job_class: String,
                val created_at: String,
                val current_location: String,
                val strength: Int,
                val intelligence: Int,
                val agility: Int,
                val luck: Int,
                val charisma: Int,
                val vitality: Int,
                val achievement_count: Int? = 0,
                val total_kills: Int? = 0,
                val total_encounters: Int? = 0
            )

            val profilesData = supabase.from(VIEW_PUBLIC_PROFILES)
                .select() {
                    order("level", order = Order.DESCENDING)
                    order("total_kills", order = Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<PublicProfileData>()

            val profiles = profilesData.map { data ->
                PublicCharacterProfile(
                    id = data.id,
                    name = data.name,
                    level = data.level,
                    jobClass = JobClass.valueOf(data.job_class.uppercase()),
                    createdAt = Instant.parse(data.created_at),
                    currentLocation = data.current_location,
                    strength = data.strength,
                    intelligence = data.intelligence,
                    agility = data.agility,
                    luck = data.luck,
                    charisma = data.charisma,
                    vitality = data.vitality,
                    achievementCount = data.achievement_count ?: 0,
                    totalKills = data.total_kills ?: 0,
                    totalEncounters = data.total_encounters ?: 0
                )
            }

            Log.d(TAG, "Retrieved ${profiles.size} top characters")
            Result.success(profiles)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get top characters", e)
            Result.failure(e)
        }
    }
}

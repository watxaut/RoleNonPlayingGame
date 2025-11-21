package com.watxaut.rolenonplayinggame.core.lifecycle

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.watxaut.rolenonplayinggame.BuildConfig
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.remote.api.SupabaseApi
import com.watxaut.rolenonplayinggame.data.remote.dto.ActivityDto
import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationResponse
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.ActivityRewards
import com.watxaut.rolenonplayinggame.domain.model.ActivityType
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages offline simulation state and execution
 */
@Singleton
class OfflineSimulationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val characterDao: CharacterDao,
    private val supabaseApi: SupabaseApi,
    private val authRepository: AuthRepository,
    private val characterRepository: CharacterRepository,
    private val activityRepository: ActivityRepository,
    private val missionProgressRepository: MissionProgressRepository
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "offline_simulation_prefs",
        Context.MODE_PRIVATE
    )

    private val _simulationState = MutableStateFlow<OfflineSimulationState>(
        OfflineSimulationState.Idle
    )
    val simulationState: StateFlow<OfflineSimulationState> = _simulationState.asStateFlow()

    companion object {
        private const val TAG = "OfflineSimulationManager"
        private const val PREF_LAST_ACTIVE = "last_active_timestamp"
        private const val PREF_CACHED_USER_ID = "cached_user_id"

        private const val MAX_RETRIES = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L // 1 second

        private fun logDebug(message: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, message)
            }
        }

        private fun logError(message: String, throwable: Throwable? = null) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    /**
     * Get cached user ID from preferences
     * This is more reliable than checking auth session on app foreground
     */
    private fun getCachedUserId(): String? {
        return prefs.getString(PREF_CACHED_USER_ID, null)
    }

    /**
     * Cache the current user ID for offline simulation tracking
     */
    suspend fun cacheCurrentUserId() {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            prefs.edit().putString(PREF_CACHED_USER_ID, userId).apply()
            logDebug("Cached user ID")
        }
    }

    /**
     * Clear cached user ID (call on logout)
     */
    fun clearCachedUserId() {
        prefs.edit().remove(PREF_CACHED_USER_ID).apply()
        logDebug("Cleared cached user ID")
    }

    /**
     * Get user-specific preference key using cached user ID
     */
    private fun getUserPrefKey(): String? {
        val userId = getCachedUserId()
        return if (userId != null) {
            "${PREF_LAST_ACTIVE}_$userId"
        } else {
            logDebug("No cached user ID found")
            null
        }
    }

    /**
     * Record when the app goes to background
     * This is the ONLY place we set the timestamp
     */
    fun recordAppBackgrounded() {
        val prefKey = getUserPrefKey()
        if (prefKey == null) {
            logDebug("No cached user ID, skipping background recording")
            return
        }

        val timestamp = System.currentTimeMillis()
        prefs.edit().putLong(prefKey, timestamp).apply()
        logDebug("App backgrounded at $timestamp")
    }

    /**
     * Check if device has active network connection
     */
    private fun hasNetworkConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Check if offline simulation is needed and run it
     * Uses retry logic with exponential backoff to handle network initialization delays
     */
    suspend fun checkAndRunOfflineSimulation() {
        val prefKey = getUserPrefKey()
        if (prefKey == null) {
            logDebug("No user logged in, skipping offline simulation check")
            return
        }

        val lastActiveTime = prefs.getLong(prefKey, 0L)

        if (lastActiveTime == 0L) {
            logDebug("No last active time recorded, skipping simulation (first time login)")
            return
        }

        val now = System.currentTimeMillis()
        val timeOfflineMs = now - lastActiveTime
        val timeOfflineMinutes = timeOfflineMs / (1000 * 60)

        logDebug("Checking offline simulation: offline=${timeOfflineMinutes}min")

        // Only run simulation if offline for more than 1 minute (for debugging)
        if (timeOfflineMinutes < 1) {
            logDebug("Offline time too short ($timeOfflineMinutes min), skipping simulation")
            return
        }

        logDebug("Offline for $timeOfflineMinutes minutes, preparing simulation")

        // Get all characters for this user
        val characters = characterDao.getAllCharacters()
        if (characters.isEmpty()) {
            logDebug("No characters found, skipping simulation")
            return
        }

        logDebug("Running simulation for ${characters.size} character(s)")

        // Run simulations for all characters
        retryWithBackoff(
            maxRetries = MAX_RETRIES,
            initialDelayMs = INITIAL_RETRY_DELAY_MS,
            operation = {
                runOfflineSimulationForAllCharacters(characters.map { it.id })
            }
        )
    }

    /**
     * Run offline simulation for all characters
     */
    private suspend fun runOfflineSimulationForAllCharacters(characterIds: List<String>) {
        _simulationState.value = OfflineSimulationState.Loading

        val responses = mutableListOf<OfflineSimulationResponse>()
        var hasError = false
        var errorMessage = ""

        for (characterId in characterIds) {
            val result = supabaseApi.runOfflineSimulation(characterId)

            result.fold(
                onSuccess = { response ->
                    responses.add(response)
                    // Update local character with new state
                    updateCharacterFromSimulation(characterId, response)
                },
                onFailure = { error ->
                    logError("Offline simulation failed for character $characterId", error)
                    hasError = true
                    errorMessage = error.message ?: "Unknown error"
                    // Continue with other characters even if one fails
                }
            )
        }

        if (responses.isNotEmpty()) {
            logDebug("Offline simulation completed for ${responses.size} character(s)")
            _simulationState.value = OfflineSimulationState.Success(responses)
        } else if (hasError) {
            _simulationState.value = OfflineSimulationState.Error(errorMessage)
            throw Exception(errorMessage)
        }
    }

    /**
     * Retry an operation with exponential backoff
     * Checks network before each attempt
     */
    private suspend fun retryWithBackoff(
        maxRetries: Int,
        initialDelayMs: Long,
        operation: suspend () -> Unit
    ) {
        var attempt = 0
        var delayMs = initialDelayMs

        while (attempt <= maxRetries) {
            // Wait before retry (except first attempt)
            if (attempt > 0) {
                logDebug("Retry attempt $attempt after ${delayMs}ms delay")
                delay(delayMs)
                delayMs *= 2 // Exponential backoff: 1s, 2s, 4s
            }

            // Check network before each attempt
            if (!hasNetworkConnection()) {
                logDebug("Attempt $attempt: No network connection")
                attempt++
                continue
            }

            // Try the operation
            try {
                logDebug("Attempt ${attempt + 1}: Network available, running simulation")
                operation()
                logDebug("Operation succeeded on attempt ${attempt + 1}")
                return // Success! Exit retry loop
            } catch (e: Exception) {
                logError("Attempt ${attempt + 1} failed", e)
                attempt++

                if (attempt > maxRetries) {
                    logError("All retry attempts exhausted, giving up")
                    return
                }
            }
        }

        logDebug("No network connection after $maxRetries retries, giving up")
    }


    /**
     * Update local character database with simulation results
     * Also saves activities and syncs everything to Supabase
     */
    private suspend fun updateCharacterFromSimulation(
        characterId: String,
        response: OfflineSimulationResponse
    ) {
        try {
            // 1. Save activities to database
            if (response.activities.isNotEmpty()) {
                val activities = response.activities.map { dto ->
                    convertActivityDtoToActivity(characterId, dto)
                }
                activityRepository.logActivities(activities).onFailure { error ->
                    logError("Failed to save activities", error)
                }
                logDebug("Saved ${activities.size} activities")
            }

            // 2. Reload mission progress from database (server already saved it)
            response.missionProgress?.let { missionProgress ->
                logDebug("Mission progress: ${missionProgress.principalMissionSteps} steps, " +
                        "${missionProgress.secondaryMissionsDiscovered} secondary missions, " +
                        "${missionProgress.loreDiscovered} lore discovered")

                // The offline simulation edge function already saved mission progress to Supabase.
                // The mission progress is tracked in the principal_mission_progress table
                // and will be automatically refreshed when the user views the character.
                // No additional action needed here - just logging for visibility.
            }

            // 3. Get the full character and update with simulation results
            val character = characterRepository.getCharacterById(characterId)
            if (character != null) {
                val updatedCharacter = character.copy(
                    level = response.characterState.level,
                    experience = response.characterState.experience,
                    gold = response.characterState.gold,
                    currentHp = response.characterState.currentHp,
                    maxHp = response.characterState.maxHp,
                    // Update stats that were changed during level-ups
                    strength = response.characterState.strength,
                    intelligence = response.characterState.intelligence,
                    agility = response.characterState.agility,
                    luck = response.characterState.luck,
                    charisma = response.characterState.charisma,
                    vitality = response.characterState.vitality,
                    lastActiveAt = Instant.now()
                )

                // 4. Sync updated character to both local DB and Supabase
                characterRepository.updateCharacter(updatedCharacter).onFailure { error ->
                    logError("Failed to sync character to Supabase", error)
                    // Still update locally via DAO as fallback
                    try {
                        val entity = characterDao.getCharacterById(characterId)
                        if (entity != null) {
                            characterDao.updateCharacter(entity.copy(
                                level = response.characterState.level,
                                experience = response.characterState.experience,
                                gold = response.characterState.gold,
                                currentHp = response.characterState.currentHp,
                                maxHp = response.characterState.maxHp,
                                strength = response.characterState.strength,
                                intelligence = response.characterState.intelligence,
                                agility = response.characterState.agility,
                                luck = response.characterState.luck,
                                charisma = response.characterState.charisma,
                                vitality = response.characterState.vitality
                            ))
                        }
                    } catch (e: Exception) {
                        logError("Failed to update character locally", e)
                    }
                }

                logDebug("Updated and synced character with simulation results")
            } else {
                logError("Character not found: $characterId")
            }
        } catch (e: Exception) {
            logError("Failed to update character from simulation", e)
        }
    }

    /**
     * Convert ActivityDto from server to Activity domain model
     */
    private fun convertActivityDtoToActivity(characterId: String, dto: ActivityDto): Activity {
        return Activity(
            characterId = characterId,
            timestamp = Instant.parse(dto.timestamp),
            type = mapActivityType(dto.activityType),
            description = dto.description,
            isMajorEvent = dto.isMajorEvent,
            rewards = dto.rewards?.let { rewards ->
                ActivityRewards(
                    experience = rewards.xp ?: 0,
                    gold = rewards.gold ?: 0,
                    items = rewards.items?.mapNotNull { it["name"] } ?: emptyList()
                )
            },
            metadata = dto.metadata ?: emptyMap()
        )
    }

    /**
     * Map activity type string from server to ActivityType enum
     */
    private fun mapActivityType(activityType: String): ActivityType {
        return when (activityType.uppercase()) {
            "COMBAT" -> ActivityType.COMBAT
            "EXPLORATION" -> ActivityType.EXPLORATION
            "QUEST" -> ActivityType.QUEST
            "SOCIAL" -> ActivityType.SOCIAL
            "REST" -> ActivityType.REST
            "SHOPPING" -> ActivityType.SHOPPING
            "LEVEL_UP" -> ActivityType.LEVEL_UP
            "DEATH" -> ActivityType.DEATH
            "LOOT" -> ActivityType.LOOT
            "TRAVEL" -> ActivityType.TRAVEL
            else -> ActivityType.IDLE
        }
    }

    /**
     * Dismiss the simulation summary
     */
    fun dismissSimulationSummary() {
        _simulationState.value = OfflineSimulationState.Idle
    }
}

/**
 * State of offline simulation
 */
sealed class OfflineSimulationState {
    data object Idle : OfflineSimulationState()
    data object Loading : OfflineSimulationState()
    data class Success(val responses: List<OfflineSimulationResponse>) : OfflineSimulationState()
    data class Error(val message: String) : OfflineSimulationState()
}

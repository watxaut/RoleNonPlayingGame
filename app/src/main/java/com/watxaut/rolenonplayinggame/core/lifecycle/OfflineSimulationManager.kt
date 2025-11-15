package com.watxaut.rolenonplayinggame.core.lifecycle

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.watxaut.rolenonplayinggame.BuildConfig
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.remote.api.SupabaseApi
import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationResponse
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val authRepository: AuthRepository
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
     * Check if offline simulation is needed and run it
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

        // Only run simulation if offline for more than 5 minutes
        if (timeOfflineMinutes < 5) {
            logDebug("Offline time too short ($timeOfflineMinutes min), skipping simulation")
            return
        }

        logDebug("Offline for $timeOfflineMinutes minutes, running simulation")

        // Get active character (for now, just get the first character)
        // TODO: Support multiple characters and select the active one
        val characters = characterDao.getAllCharacters()
        if (characters.isEmpty()) {
            logDebug("No characters found, skipping simulation")
            return
        }

        val character = characters.first()
        runOfflineSimulation(character.id)
    }

    /**
     * Run offline simulation for a specific character
     */
    suspend fun runOfflineSimulation(characterId: String) {
        _simulationState.value = OfflineSimulationState.Loading

        try {
            // Ensure user is authenticated before calling the simulation
            if (!authRepository.isAuthenticated()) {
                logError("User not authenticated for offline simulation")
                _simulationState.value = OfflineSimulationState.Error(
                    "You must be logged in to use offline simulation"
                )
                return
            }

            val result = supabaseApi.runOfflineSimulation(characterId)

            result.fold(
                onSuccess = { response ->
                    logDebug("Offline simulation completed successfully")
                    _simulationState.value = OfflineSimulationState.Success(response)

                    // Update local character with new state
                    updateCharacterFromSimulation(characterId, response)
                },
                onFailure = { error ->
                    logError("Offline simulation failed", error)
                    _simulationState.value = OfflineSimulationState.Error(
                        error.message ?: "Unknown error"
                    )
                }
            )
        } catch (e: Exception) {
            logError("Exception during offline simulation", e)
            _simulationState.value = OfflineSimulationState.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Update local character database with simulation results
     */
    private suspend fun updateCharacterFromSimulation(
        characterId: String,
        response: OfflineSimulationResponse
    ) {
        try {
            val character = characterDao.getCharacterById(characterId)
            if (character != null) {
                val updatedCharacter = character.copy(
                    level = response.characterState.level,
                    experience = response.characterState.experience,
                    gold = response.characterState.gold,
                    currentHp = response.characterState.currentHp,
                    maxHp = response.characterState.maxHp
                )
                characterDao.updateCharacter(updatedCharacter)
                logDebug("Updated character with simulation results")
            }
        } catch (e: Exception) {
            logError("Failed to update character from simulation", e)
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
    data class Success(val response: OfflineSimulationResponse) : OfflineSimulationState()
    data class Error(val message: String) : OfflineSimulationState()
}

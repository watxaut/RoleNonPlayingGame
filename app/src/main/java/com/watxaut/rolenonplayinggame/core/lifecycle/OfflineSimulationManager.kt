package com.watxaut.rolenonplayinggame.core.lifecycle

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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

    /**
     * Record when the app goes to background
     */
    fun recordAppBackgrounded() {
        val timestamp = System.currentTimeMillis()
        prefs.edit().putLong(PREF_LAST_ACTIVE, timestamp).apply()
        Log.d(TAG, "App backgrounded at $timestamp")
    }

    /**
     * Check if offline simulation is needed and run it
     */
    suspend fun checkAndRunOfflineSimulation() {
        val lastActiveTime = prefs.getLong(PREF_LAST_ACTIVE, 0L)

        if (lastActiveTime == 0L) {
            Log.d(TAG, "No last active time recorded, skipping simulation")
            return
        }

        val now = System.currentTimeMillis()
        val timeOfflineMs = now - lastActiveTime
        val timeOfflineMinutes = timeOfflineMs / (1000 * 60)

        // Only run simulation if offline for more than 5 minutes
        if (timeOfflineMinutes < 5) {
            Log.d(TAG, "Offline time too short ($timeOfflineMinutes min), skipping simulation")
            return
        }

        Log.d(TAG, "Offline for $timeOfflineMinutes minutes, running simulation")

        // Get active character (for now, just get the first character)
        // TODO: Support multiple characters and select the active one
        val characters = characterDao.getAllCharacters()
        if (characters.isEmpty()) {
            Log.d(TAG, "No characters found, skipping simulation")
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
                Log.e(TAG, "User not authenticated for offline simulation")
                _simulationState.value = OfflineSimulationState.Error(
                    "You must be logged in to use offline simulation"
                )
                return
            }

            val result = supabaseApi.runOfflineSimulation(characterId)

            result.fold(
                onSuccess = { response ->
                    Log.d(TAG, "Offline simulation completed: ${response.message}")
                    _simulationState.value = OfflineSimulationState.Success(response)

                    // Update local character with new state
                    updateCharacterFromSimulation(characterId, response)
                },
                onFailure = { error ->
                    Log.e(TAG, "Offline simulation failed", error)
                    _simulationState.value = OfflineSimulationState.Error(
                        error.message ?: "Unknown error"
                    )
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during offline simulation", e)
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
                Log.d(TAG, "Updated character $characterId with simulation results")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update character from simulation", e)
        }
    }

    /**
     * Dismiss the simulation summary
     */
    fun dismissSimulationSummary() {
        _simulationState.value = OfflineSimulationState.Idle
    }

    companion object {
        private const val TAG = "OfflineSimulationManager"
        private const val PREF_LAST_ACTIVE = "last_active_timestamp"
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

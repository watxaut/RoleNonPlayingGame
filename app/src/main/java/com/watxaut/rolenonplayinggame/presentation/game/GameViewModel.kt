package com.watxaut.rolenonplayinggame.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.core.ai.BasicDecisionEngine
import com.watxaut.rolenonplayinggame.core.ai.DecisionContext
import com.watxaut.rolenonplayinggame.data.repository.PrincipalMissionsRepository
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
import com.watxaut.rolenonplayinggame.domain.usecase.ExecuteDecisionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * ViewModel for the Game Screen.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md Week 3:
 * - Manages AI decision loop (3-10s timer)
 * - Executes decisions through use case
 * - Updates character state
 * - Provides activity log to UI
 * - Handles real-time gameplay
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val activityRepository: ActivityRepository,
    private val missionProgressRepository: MissionProgressRepository,
    private val executeDecisionUseCase: ExecuteDecisionUseCase,
    private val decisionEngine: BasicDecisionEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var decisionLoopJob: Job? = null
    private var characterObserverJob: Job? = null
    private var activityObserverJob: Job? = null
    private var characterId: String? = null

    /**
     * Load character and start the autonomous AI loop.
     */
    fun loadCharacter(characterId: String) {
        this.characterId = characterId

        // Cancel any existing observers
        characterObserverJob?.cancel()
        activityObserverJob?.cancel()

        characterObserverJob = viewModelScope.launch(Dispatchers.IO) {
            // Load character from repository
            var isFirstLoad = true
            characterRepository.getCharacterByIdFlow(characterId)
                .flowOn(Dispatchers.IO)
                .collect { character ->
                    // StateFlow updates are thread-safe, no need for withContext(Dispatchers.Main)
                    if (character != null) {
                        _uiState.update { it.copy(character = character, isLoading = false) }

                        // ONLY start decision loop on first load, not on every character update
                        // The decision loop will handle its own character updates
                        if (isFirstLoad) {
                            isFirstLoad = false
                            if (decisionLoopJob == null || decisionLoopJob?.isActive == false) {
                                startDecisionLoop(character)
                            }
                            // Load mission progress on first load
                            loadMissionProgress(character)
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                error = "Character not found",
                                isLoading = false
                            )
                        }
                    }
                }
        }

        // Load activity log
        activityObserverJob = viewModelScope.launch(Dispatchers.IO) {
            activityRepository.getActivitiesForCharacter(characterId, limit = 50)
                .flowOn(Dispatchers.IO)
                .collect { activities ->
                    // StateFlow updates are thread-safe, no need for withContext(Dispatchers.Main)
                    _uiState.update { it.copy(activityLog = activities) }
                }
        }
    }

    /**
     * Start the autonomous AI decision loop.
     * Character makes decisions every 3-10 seconds.
     */
    private fun startDecisionLoop(initialCharacter: Character) {
        decisionLoopJob?.cancel() // Cancel any existing loop

        decisionLoopJob = viewModelScope.launch {
            var character = initialCharacter

            while (true) {
                try {
                    // Random delay between 2-8 seconds (faster progression)
                    val delayMs = Random.nextLong(2000, 8000)
                    delay(delayMs)

                    // Build decision context
                    val context = buildDecisionContext(character)

                    // AI makes a decision
                    val decision = decisionEngine.makeDecision(character, context)

                    // Update UI to show decision is being executed
                    _uiState.update {
                        it.copy(currentAction = "Executing: ${decision.javaClass.simpleName}...")
                    }

                    // Execute the decision
                    val result = executeDecisionUseCase(character, decision)

                    result.onSuccess { outcome ->
                        // Update character state
                        character = outcome.updatedCharacter
                        _uiState.update {
                            it.copy(
                                character = character,
                                currentAction = outcome.activity.description
                            )
                        }

                        // Check for level up and update local character variable
                        if (character.shouldLevelUp()) {
                            character = levelUpCharacter(character)
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                error = "Error executing decision: ${error.message}",
                                currentAction = "Idle"
                            )
                        }
                    }

                } catch (e: kotlinx.coroutines.CancellationException) {
                    // Job was cancelled - exit the loop cleanly
                    throw e  // Re-throw to properly cancel the coroutine
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = "AI error: ${e.message}",
                            currentAction = "Idle"
                        )
                    }
                }
            }
        }
    }

    /**
     * Build decision context for the AI.
     */
    private fun buildDecisionContext(character: Character): DecisionContext {
        // For Week 3, use simple context
        // Future weeks will expand this with real location data, quests, etc.

        val nearbyLocations = when (character.currentLocation) {
            "heartlands_havenmoor" -> listOf("heartlands_meadowbrook_fields", "heartlands_millers_rest")
            "heartlands_meadowbrook_fields" -> listOf("heartlands_havenmoor", "heartlands_whispering_grove")
            "heartlands_whispering_grove" -> listOf("heartlands_meadowbrook_fields", "heartlands_broken_bridge")
            "heartlands_millers_rest" -> listOf("heartlands_havenmoor")
            "heartlands_broken_bridge" -> listOf("heartlands_whispering_grove")
            else -> listOf("heartlands_havenmoor") // Default: return to starting town
        }

        val hasInn = character.currentLocation == "heartlands_havenmoor" // Only Havenmoor (the main town) has an inn

        return DecisionContext(
            currentLocation = character.currentLocation,
            currentHp = character.currentHp,
            maxHp = character.maxHp,
            gold = character.gold,
            level = character.level,
            nearbyLocations = nearbyLocations,
            hasInnAccess = hasInn,
            hasActiveQuest = false // TODO: Implement quest system in future weeks
        )
    }

    /**
     * Handle character level up.
     * Returns the leveled-up character to update the decision loop's local variable.
     */
    private suspend fun levelUpCharacter(character: Character): Character {
        // Auto-allocate stat points based on job class
        val statIncrease = character.jobClass.statPriorities

        val leveledUpCharacter = character.copy(
            level = character.level + 1,
            experience = 0, // Reset XP
            strength = character.strength + if (statIncrease.contains("STR")) 2 else 1,
            intelligence = character.intelligence + if (statIncrease.contains("INT")) 2 else 1,
            agility = character.agility + if (statIncrease.contains("AGI")) 2 else 1,
            luck = character.luck + if (statIncrease.contains("LUK")) 2 else 1,
            charisma = character.charisma + if (statIncrease.contains("CHA")) 2 else 1,
            vitality = character.vitality + if (statIncrease.contains("VIT")) 2 else 1
        )

        // Calculate new max HP based on new vitality and level
        val newMaxHp = leveledUpCharacter.calculateMaxHp()
        val fullHealCharacter = leveledUpCharacter.copy(
            maxHp = newMaxHp,
            currentHp = newMaxHp // Full heal on level up
        )

        characterRepository.updateCharacter(fullHealCharacter)

        _uiState.update {
            it.copy(
                character = fullHealCharacter,
                currentAction = "LEVEL UP! Reached level ${fullHealCharacter.level}!",
                showLevelUpAnimation = true
            )
        }

        // Launch animation dismissal in background (don't wait for it)
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(showLevelUpAnimation = false) }
        }

        // Return the leveled-up character immediately
        return fullHealCharacter
    }

    /**
     * Pause the AI decision loop and all observers.
     * Called when navigating away from the GameScreen.
     */
    fun pauseAi() {
        // Cancel all jobs immediately
        decisionLoopJob?.cancel()
        characterObserverJob?.cancel()
        activityObserverJob?.cancel()

        // Update UI state
        _uiState.update { it.copy(currentAction = "AI Paused") }
    }

    /**
     * Resume the AI decision loop.
     */
    fun resumeAi() {
        val character = _uiState.value.character
        if (character != null) {
            startDecisionLoop(character)
        }
    }

    /**
     * Load and format principal mission progress for display
     */
    private fun loadMissionProgress(character: Character) {
        viewModelScope.launch(Dispatchers.IO) {
            character.activePrincipalMissionId?.let { missionId ->
                // Get mission definition
                val mission = PrincipalMissionsRepository.getMissionById(missionId)

                if (mission != null) {
                    // Get progress from Supabase
                    val progressResult = missionProgressRepository.getActivePrincipalMission(character.id)

                    progressResult.onSuccess { progress ->
                        val missionText = if (progress != null) {
                            val stepsComplete = progress.completedSteps.size
                            val totalSteps = mission.steps.size
                            val progressPercentage = ((stepsComplete + if (progress.bossDefeated) 1 else 0).toFloat() /
                                                      (totalSteps + 1) * 100).toInt()

                            buildString {
                                appendLine("📜 ${mission.name}")
                                appendLine()
                                appendLine(mission.description)
                                appendLine()
                                appendLine("Progress: $stepsComplete / $totalSteps steps ($progressPercentage%)")

                                if (progress.bossDefeated) {
                                    appendLine("✅ Boss Defeated!")
                                } else if (progress.bossEncountered) {
                                    appendLine("⚔️ Boss Encountered - Not Yet Defeated")
                                } else if (stepsComplete == totalSteps) {
                                    appendLine("🎯 All steps complete! Boss battle imminent...")
                                }

                                appendLine()
                                appendLine("Region: ${mission.loreRegion.displayName}")
                            }
                        } else {
                            // Mission assigned but no progress yet
                            buildString {
                                appendLine("📜 ${mission.name}")
                                appendLine()
                                appendLine(mission.description)
                                appendLine()
                                appendLine("Progress: 0 / ${mission.steps.size} steps (0%)")
                                appendLine()
                                appendLine("Region: ${mission.loreRegion.displayName}")
                            }
                        }

                        _uiState.update { it.copy(principalMissionProgress = missionText) }
                    }
                } else {
                    // Mission ID exists but mission not found in repository
                    _uiState.update {
                        it.copy(principalMissionProgress = "Mission ID: $missionId (details not available)")
                    }
                }
            } ?: run {
                // No mission assigned
                _uiState.update {
                    it.copy(principalMissionProgress = null)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel all running jobs when ViewModel is cleared
        decisionLoopJob?.cancel()
        characterObserverJob?.cancel()
        activityObserverJob?.cancel()
    }
}

/**
 * UI state for the Game Screen.
 */
data class GameUiState(
    val character: Character? = null,
    val activityLog: List<Activity> = emptyList(),
    val currentAction: String = "Waiting for character...",
    val principalMissionProgress: String? = null,
    val discoveredLore: List<String> = emptyList(),
    val secondaryMissions: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showLevelUpAnimation: Boolean = false
)

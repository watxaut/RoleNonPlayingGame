package com.watxaut.rolenonplayinggame.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.core.ai.BasicDecisionEngine
import com.watxaut.rolenonplayinggame.core.ai.DecisionContext
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.usecase.ExecuteDecisionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

        characterObserverJob = viewModelScope.launch {
            // Load character from repository
            characterRepository.getCharacterByIdFlow(characterId).collect { character ->
                if (character != null) {
                    _uiState.update { it.copy(character = character, isLoading = false) }

                    // Start decision loop if not already running
                    if (decisionLoopJob == null || decisionLoopJob?.isActive == false) {
                        startDecisionLoop(character)
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
        activityObserverJob = viewModelScope.launch {
            activityRepository.getActivitiesForCharacter(characterId, limit = 50)
                .collect { activities ->
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
                    // Random delay between 10-30 seconds (slower progression)
                    val delayMs = Random.nextLong(10000, 30000)
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
                                currentAction = outcome.activity.description,
                                combatLog = if (outcome.combatLog.isNotEmpty()) {
                                    outcome.combatLog
                                } else {
                                    emptyList()
                                }
                            )
                        }

                        // Check for level up
                        if (character.experience >= character.level * 100) {
                            levelUpCharacter(character)
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                error = "Error executing decision: ${error.message}",
                                currentAction = "Idle"
                            )
                        }
                    }

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
            "Willowdale Village" -> listOf("Meadowbrook Fields")
            "Meadowbrook Fields" -> listOf("Willowdale Village", "Whispering Woods")
            else -> listOf("Willowdale Village")
        }

        val hasInn = character.currentLocation == "Willowdale Village"

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
     */
    private fun levelUpCharacter(character: Character) {
        viewModelScope.launch {
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

            // Reset level up animation after delay
            delay(3000)
            _uiState.update { it.copy(showLevelUpAnimation = false) }
        }
    }

    /**
     * Pause the AI decision loop and all observers.
     * Called when navigating away from the GameScreen.
     */
    fun pauseAi() {
        decisionLoopJob?.cancel()
        characterObserverJob?.cancel()
        activityObserverJob?.cancel()
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
    val combatLog: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showLevelUpAnimation: Boolean = false
)

package com.watxaut.rolenonplayinggame.presentation.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.domain.model.CharacterStats
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.StatType
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.domain.usecase.CreateCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for Character Creation screen.
 * Manages state for name input, job selection, and stat allocation.
 */
@HiltViewModel
class CharacterCreationViewModel @Inject constructor(
    private val createCharacterUseCase: CreateCharacterUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterCreationUiState())
    val uiState: StateFlow<CharacterCreationUiState> = _uiState.asStateFlow()

    /**
     * Update the character name
     */
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    /**
     * Select a job class
     */
    fun selectJobClass(jobClass: JobClass) {
        _uiState.update {
            it.copy(
                selectedJobClass = jobClass,
                // Show recommended stats for the selected job class
                showJobClassInfo = true
            )
        }
    }

    /**
     * Increment a stat (if points available)
     */
    fun incrementStat(statType: StatType) {
        val currentStats = _uiState.value.stats
        val remainingPoints = _uiState.value.getRemainingPoints()

        if (remainingPoints > 0) {
            val updatedStats = when (statType) {
                StatType.STRENGTH -> currentStats.copy(strength = currentStats.strength + 1)
                StatType.INTELLIGENCE -> currentStats.copy(intelligence = currentStats.intelligence + 1)
                StatType.AGILITY -> currentStats.copy(agility = currentStats.agility + 1)
                StatType.LUCK -> currentStats.copy(luck = currentStats.luck + 1)
                StatType.CHARISMA -> currentStats.copy(charisma = currentStats.charisma + 1)
                StatType.VITALITY -> currentStats.copy(vitality = currentStats.vitality + 1)
            }

            _uiState.update { it.copy(stats = updatedStats, statsError = null) }
        }
    }

    /**
     * Decrement a stat (minimum 1 per stat)
     */
    fun decrementStat(statType: StatType) {
        val currentStats = _uiState.value.stats
        val currentValue = when (statType) {
            StatType.STRENGTH -> currentStats.strength
            StatType.INTELLIGENCE -> currentStats.intelligence
            StatType.AGILITY -> currentStats.agility
            StatType.LUCK -> currentStats.luck
            StatType.CHARISMA -> currentStats.charisma
            StatType.VITALITY -> currentStats.vitality
        }

        if (currentValue > 1) {
            val updatedStats = when (statType) {
                StatType.STRENGTH -> currentStats.copy(strength = currentStats.strength - 1)
                StatType.INTELLIGENCE -> currentStats.copy(intelligence = currentStats.intelligence - 1)
                StatType.AGILITY -> currentStats.copy(agility = currentStats.agility - 1)
                StatType.LUCK -> currentStats.copy(luck = currentStats.luck - 1)
                StatType.CHARISMA -> currentStats.copy(charisma = currentStats.charisma - 1)
                StatType.VITALITY -> currentStats.copy(vitality = currentStats.vitality - 1)
            }

            _uiState.update { it.copy(stats = updatedStats) }
        }
    }

    /**
     * Reset stats to default (1 point in each stat, 4 remaining)
     */
    fun resetStats() {
        _uiState.update {
            it.copy(
                stats = CharacterStats.default(),
                statsError = null
            )
        }
    }

    /**
     * Apply recommended stats for the selected job class
     */
    fun applyRecommendedStats() {
        val jobClass = _uiState.value.selectedJobClass ?: return
        val weights = jobClass.getStatAllocationWeights()

        // Start with 1 in each stat (6 points)
        var stats = CharacterStats(1, 1, 1, 1, 1, 1)
        var remainingPoints = 4 // 10 total - 6 base = 4 to allocate

        // Allocate 2 points to primary stat
        stats = when (jobClass.primaryStat) {
            StatType.STRENGTH -> stats.copy(strength = stats.strength + 2)
            StatType.INTELLIGENCE -> stats.copy(intelligence = stats.intelligence + 2)
            StatType.AGILITY -> stats.copy(agility = stats.agility + 2)
            StatType.LUCK -> stats.copy(luck = stats.luck + 2)
            StatType.CHARISMA -> stats.copy(charisma = stats.charisma + 2)
            StatType.VITALITY -> stats.copy(vitality = stats.vitality + 2)
        }
        remainingPoints -= 2

        // Allocate remaining 2 points to secondary stat
        stats = when (jobClass.secondaryStat) {
            StatType.STRENGTH -> stats.copy(strength = stats.strength + 2)
            StatType.INTELLIGENCE -> stats.copy(intelligence = stats.intelligence + 2)
            StatType.AGILITY -> stats.copy(agility = stats.agility + 2)
            StatType.LUCK -> stats.copy(luck = stats.luck + 2)
            StatType.CHARISMA -> stats.copy(charisma = stats.charisma + 2)
            StatType.VITALITY -> stats.copy(vitality = stats.vitality + 2)
        }

        _uiState.update { it.copy(stats = stats, statsError = null) }
    }

    /**
     * Validate and create the character
     */
    fun createCharacter() {
        // Validate name
        val nameValidation = createCharacterUseCase.validateName(_uiState.value.name)
        if (nameValidation.isFailure) {
            _uiState.update {
                it.copy(nameError = nameValidation.exceptionOrNull()?.message)
            }
            return
        }

        // Validate job class selection
        val jobClass = _uiState.value.selectedJobClass
        if (jobClass == null) {
            _uiState.update {
                it.copy(generalError = "Please select a job class")
            }
            return
        }

        // Validate stats
        val statsValidation = createCharacterUseCase.validateStats(_uiState.value.stats)
        if (statsValidation.isFailure) {
            _uiState.update {
                it.copy(statsError = statsValidation.exceptionOrNull()?.message)
            }
            return
        }

        // All validation passed - create character
        _uiState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch(Dispatchers.IO) {
            // Get authenticated user ID
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = "You must be logged in to create a character"
                        )
                    }
                }
                return@launch
            }

            // Create character with authenticated user ID
            val result = createCharacterUseCase(
                userId = userId,
                name = _uiState.value.name,
                jobClass = jobClass,
                stats = _uiState.value.stats
            )

            withContext(Dispatchers.Main) {
                result.onSuccess { character ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            createdCharacterId = character.id,
                            creationComplete = true
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = error.message ?: "Failed to create character"
                        )
                    }
                }
            }
        }
    }

    /**
     * Dismiss job class info panel
     */
    fun dismissJobClassInfo() {
        _uiState.update { it.copy(showJobClassInfo = false) }
    }
}

/**
 * UI State for Character Creation
 */
data class CharacterCreationUiState(
    val name: String = "",
    val selectedJobClass: JobClass? = null,
    val stats: CharacterStats = CharacterStats.default(),
    val showJobClassInfo: Boolean = false,

    // Validation errors
    val nameError: String? = null,
    val statsError: String? = null,
    val generalError: String? = null,

    // Loading and completion states
    val isLoading: Boolean = false,
    val creationComplete: Boolean = false,
    val createdCharacterId: String? = null
) {
    /**
     * Calculate remaining stat points (should be 0 when ready to create)
     */
    fun getRemainingPoints(): Int {
        return 10 - stats.getTotalPoints()
    }

    /**
     * Check if all inputs are valid and ready to create character
     */
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                selectedJobClass != null &&
                getRemainingPoints() == 0 &&
                nameError == null &&
                statsError == null
    }

    /**
     * Get primary stat for selected job class
     */
    fun getPrimaryStat(): StatType? = selectedJobClass?.primaryStat

    /**
     * Get secondary stat for selected job class
     */
    fun getSecondaryStat(): StatType? = selectedJobClass?.secondaryStat
}

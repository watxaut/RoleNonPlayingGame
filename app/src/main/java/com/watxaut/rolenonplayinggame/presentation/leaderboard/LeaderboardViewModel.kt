package com.watxaut.rolenonplayinggame.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Leaderboard screen
 * Displays all characters ranked by experience
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    /**
     * Load all characters for the leaderboard
     */
    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading

            characterRepository.getAllCharacters()
                .onSuccess { characters ->
                    // Sort by experience descending
                    val sortedCharacters = characters.sortedByDescending { it.experience }
                    _uiState.value = LeaderboardUiState.Success(sortedCharacters)
                }
                .onFailure { error ->
                    _uiState.value = LeaderboardUiState.Error(
                        error.message ?: "Failed to load leaderboard"
                    )
                }
        }
    }

    /**
     * Retry loading the leaderboard
     */
    fun retry() {
        loadLeaderboard()
    }
}

/**
 * UI state for the leaderboard screen
 */
sealed class LeaderboardUiState {
    data object Loading : LeaderboardUiState()
    data class Success(val characters: List<Character>) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}

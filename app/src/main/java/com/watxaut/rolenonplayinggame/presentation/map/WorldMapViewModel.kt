package com.watxaut.rolenonplayinggame.presentation.map

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
 * ViewModel for the world map screen
 */
@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldMapUiState())
    val uiState: StateFlow<WorldMapUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            characterRepository.getAllCharacters().collect { characters ->
                _uiState.value = _uiState.value.copy(
                    characters = characters,
                    isLoading = false
                )
            }
        }
    }
}

/**
 * UI state for the world map
 */
data class WorldMapUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = true
)

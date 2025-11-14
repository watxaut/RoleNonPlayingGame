package com.watxaut.rolenonplayinggame.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the profile screen
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            characterRepository.getAllCharacters().collect { characters ->
                _uiState.value = ProfileUiState(
                    totalCharacters = characters.size,
                    activeCharacters = characters.count { it.currentHp > 0 },
                    totalLevel = characters.sumOf { it.level },
                    highestLevel = characters.maxOfOrNull { it.level } ?: 0,
                    totalGold = characters.sumOf { it.gold }
                )
            }
        }
    }
}

/**
 * UI state for the profile screen
 */
data class ProfileUiState(
    val totalCharacters: Int = 0,
    val activeCharacters: Int = 0,
    val totalLevel: Int = 0,
    val highestLevel: Int = 0,
    val totalGold: Int = 0
)

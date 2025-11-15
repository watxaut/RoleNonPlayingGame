package com.watxaut.rolenonplayinggame.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the world map screen
 */
@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Characters for the current user, combined into UI state
     * Automatically updates when characters change
     */
    val uiState: StateFlow<WorldMapUiState> = flow {
        // Get current authenticated user on IO dispatcher to avoid blocking main thread
        val userId = authRepository.getCurrentUserId()
        emit(userId)
    }
        .flowOn(Dispatchers.IO)  // Run auth check on IO thread
        .flatMapLatest { userId ->
            if (userId != null) {
                characterRepository.getCharactersByUserId(userId)
            } else {
                emptyFlow()
            }
        }
        .flowOn(Dispatchers.IO)  // Run database queries on IO thread
        .map { characters ->
            WorldMapUiState(
                characters = characters,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorldMapUiState(isLoading = true)
        )
}

/**
 * UI state for the world map
 */
data class WorldMapUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = true
)

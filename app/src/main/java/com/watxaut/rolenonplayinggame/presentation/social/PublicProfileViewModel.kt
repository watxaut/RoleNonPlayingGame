package com.watxaut.rolenonplayinggame.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.domain.model.EncounterHistoryEntry
import com.watxaut.rolenonplayinggame.domain.model.PublicCharacterProfile
import com.watxaut.rolenonplayinggame.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for viewing public character profiles
 */
@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    /**
     * Load a character's public profile
     */
    fun loadProfile(characterId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val profileResult = socialRepository.getPublicProfile(characterId)

            profileResult.fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load profile: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    /**
     * Load top characters for discovery
     */
    fun loadTopCharacters(limit: Int = 20) {
        _uiState.update { it.copy(isLoadingTopCharacters = true) }

        viewModelScope.launch {
            val result = socialRepository.getTopCharacters(limit)

            result.fold(
                onSuccess = { characters ->
                    _uiState.update {
                        it.copy(
                            topCharacters = characters,
                            isLoadingTopCharacters = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load top characters: ${error.message}",
                            isLoadingTopCharacters = false
                        )
                    }
                }
            )
        }
    }
}

/**
 * UI state for public profile screen
 */
data class PublicProfileUiState(
    val profile: PublicCharacterProfile? = null,
    val topCharacters: List<PublicCharacterProfile> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingTopCharacters: Boolean = false,
    val error: String? = null
)

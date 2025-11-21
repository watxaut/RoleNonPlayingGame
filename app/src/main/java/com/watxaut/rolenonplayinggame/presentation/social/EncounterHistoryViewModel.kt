package com.watxaut.rolenonplayinggame.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.domain.model.EncounterHistoryEntry
import com.watxaut.rolenonplayinggame.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for viewing encounter history
 */
@HiltViewModel
class EncounterHistoryViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EncounterHistoryUiState())
    val uiState: StateFlow<EncounterHistoryUiState> = _uiState.asStateFlow()

    /**
     * Load encounter history for a character
     */
    fun loadEncounterHistory(characterId: String, limit: Int = 50) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = socialRepository.getEncounterHistory(characterId, limit)

            result.fold(
                onSuccess = { history ->
                    _uiState.update {
                        it.copy(
                            encounters = history,
                            filteredEncounters = history,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load encounters: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    /**
     * Filter encounters by type
     */
    fun filterByType(filterType: String?) {
        val allEncounters = _uiState.value.encounters

        val filtered = if (filterType == null) {
            allEncounters
        } else {
            allEncounters.filter { it.encounterType.name == filterType }
        }

        _uiState.update {
            it.copy(
                filteredEncounters = filtered,
                currentFilter = filterType
            )
        }
    }
}

/**
 * UI state for encounter history screen
 */
data class EncounterHistoryUiState(
    val encounters: List<EncounterHistoryEntry> = emptyList(),
    val filteredEncounters: List<EncounterHistoryEntry> = emptyList(),
    val currentFilter: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

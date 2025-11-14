package com.watxaut.rolenonplayinggame.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.core.lifecycle.OfflineSimulationManager
import com.watxaut.rolenonplayinggame.core.lifecycle.OfflineSimulationState
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val authRepository: AuthRepository,
    private val offlineSimulationManager: OfflineSimulationManager
) : ViewModel() {

    /**
     * Offline simulation state from the manager
     */
    val simulationState: StateFlow<OfflineSimulationState> =
        offlineSimulationManager.simulationState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OfflineSimulationState.Idle
        )

    /**
     * Characters for the current user
     * Automatically updates when characters change
     */
    val characters: StateFlow<List<Character>> = flow {
        // Ensure user is authenticated
        authRepository.signInAnonymously()
        val userId = authRepository.getCurrentUserId()
        emit(userId)
    }.flatMapLatest { userId ->
        if (userId != null) {
            characterRepository.getCharactersByUserId(userId)
        } else {
            emptyFlow()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Initialize authentication
     * Called when the screen is first displayed
     */
    init {
        viewModelScope.launch {
            // Sign in anonymously if not already authenticated
            authRepository.signInAnonymously()
        }
    }

    /**
     * Dismiss the offline simulation summary
     */
    fun dismissSimulationSummary() {
        offlineSimulationManager.dismissSimulationSummary()
    }
}

package com.watxaut.rolenonplayinggame.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watxaut.rolenonplayinggame.core.lifecycle.OfflineSimulationManager
import com.watxaut.rolenonplayinggame.core.lifecycle.OfflineSimulationState
import com.watxaut.rolenonplayinggame.data.local.dao.CharacterDao
import com.watxaut.rolenonplayinggame.data.local.entity.CharacterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterDao: CharacterDao,
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
     * Dismiss the offline simulation summary
     */
    fun dismissSimulationSummary() {
        offlineSimulationManager.dismissSimulationSummary()
    }

    /**
     * Get all characters
     */
    suspend fun getAllCharacters(): List<CharacterEntity> {
        return characterDao.getAllCharacters()
    }
}

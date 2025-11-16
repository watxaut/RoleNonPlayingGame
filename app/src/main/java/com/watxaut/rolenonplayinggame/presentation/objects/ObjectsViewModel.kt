package com.watxaut.rolenonplayinggame.presentation.objects

import androidx.lifecycle.ViewModel
import com.watxaut.rolenonplayinggame.data.ItemDatabase
import com.watxaut.rolenonplayinggame.domain.model.Item
import com.watxaut.rolenonplayinggame.domain.model.ItemRarity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Objects screen
 * Displays all game items with their rarity and drop chance
 */
@HiltViewModel
class ObjectsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ObjectsUiState>(ObjectsUiState.Loading)
    val uiState: StateFlow<ObjectsUiState> = _uiState.asStateFlow()

    init {
        loadObjects()
    }

    /**
     * Load all objects from the item database
     */
    private fun loadObjects() {
        try {
            val allItems = ItemDatabase.getAllItems()

            // Sort by rarity (Legendary first, then Epic, Rare, etc.)
            val sortedItems = allItems.sortedWith(
                compareBy<Item> { getRarityOrder(it.rarity) }
                    .thenBy { it.name }
            )

            _uiState.value = ObjectsUiState.Success(sortedItems)
        } catch (e: Exception) {
            _uiState.value = ObjectsUiState.Error(
                e.message ?: "Failed to load objects"
            )
        }
    }

    /**
     * Get rarity order for sorting (lower number = higher rarity)
     */
    private fun getRarityOrder(rarity: ItemRarity): Int {
        return when (rarity) {
            ItemRarity.LEGENDARY -> 0
            ItemRarity.EPIC -> 1
            ItemRarity.RARE -> 2
            ItemRarity.UNCOMMON -> 3
            ItemRarity.COMMON -> 4
            ItemRarity.JUNK -> 5
        }
    }
}

/**
 * UI state for the objects screen
 */
sealed class ObjectsUiState {
    data object Loading : ObjectsUiState()
    data class Success(val items: List<Item>) : ObjectsUiState()
    data class Error(val message: String) : ObjectsUiState()
}

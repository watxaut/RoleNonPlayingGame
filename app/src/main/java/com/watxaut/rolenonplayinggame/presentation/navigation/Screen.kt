package com.watxaut.rolenonplayinggame.presentation.navigation

/**
 * Sealed class representing navigation destinations
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object CharacterCreation : Screen("character_creation")
    data object Game : Screen("game/{characterId}") {
        fun createRoute(characterId: String) = "game/$characterId"
    }
    data object CharacterSheet : Screen("character_sheet/{characterId}") {
        fun createRoute(characterId: String) = "character_sheet/$characterId"
    }
}

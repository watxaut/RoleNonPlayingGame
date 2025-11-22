package com.watxaut.rolenonplayinggame.presentation.navigation

/**
 * Sealed class representing navigation destinations
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object CharacterCreation : Screen("character_creation")
    data object Game : Screen("game/{characterId}") {
        fun createRoute(characterId: String) = "game/$characterId"
    }
    data object CharacterSheet : Screen("character_sheet/{characterId}") {
        fun createRoute(characterId: String) = "character_sheet/$characterId"
    }
    data object OfflineSummary : Screen("offline_summary")
    data object PublicProfile : Screen("public_profile/{characterId}") {
        fun createRoute(characterId: String) = "public_profile/$characterId"
    }
    data object EncounterHistory : Screen("encounter_history/{characterId}") {
        fun createRoute(characterId: String) = "encounter_history/$characterId"
    }
}

package com.watxaut.rolenonplayinggame.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.watxaut.rolenonplayinggame.presentation.character.CharacterCreationScreen
import com.watxaut.rolenonplayinggame.presentation.game.GameScreen
import com.watxaut.rolenonplayinggame.presentation.home.HomeScreen

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCharacterCreation = {
                    navController.navigate(Screen.CharacterCreation.route)
                },
                onNavigateToGame = { characterId ->
                    navController.navigate(Screen.Game.createRoute(characterId))
                }
            )
        }

        composable(Screen.CharacterCreation.route) {
            CharacterCreationScreen(
                onCharacterCreated = { characterId ->
                    navController.navigate(Screen.Game.createRoute(characterId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("characterId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
            GameScreen(
                characterId = characterId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

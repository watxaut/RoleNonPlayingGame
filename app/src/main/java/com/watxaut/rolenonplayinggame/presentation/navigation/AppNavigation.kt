package com.watxaut.rolenonplayinggame.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.presentation.auth.LoginScreen
import com.watxaut.rolenonplayinggame.presentation.character.CharacterCreationScreen
import com.watxaut.rolenonplayinggame.presentation.game.GameScreen
import com.watxaut.rolenonplayinggame.presentation.main.MainScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Main navigation component for the app
 * Handles authentication and main navigation flow
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = hiltViewModel<NavigationViewModel>().authRepository
) {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) }
    var isCheckingAuth by remember { mutableStateOf(true) }

    // Check authentication status on background thread to prevent ANR
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val authenticated = authRepository.isAuthenticated() && !authRepository.isAnonymous()
            withContext(Dispatchers.Main) {
                isAuthenticated = authenticated
                isCheckingAuth = false
            }
        }
    }

    if (!isCheckingAuth) {
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Main.route else Screen.Login.route,
            modifier = modifier
        ) {
            // Login screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        isAuthenticated = true
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Main screen with bottom navigation (Heroes, Map, Profile)
            composable(Screen.Main.route) {
                MainScreen(
                    onNavigateToCharacterCreation = {
                        navController.navigate(Screen.CharacterCreation.route)
                    },
                    onNavigateToGame = { characterId ->
                        navController.navigate(Screen.Game.createRoute(characterId))
                    },
                    onSignOut = {
                        isAuthenticated = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Character creation
            composable(Screen.CharacterCreation.route) {
                CharacterCreationScreen(
                    onCharacterCreated = { characterId ->
                        navController.navigate(Screen.Game.createRoute(characterId)) {
                            popUpTo(Screen.Main.route)
                        }
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            // Game screen
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
}

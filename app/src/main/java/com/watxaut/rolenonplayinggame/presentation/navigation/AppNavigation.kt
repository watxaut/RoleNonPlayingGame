package com.watxaut.rolenonplayinggame.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.watxaut.rolenonplayinggame.presentation.social.EncounterHistoryScreen
import com.watxaut.rolenonplayinggame.presentation.social.PublicProfileScreen
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
    // Add delay to ensure Supabase has time to load session from storage
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // Small delay to allow Supabase Auth to load session from storage
            // This is necessary because autoLoadFromStorage is async
            kotlinx.coroutines.delay(100)

            val authenticated = authRepository.isAuthenticated() && !authRepository.isAnonymous()
            withContext(Dispatchers.Main) {
                isAuthenticated = authenticated
                isCheckingAuth = false
            }
        }
    }

    if (isCheckingAuth) {
        // Show loading screen while checking authentication
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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
                    },
                    onNavigateToEncounterHistory = { charId ->
                        navController.navigate(Screen.EncounterHistory.createRoute(charId))
                    }
                )
            }

            // Public Profile screen
            composable(
                route = Screen.PublicProfile.route,
                arguments = listOf(
                    navArgument("characterId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
                PublicProfileScreen(
                    characterId = characterId,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            // Encounter History screen
            composable(
                route = Screen.EncounterHistory.route,
                arguments = listOf(
                    navArgument("characterId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
                EncounterHistoryScreen(
                    characterId = characterId,
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToProfile = { profileCharacterId ->
                        navController.navigate(Screen.PublicProfile.createRoute(profileCharacterId))
                    }
                )
            }
        }
    }
}

package com.watxaut.rolenonplayinggame.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.watxaut.rolenonplayinggame.domain.repository.AuthRepository
import com.watxaut.rolenonplayinggame.presentation.auth.LoginScreen
import com.watxaut.rolenonplayinggame.presentation.auth.ProfileScreen
import com.watxaut.rolenonplayinggame.presentation.character.CharacterCreationScreen
import com.watxaut.rolenonplayinggame.presentation.components.AppNavBar
import com.watxaut.rolenonplayinggame.presentation.game.GameScreen
import com.watxaut.rolenonplayinggame.presentation.home.HomeScreen

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authRepository: AuthRepository = hiltViewModel<NavigationViewModel>().authRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isAuthenticated by remember { mutableStateOf(false) }
    var isCheckingAuth by remember { mutableStateOf(true) }

    // Check authentication status
    LaunchedEffect(Unit) {
        isAuthenticated = authRepository.isAuthenticated() && !authRepository.isAnonymous()
        isCheckingAuth = false
    }

    // Determine if navbar should be shown
    val showNavBar = isAuthenticated && currentRoute != Screen.Login.route

    Column(modifier = modifier.fillMaxSize()) {
        if (showNavBar) {
            AppNavBar(
                currentRoute = currentRoute,
                onNavigateToHeroes = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        if (!isCheckingAuth) {
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            isAuthenticated = true
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToCharacterCreation = {
                            // User is already authenticated if they're on this screen
                            navController.navigate(Screen.CharacterCreation.route)
                        },
                        onNavigateToGame = { characterId ->
                            navController.navigate(Screen.Game.createRoute(characterId))
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onSignOut = {
                            isAuthenticated = false
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
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
    }
}

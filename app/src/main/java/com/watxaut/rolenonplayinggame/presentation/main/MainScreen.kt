package com.watxaut.rolenonplayinggame.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.watxaut.rolenonplayinggame.presentation.auth.ProfileScreen
import com.watxaut.rolenonplayinggame.presentation.components.BottomNavigationBar
import com.watxaut.rolenonplayinggame.presentation.home.HomeScreen
import com.watxaut.rolenonplayinggame.presentation.leaderboard.LeaderboardScreen
import com.watxaut.rolenonplayinggame.presentation.map.WorldMapScreen
import com.watxaut.rolenonplayinggame.presentation.objects.ObjectsScreen

/**
 * Main screen with bottom navigation
 * Contains Heroes, Leaderboard, Objects, Map, and Profile tabs
 */
@Composable
fun MainScreen(
    onNavigateToCharacterCreation: () -> Unit,
    onNavigateToGame: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentRoute by remember { mutableStateOf("heroes") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                }
            )
        }
    ) { innerPadding ->
        when (currentRoute) {
            "heroes" -> HomeScreen(
                onNavigateToCharacterCreation = onNavigateToCharacterCreation,
                onNavigateToGame = onNavigateToGame,
                modifier = Modifier.padding(innerPadding)
            )
            "leaderboard" -> LeaderboardScreen(
                modifier = Modifier.padding(innerPadding)
            )
            "objects" -> ObjectsScreen(
                modifier = Modifier.padding(innerPadding)
            )
            "map" -> WorldMapScreen(
                modifier = Modifier.padding(innerPadding)
            )
            "profile" -> ProfileScreen(
                onSignOut = onSignOut,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

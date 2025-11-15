package com.watxaut.rolenonplayinggame.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation destinations
 */
sealed class BottomNavDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    data object Heroes : BottomNavDestination(
        route = "heroes",
        icon = Icons.Default.Face,
        label = "Heroes"
    )

    data object Map : BottomNavDestination(
        route = "map",
        icon = Icons.Default.Place,
        label = "Map"
    )

    data object Profile : BottomNavDestination(
        route = "profile",
        icon = Icons.Default.AccountCircle,
        label = "Profile"
    )

    companion object {
        val items = listOf(Heroes, Map, Profile)
    }
}

/**
 * Bottom navigation bar component
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        BottomNavDestination.items.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}

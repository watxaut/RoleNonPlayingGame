package com.watxaut.rolenonplayinggame.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavBar(
    currentRoute: String?,
    onNavigateToHeroes: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = when (currentRoute) {
                    "profile" -> "Profile"
                    "character_creation" -> "Create Hero"
                    "game/{characterId}" -> "Adventure"
                    else -> "Role Non-Playing Game"
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            // Heroes button
            IconButton(
                onClick = onNavigateToHeroes
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Heroes",
                    tint = if (currentRoute == "home") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Profile button
            IconButton(
                onClick = onNavigateToProfile
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (currentRoute == "profile") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}

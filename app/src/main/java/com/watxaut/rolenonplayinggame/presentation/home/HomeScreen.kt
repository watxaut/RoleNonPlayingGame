package com.watxaut.rolenonplayinggame.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.watxaut.rolenonplayinggame.core.lifecycle.OfflineSimulationState
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.presentation.map.getLocationDisplayName
import com.watxaut.rolenonplayinggame.presentation.summary.OfflineSummaryScreen

/**
 * Home screen - entry point of the app
 * Shows welcome message and allows creating/selecting characters
 */
@Composable
fun HomeScreen(
    onNavigateToCharacterCreation: () -> Unit,
    onNavigateToGame: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val simulationState by viewModel.simulationState.collectAsState()
    val characters by viewModel.characters.collectAsState()

    if (characters.isEmpty()) {
        // Show welcome message when no characters exist (centered)
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Role Non-Playing Game",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Watch your autonomous characters\nlive their own adventures",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNavigateToCharacterCreation
            ) {
                Text("Create New Character")
            }
        }
    } else {
        // Show scrollable character list
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Your Characters (${characters.size}/5)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(characters) { character ->
                CharacterListItem(
                    character = character,
                    onClick = { onNavigateToGame(character.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                val hasReachedLimit = characters.size >= 5
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onNavigateToCharacterCreation,
                        enabled = !hasReachedLimit
                    ) {
                        Text("Create Another Character")
                    }

                    if (hasReachedLimit) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Maximum 5 characters reached",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Show offline simulation loading or summary
    when (val state = simulationState) {
        is OfflineSimulationState.Loading -> {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Catching up on your character's adventures...",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        is OfflineSimulationState.Success -> {
            Dialog(
                onDismissRequest = { viewModel.dismissSimulationSummary() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                OfflineSummaryScreen(
                    simulationResponses = state.responses,
                    onDismiss = { viewModel.dismissSimulationSummary() }
                )
            }
        }

        is OfflineSimulationState.Error -> {
            // TODO: Show error message
            // For now, just log and dismiss
            viewModel.dismissSimulationSummary()
        }

        OfflineSimulationState.Idle -> {
            // No dialog to show
        }
    }
}

/**
 * Character list item card
 */
@Composable
private fun CharacterListItem(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Character name and job class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = character.jobClass.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Lv ${character.level}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // HP bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HP:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(36.dp)
                )
                LinearProgressIndicator(
                    progress = { character.getHealthPercentage() },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${character.currentHp}/${character.maxHp}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            Text(
                text = "📍 ${getLocationDisplayName(character.currentLocation)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Gold
            Text(
                text = "💰 ${character.gold} gold",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

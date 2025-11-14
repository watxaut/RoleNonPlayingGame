package com.watxaut.rolenonplayinggame.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Show list of existing characters
        Text(
            text = "Character list will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
                    simulationResponse = state.response,
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

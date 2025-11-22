package com.watxaut.rolenonplayinggame.presentation.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.watxaut.rolenonplayinggame.domain.model.EncounterHistoryEntry
import com.watxaut.rolenonplayinggame.domain.model.EncounterType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen displaying a character's encounter history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncounterHistoryScreen(
    characterId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EncounterHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.loadEncounterHistory(characterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Encounter History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadEncounterHistory(characterId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.encounters.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No encounters yet.\nYour character hasn't met anyone!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                EncounterHistoryContent(
                    encounters = uiState.filteredEncounters,
                    currentFilter = uiState.currentFilter,
                    onFilterChange = { viewModel.filterByType(it) },
                    onEncounterClick = { onNavigateToProfile(it.otherCharacterId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun EncounterHistoryContent(
    encounters: List<EncounterHistoryEntry>,
    currentFilter: String?,
    onFilterChange: (String?) -> Unit,
    onEncounterClick: (EncounterHistoryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Filter chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = currentFilter == null,
                    onClick = { onFilterChange(null) },
                    label = { Text("All") }
                )
            }

            items(EncounterType.values()) { type ->
                FilterChip(
                    selected = currentFilter == type.name,
                    onClick = { onFilterChange(type.name) },
                    label = { Text(type.getDisplayName()) }
                )
            }
        }

        Divider()

        // Encounter list
        if (encounters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No encounters of this type",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(encounters) { encounter ->
                    EncounterCard(
                        encounter = encounter,
                        onClick = { onEncounterClick(encounter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EncounterCard(
    encounter: EncounterHistoryEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = encounter.otherCharacterName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                EncounterTypeChip(encounter.encounterType)
            }

            // Character info
            Text(
                text = "Level ${encounter.otherCharacterLevel}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Location
            Text(
                text = "📍 ${formatLocation(encounter.location)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider()

            // Outcome description
            Text(
                text = encounter.outcome.description,
                style = MaterialTheme.typography.bodyMedium
            )

            // Timestamp
            Text(
                text = formatTimestamp(encounter.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EncounterTypeChip(type: EncounterType) {
    val (emoji, color) = when (type) {
        EncounterType.GREETING -> "👋" to MaterialTheme.colorScheme.primary
        EncounterType.TRADE -> "💰" to MaterialTheme.colorScheme.tertiary
        EncounterType.PARTY -> "🤝" to MaterialTheme.colorScheme.secondary
        EncounterType.COMBAT -> "⚔️" to MaterialTheme.colorScheme.error
        EncounterType.IGNORE -> "👁️" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    AssistChip(
        onClick = {},
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emoji)
                Text(type.getDisplayName())
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        )
    )
}

private fun formatLocation(location: String): String {
    return location
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.capitalize() }
}

private fun formatTimestamp(timestamp: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(timestamp)
}

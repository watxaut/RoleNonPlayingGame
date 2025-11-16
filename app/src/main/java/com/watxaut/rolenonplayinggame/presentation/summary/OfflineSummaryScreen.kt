package com.watxaut.rolenonplayinggame.presentation.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watxaut.rolenonplayinggame.data.remote.dto.OfflineSimulationResponse
import kotlin.math.roundToInt

/**
 * Screen showing "While you were away" summary for multiple characters
 */
@Composable
fun OfflineSummaryScreen(
    simulationResponses: List<OfflineSimulationResponse>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                MainHeader(
                    characterCount = simulationResponses.size,
                    totalHoursOffline = simulationResponses.firstOrNull()?.realHoursOffline ?: 0.0
                )
            }

            // Character summaries
            items(simulationResponses) { response ->
                CharacterSummaryCard(response)
            }

            // Continue Button
            item {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Continue Adventure")
                }
            }
        }
    }
}

@Composable
private fun MainHeader(
    characterCount: Int,
    totalHoursOffline: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "While You Were Away",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (characterCount == 1) {
                    "Your character was busy for ${totalHoursOffline.roundToInt()} hours"
                } else {
                    "Your $characterCount characters were busy for ${totalHoursOffline.roundToInt()} hours"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CharacterSummaryCard(response: OfflineSimulationResponse) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Character name header
            Text(
                text = response.characterName ?: "Unknown Hero",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "${response.gameHoursSimulated} game hours simulated",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Divider()

            // Statistics Summary
            StatisticsSummary(response)

            // Mission Progress (if any)
            if (response.missionProgress != null) {
                MissionProgressSection(response.missionProgress)
            }

            // Major Events
            if (response.summary.majorEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Major Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    response.summary.majorEvents.take(5).forEach { event ->
                        MajorEventCard(event)
                    }
                    if (response.summary.majorEvents.size > 5) {
                        Text(
                            text = "...and ${response.summary.majorEvents.size - 5} more events",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionProgressSection(missionProgress: com.watxaut.rolenonplayinggame.data.remote.dto.MissionProgressDto) {
    val hasProgress = missionProgress.principalMissionSteps > 0 ||
            missionProgress.secondaryMissionsDiscovered > 0 ||
            missionProgress.loreDiscovered > 0

    if (!hasProgress) return

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Mission Progress",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (missionProgress.principalMissionSteps > 0) {
            SummaryRow(
                icon = Icons.Default.Star,
                label = "Principal Mission Steps",
                value = "+${missionProgress.principalMissionSteps}",
                highlighted = true
            )
        }

        if (missionProgress.secondaryMissionsDiscovered > 0) {
            SummaryRow(
                icon = Icons.Default.Notifications,
                label = "New Secondary Missions",
                value = "+${missionProgress.secondaryMissionsDiscovered}",
                highlighted = true
            )
        }

        if (missionProgress.loreDiscovered > 0) {
            SummaryRow(
                icon = Icons.Default.Info,
                label = "Lore Discovered",
                value = "+${missionProgress.loreDiscovered}",
                highlighted = true
            )
        }
    }
}

@Composable
private fun StatisticsSummary(response: OfflineSimulationResponse) {
    val summary = response.summary

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Character State
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                icon = Icons.Default.Star,
                label = "Level",
                value = response.characterState.level.toString(),
                color = Color(0xFFFFD700) // Gold
            )
            StatItem(
                icon = Icons.Default.Favorite,
                label = "HP",
                value = "${response.characterState.currentHp}/${response.characterState.maxHp}",
                color = Color(0xFFE74C3C) // Red
            )
        }

        // Combat Stats
        if (summary.totalCombats > 0) {
            SummaryRow(
                icon = Icons.Default.Swords,
                label = "Battles",
                value = "${summary.combatsWon}W / ${summary.combatsLost}L"
            )
        }

        // Experience and Gold
        if (summary.totalXpGained > 0) {
            SummaryRow(
                icon = Icons.Default.Star,
                label = "XP Gained",
                value = "+${summary.totalXpGained}"
            )
        }

        if (summary.totalGoldGained > 0) {
            SummaryRow(
                icon = Icons.Default.ShoppingCart,
                label = "Gold Earned",
                value = "+${summary.totalGoldGained}"
            )
        }

        // Levels Gained
        if (summary.levelsGained > 0) {
            SummaryRow(
                icon = Icons.Default.KeyboardArrowUp,
                label = "Levels Gained",
                value = "+${summary.levelsGained}",
                highlighted = true
            )
        }

        // Locations and Items
        if (summary.locationsDiscovered > 0) {
            SummaryRow(
                icon = Icons.Default.Place,
                label = "New Locations",
                value = summary.locationsDiscovered.toString()
            )
        }

        if (summary.itemsFound > 0) {
            SummaryRow(
                icon = Icons.Default.Inventory,
                label = "Items Found",
                value = summary.itemsFound.toString()
            )
        }

        // Deaths (if any)
        if (summary.deaths > 0) {
            SummaryRow(
                icon = Icons.Default.Warning,
                label = "Deaths",
                value = summary.deaths.toString(),
                warning = true
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SummaryRow(
    icon: ImageVector,
    label: String,
    value: String,
    highlighted: Boolean = false,
    warning: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (highlighted) Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                ).padding(8.dp)
                else Modifier
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    warning -> MaterialTheme.colorScheme.error
                    highlighted -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (warning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MajorEventCard(event: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = event,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

// Extension for the missing Swords icon
private val Icons.Filled.Swords: ImageVector
    get() = Icons.Filled.Menu // Using menu as substitute for swords

private val Icons.Filled.Inventory: ImageVector
    get() = Icons.Filled.List // Using list as substitute for inventory

package com.watxaut.rolenonplayinggame.presentation.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.StatType

/**
 * Character creation screen
 * Allows player to create a new autonomous character
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterCreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate when character is created
    LaunchedEffect(uiState.creationComplete) {
        if (uiState.creationComplete && uiState.createdCharacterId != null) {
            onCharacterCreated(uiState.createdCharacterId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Your Character") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Step 1: Name Input
            item {
                NameInputSection(
                    name = uiState.name,
                    error = uiState.nameError,
                    onNameChange = viewModel::updateName
                )
            }

            // Step 2: Job Class Selection
            item {
                JobClassSelectionSection(
                    selectedJobClass = uiState.selectedJobClass,
                    onJobClassSelect = viewModel::selectJobClass
                )
            }

            // Step 3: Stat Allocation
            item {
                StatAllocationSection(
                    stats = uiState.stats,
                    remainingPoints = uiState.getRemainingPoints(),
                    primaryStat = uiState.getPrimaryStat(),
                    secondaryStat = uiState.getSecondaryStat(),
                    error = uiState.statsError,
                    onIncrementStat = viewModel::incrementStat,
                    onDecrementStat = viewModel::decrementStat,
                    onResetStats = viewModel::resetStats,
                    onApplyRecommended = viewModel::applyRecommendedStats
                )
            }

            // Error messages
            if (uiState.generalError != null) {
                item {
                    Text(
                        text = uiState.generalError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Create Button
            item {
                Button(
                    onClick = viewModel::createCharacter,
                    enabled = uiState.isValid() && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Character")
                    }
                }
            }

            // Spacing at bottom
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun NameInputSection(
    name: String,
    error: String?,
    onNameChange: (String) -> Unit
) {
    Column {
        Text(
            text = "1. Choose a Name",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Character Name") },
            placeholder = { Text("Enter a name...") },
            isError = error != null,
            supportingText = if (error != null) {
                { Text(error) }
            } else null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun JobClassSelectionSection(
    selectedJobClass: JobClass?,
    onJobClassSelect: (JobClass) -> Unit
) {
    Column {
        Text(
            text = "2. Select Your Job Class",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(JobClass.entries.toTypedArray()) { jobClass ->
                JobClassCard(
                    jobClass = jobClass,
                    isSelected = jobClass == selectedJobClass,
                    onClick = { onJobClassSelect(jobClass) }
                )
            }
        }
    }
}

@Composable
private fun JobClassCard(
    jobClass: JobClass,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = jobClass.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = jobClass.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Primary: ${jobClass.primaryStat.name}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Secondary: ${jobClass.secondaryStat.name}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StatAllocationSection(
    stats: com.watxaut.rolenonplayinggame.domain.model.CharacterStats,
    remainingPoints: Int,
    primaryStat: StatType?,
    secondaryStat: StatType?,
    error: String?,
    onIncrementStat: (StatType) -> Unit,
    onDecrementStat: (StatType) -> Unit,
    onResetStats: () -> Unit,
    onApplyRecommended: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "3. Allocate Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Points Remaining: $remainingPoints",
                style = MaterialTheme.typography.titleMedium,
                color = if (remainingPoints == 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onResetStats) {
                Text("Reset")
            }
            TextButton(onClick = onApplyRecommended) {
                Text("Apply Recommended")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stat rows
        StatRow(
            statType = StatType.STRENGTH,
            value = stats.strength,
            isPrimary = StatType.STRENGTH == primaryStat,
            isSecondary = StatType.STRENGTH == secondaryStat,
            onIncrement = onIncrementStat,
            onDecrement = onDecrementStat
        )
        StatRow(
            statType = StatType.INTELLIGENCE,
            value = stats.intelligence,
            isPrimary = StatType.INTELLIGENCE == primaryStat,
            isSecondary = StatType.INTELLIGENCE == secondaryStat,
            onIncrement = onIncrementStat,
            onDecrement = onDecrementStat
        )
        StatRow(
            statType = StatType.AGILITY,
            value = stats.agility,
            isPrimary = StatType.AGILITY == primaryStat,
            isSecondary = StatType.AGILITY == secondaryStat,
            onIncrement = onIncrementStat,
            onDecrement = onDecrementStat
        )
        StatRow(
            statType = StatType.LUCK,
            value = stats.luck,
            isPrimary = StatType.LUCK == primaryStat,
            isSecondary = StatType.LUCK == secondaryStat,
            onIncrement = onIncrementStat,
            onDecrement = onDecrementStat
        )
        StatRow(
            statType = StatType.CHARISMA,
            value = stats.charisma,
            isPrimary = StatType.CHARISMA == primaryStat,
            isSecondary = StatType.CHARISMA == secondaryStat,
            onIncrement = onIncrementStat,
            onDecrement = onDecrementStat
        )
        StatRow(
            statType = StatType.VITALITY,
            value = stats.vitality,
            isPrimary = StatType.VITALITY == primaryStat,
            isSecondary = StatType.VITALITY == secondaryStat,
            onIncrement = onIncrementStat,
            onDecrement = onDecrementStat
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun StatRow(
    statType: StatType,
    value: Int,
    isPrimary: Boolean,
    isSecondary: Boolean,
    onIncrement: (StatType) -> Unit,
    onDecrement: (StatType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = statType.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(120.dp)
            )
            if (isPrimary) {
                Text(
                    text = "★",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall
                )
            } else if (isSecondary) {
                Text(
                    text = "☆",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { onDecrement(statType) },
                enabled = value > 1
            ) {
                Icon(Icons.Default.Remove, "Decrease")
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { onIncrement(statType) }
            ) {
                Icon(Icons.Default.Add, "Increase")
            }
        }
    }
}

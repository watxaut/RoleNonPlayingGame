package com.watxaut.rolenonplayinggame.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.ActivityType
import com.watxaut.rolenonplayinggame.domain.model.Character
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Main game screen showing autonomous character gameplay.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md Week 3:
 * - Character visualization
 * - Real-time stat display
 * - Current location display
 * - Activity log UI
 * - Real-time autonomous actions (3-10s timer)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    characterId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }

    // Load character when screen opens
    LaunchedEffect(characterId) {
        viewModel.loadCharacter(characterId)
    }

    // Pause AI when screen is disposed (navigating away)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.pauseAi()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Observing ${uiState.character?.name ?: "Character"}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                // Loading state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading character...")
                }
            } else if (uiState.error != null) {
                // Error state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (uiState.character != null) {
                // Main game view with tabs
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Tab Row
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Current") }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Activity Log") }
                        )
                    }

                    // Tab Content
                    when (selectedTabIndex) {
                        0 -> CurrentTab(
                            character = uiState.character!!,
                            currentAction = uiState.currentAction,
                            combatLog = uiState.combatLog,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        1 -> ActivityLogTab(
                            activities = uiState.activityLog,
                            onActivityClick = { activity ->
                                selectedActivity = activity
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
            }

            // Level up animation overlay
            AnimatedVisibility(
                visible = uiState.showLevelUpAnimation,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Level Up",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFFFFD700) // Gold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "LEVEL UP!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Level ${uiState.character?.level ?: 1}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        // Activity detail dialog
        selectedActivity?.let { activity ->
            ActivityDetailDialog(
                activity = activity,
                onDismiss = { selectedActivity = null }
            )
        }
    }
}

/**
 * Tab 1: Current stats and activity
 */
@Composable
fun CurrentTab(
    character: Character,
    currentAction: String,
    combatLog: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Character stats card
        CharacterStatsCard(
            character = character,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Current action
        CurrentActionCard(
            currentAction = currentAction,
            modifier = Modifier.fillMaxWidth()
        )

        // Combat log (if in combat)
        if (combatLog.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            CombatLogSection(
                combatLog = combatLog,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Tab 2: Activity log with clickable items
 */
@Composable
fun ActivityLogTab(
    activities: List<Activity>,
    onActivityClick: (Activity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Activity Log",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(activities, key = { it.id }) { activity ->
                ClickableActivityLogItem(
                    activity = activity,
                    onClick = { onActivityClick(activity) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (activities.isEmpty()) {
                item {
                    Text(
                        text = "No activities yet. Character will start acting soon...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterStatsCard(
    character: Character,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Name, Level, Job Class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Level ${character.level} ${character.jobClass.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = character.currentLocation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // HP bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "HP",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "HP: ${character.currentHp} / ${character.maxHp}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    LinearProgressIndicator(
                        progress = { character.currentHp.toFloat() / character.maxHp.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // XP bar
            val xpForNextLevel = character.level * 100
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "XP",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "XP: ${character.experience} / $xpForNextLevel",
                        style = MaterialTheme.typography.bodySmall
                    )
                    LinearProgressIndicator(
                        progress = { character.experience.toFloat() / xpForNextLevel.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip("STR", character.strength)
                StatChip("INT", character.intelligence)
                StatChip("AGI", character.agility)
                StatChip("LUK", character.luck)
                StatChip("CHA", character.charisma)
                StatChip("VIT", character.vitality)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Gold
            Text(
                text = "Gold: ${character.gold}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFD700)
            )
        }
    }
}

@Composable
fun StatChip(statName: String, value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$statName: $value",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun CurrentActionCard(
    currentAction: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = currentAction,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActivityLogSection(
    activities: List<Activity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Activity Log",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(activities, key = { it.id }) { activity ->
                ActivityLogItem(activity = activity)
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (activities.isEmpty()) {
                item {
                    Text(
                        text = "No activities yet. Character will start acting soon...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(activity: Activity) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val time = activity.timestamp
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    val backgroundColor = when (activity.type) {
        ActivityType.COMBAT -> MaterialTheme.colorScheme.errorContainer
        ActivityType.LEVEL_UP -> Color(0xFFFFF9C4) // Light yellow
        ActivityType.DEATH -> Color(0xFFFFCDD2) // Light red
        ActivityType.EXPLORATION -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.description,
                    style = if (activity.isMajorEvent) {
                        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    } else {
                        MaterialTheme.typography.bodySmall
                    }
                )
                if (activity.rewards != null) {
                    Text(
                        text = buildString {
                            if (activity.rewards.experience > 0) append("+${activity.rewards.experience} XP ")
                            if (activity.rewards.gold > 0) append("+${activity.rewards.gold} Gold")
                        }.trim(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

/**
 * Clickable activity log item for the Activity Log tab
 */
@Composable
fun ClickableActivityLogItem(
    activity: Activity,
    onClick: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val time = activity.timestamp
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    val backgroundColor = when (activity.type) {
        ActivityType.COMBAT -> MaterialTheme.colorScheme.errorContainer
        ActivityType.LEVEL_UP -> Color(0xFFFFF9C4) // Light yellow
        ActivityType.DEATH -> Color(0xFFFFCDD2) // Light red
        ActivityType.EXPLORATION -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.description,
                    style = if (activity.isMajorEvent) {
                        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    } else {
                        MaterialTheme.typography.bodySmall
                    }
                )
                if (activity.rewards != null) {
                    Text(
                        text = buildString {
                            if (activity.rewards.experience > 0) append("+${activity.rewards.experience} XP ")
                            if (activity.rewards.gold > 0) append("+${activity.rewards.gold} Gold")
                        }.trim(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

/**
 * Dialog showing activity details with lore
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailDialog(
    activity: Activity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activity Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Activity type badge
                val activityTypeColor = when (activity.type) {
                    ActivityType.COMBAT -> Color.Red
                    ActivityType.LEVEL_UP -> Color(0xFFFFD700)
                    ActivityType.EXPLORATION -> Color.Blue
                    ActivityType.DEATH -> Color.DarkGray
                    else -> MaterialTheme.colorScheme.primary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(activityTypeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = activity.type.name.replace("_", " "),
                        style = MaterialTheme.typography.labelMedium,
                        color = activityTypeColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timestamp
                val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")
                val formattedTime = activity.timestamp
                    .atZone(ZoneId.systemDefault())
                    .format(dateFormatter)

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards section
                if (activity.rewards != null) {
                    Text(
                        text = "Rewards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (activity.rewards.experience > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "XP",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${activity.rewards.experience} Experience",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (activity.rewards.gold > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Gold",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${activity.rewards.gold} Gold",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    if (activity.rewards.items.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Items: ${activity.rewards.items.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Lore section
                activity.metadata["lore"]?.let { lore ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Lore",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lore,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CombatLogSection(
    combatLog: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Combat Log",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            combatLog.take(5).forEach { logLine ->
                Text(
                    text = logLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

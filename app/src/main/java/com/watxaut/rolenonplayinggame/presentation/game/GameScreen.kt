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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.watxaut.rolenonplayinggame.presentation.map.getLocationDisplayName
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
                    IconButton(onClick = {
                        // Pause AI BEFORE navigating to prevent database conflicts
                        viewModel.pauseAi()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
                            text = { Text("Equipment") }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Lore") }
                        )
                        Tab(
                            selected = selectedTabIndex == 3,
                            onClick = { selectedTabIndex = 3 },
                            text = { Text("Missions") }
                        )
                        Tab(
                            selected = selectedTabIndex == 4,
                            onClick = { selectedTabIndex = 4 },
                            text = { Text("Activity Log") }
                        )
                    }

                    // Tab Content
                    when (selectedTabIndex) {
                        0 -> CurrentTab(
                            character = uiState.character!!,
                            currentAction = uiState.currentAction,
                            principalMissionProgress = uiState.principalMissionProgress,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        1 -> EquipmentTab(
                            character = uiState.character!!,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        2 -> LoreTab(
                            discoveredLore = uiState.discoveredLore,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        3 -> SecondaryMissionsTab(
                            secondaryMissions = uiState.secondaryMissions,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        4 -> ActivityLogTab(
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
    principalMissionProgress: String?,
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

        Spacer(modifier = Modifier.weight(1f))

        // Principal mission display at the bottom
        if (principalMissionProgress != null) {
            Spacer(modifier = Modifier.height(12.dp))
            PrincipalMissionCard(
                missionProgress = principalMissionProgress,
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
                    text = getLocationDisplayName(character.currentLocation),
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

            // Stats (with equipment bonuses)
            val equipmentBonuses = character.equipment.getTotalBonuses()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChipWithBonus("STR", character.strength, equipmentBonuses.strength)
                StatChipWithBonus("INT", character.intelligence, equipmentBonuses.intelligence)
                StatChipWithBonus("AGI", character.agility, equipmentBonuses.agility)
                StatChipWithBonus("LUK", character.luck, equipmentBonuses.luck)
                StatChipWithBonus("CHA", character.charisma, equipmentBonuses.charisma)
                StatChipWithBonus("VIT", character.vitality, equipmentBonuses.vitality)
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

/**
 * Stat chip that shows base value + equipment bonus
 */
@Composable
fun StatChipWithBonus(statName: String, baseValue: Int, bonus: Int, modifier: Modifier = Modifier) {
    val totalValue = baseValue + bonus
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$statName: $totalValue",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = if (bonus > 0) FontWeight.Bold else FontWeight.Normal
            )
            if (bonus > 0) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "+$bonus",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50), // Green for bonuses
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Equipment tab showing all equipped items with fun descriptions.
 */
@Composable
fun EquipmentTab(
    character: Character,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        // Header with total bonuses
        item {
            val totalBonuses = character.equipment.getTotalBonuses()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Equipment Bonuses",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            BonusStat("STR", totalBonuses.strength)
                            BonusStat("INT", totalBonuses.intelligence)
                            BonusStat("AGI", totalBonuses.agility)
                        }
                        Column {
                            BonusStat("VIT", totalBonuses.vitality)
                            BonusStat("LUK", totalBonuses.luck)
                            BonusStat("CHA", totalBonuses.charisma)
                        }
                    }
                }
            }
        }

        // Main Weapon
        item {
            EquipmentSlotCard(
                slotName = "Main Weapon",
                equipment = character.equipment.weaponMain
            )
        }

        // Off-hand Weapon
        item {
            EquipmentSlotCard(
                slotName = "Off-hand Weapon",
                equipment = character.equipment.weaponOff
            )
        }

        // Armor
        item {
            EquipmentSlotCard(
                slotName = "Armor",
                equipment = character.equipment.armor
            )
        }

        // Gloves
        item {
            EquipmentSlotCard(
                slotName = "Gloves",
                equipment = character.equipment.gloves
            )
        }

        // Head Armor
        item {
            EquipmentSlotCard(
                slotName = "Head Armor",
                equipment = character.equipment.head
            )
        }

        // Accessory
        item {
            EquipmentSlotCard(
                slotName = "Accessory",
                equipment = character.equipment.accessory
            )
        }
    }
}

/**
 * Display a stat bonus
 */
@Composable
fun BonusStat(statName: String, bonus: Int) {
    if (bonus > 0) {
        Text(
            text = "$statName: +$bonus",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50) // Green for bonuses
        )
    }
}

/**
 * Card displaying an equipment slot and its item
 */
@Composable
fun EquipmentSlotCard(
    slotName: String,
    equipment: com.watxaut.rolenonplayinggame.domain.model.Equipment?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (equipment != null) {
                if (equipment.rarity == com.watxaut.rolenonplayinggame.domain.model.Rarity.RARE) {
                    Color(0xFFFFD700).copy(alpha = 0.1f) // Gold tint for rare
                } else {
                    MaterialTheme.colorScheme.surface
                }
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Slot name
            Text(
                text = slotName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (equipment != null) {
                // Equipment name with rarity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = equipment.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (equipment.rarity == com.watxaut.rolenonplayinggame.domain.model.Rarity.RARE) {
                            Color(0xFFFFD700) // Gold for rare
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = equipment.rarity.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (equipment.rarity == com.watxaut.rolenonplayinggame.domain.model.Rarity.RARE) {
                            Color(0xFFFFD700)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Description (fun flavor text!)
                Text(
                    text = equipment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (equipment.strengthBonus > 0) {
                        StatBonus("STR", equipment.strengthBonus)
                    }
                    if (equipment.intelligenceBonus > 0) {
                        StatBonus("INT", equipment.intelligenceBonus)
                    }
                    if (equipment.agilityBonus > 0) {
                        StatBonus("AGI", equipment.agilityBonus)
                    }
                    if (equipment.vitalityBonus > 0) {
                        StatBonus("VIT", equipment.vitalityBonus)
                    }
                    if (equipment.luckBonus > 0) {
                        StatBonus("LUK", equipment.luckBonus)
                    }
                    if (equipment.charismaBonus > 0) {
                        StatBonus("CHA", equipment.charismaBonus)
                    }
                }

                // Total stats
                Text(
                    text = "Total: +${equipment.getTotalStatBonus()} stats",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                // Empty slot
                Text(
                    text = "No equipment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

/**
 * Display a single stat bonus chip
 */
@Composable
fun StatBonus(statName: String, bonus: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
        )
    ) {
        Text(
            text = "$statName +$bonus",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

                // Description - show full description from metadata if available
                val displayDescription = activity.metadata?.get("fullDescription") as? String
                    ?: activity.description

                // Make description scrollable for long combat logs
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = displayDescription,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

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

/**
 * Display principal mission progress at bottom of Current tab
 */
@Composable
fun PrincipalMissionCard(
    missionProgress: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFD700).copy(alpha = 0.2f) // Gold tint
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Principal Mission",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Principal Mission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB8860B) // Dark golden
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = missionProgress,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Lore tab showing discovered world knowledge
 */
@Composable
fun LoreTab(
    discoveredLore: List<com.watxaut.rolenonplayinggame.domain.model.LoreDiscovery>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Discovered Lore",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your hero has discovered ${discoveredLore.size} lore entries",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(discoveredLore) { lore ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Category badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lore.category.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lore title
                    Text(
                        text = lore.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lore content
                    Text(
                        text = lore.content,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Discovery source
                    Text(
                        text = "Discovered: ${lore.sourceType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        if (discoveredLore.isEmpty()) {
            item {
                Text(
                    text = "No lore discovered yet. Your character started with knowledge of Aethermoor - check if the data has loaded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            }
        }
    }
}

/**
 * Secondary Missions tab showing active and completed missions
 */
@Composable
fun SecondaryMissionsTab(
    secondaryMissions: List<String>,
    modifier: Modifier = Modifier
) {
    var selectedMission by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Secondary Missions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${secondaryMissions.size} active missions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(secondaryMissions.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedMission = secondaryMissions[index] },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mission ${index + 1}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = secondaryMissions[index],
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "Ongoing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (secondaryMissions.isEmpty()) {
                item {
                    Text(
                        text = "No secondary missions yet. Your hero will discover them during adventures (1% chance per action).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            }
        }
    }

    // Mission detail popup
    selectedMission?.let { mission ->
        Dialog(onDismissRequest = { selectedMission = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mission Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { selectedMission = null }) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = mission,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Win Condition: (Details will be shown here)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

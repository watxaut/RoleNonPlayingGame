package com.watxaut.rolenonplayinggame.presentation.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.Location
import com.watxaut.rolenonplayinggame.domain.model.WorldRegion
import kotlin.math.cos
import kotlin.math.sin

/**
 * World map screen showing the Island of Aethermoor in isometric view.
 * Displays all five regions, locations, and player characters.
 */
@Composable
fun WorldMapScreen(
    modifier: Modifier = Modifier,
    viewModel: WorldMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedLocation by remember { mutableStateOf<Pair<WorldRegion, Location>?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // Dark background for the world
    ) {
        // Main map canvas with pan and zoom
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Draw the world map
            drawIsometricWorld(
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                characters = uiState.characters,
                onLocationClick = { region, location ->
                    selectedLocation = region to location
                }
            )
        }

        // Title card at top
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "The Island of Aethermoor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${uiState.characters.size} Characters Exploring",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Legend card at bottom
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Regions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                WorldRegion.entries.forEach { region ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(region.color))
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "${region.displayName} (${region.minLevel}-${region.maxLevel})",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Location detail dialog
        selectedLocation?.let { (region, location) ->
            LocationDetailDialog(
                region = region,
                location = location,
                characters = uiState.characters.filter { it.currentLocation == location.name },
                onDismiss = { selectedLocation = null }
            )
        }
    }
}

/**
 * Draw the isometric world map
 */
private fun DrawScope.drawIsometricWorld(
    canvasWidth: Float,
    canvasHeight: Float,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    characters: List<Character>,
    onLocationClick: (WorldRegion, Location) -> Unit
) {
    val centerX = canvasWidth / 2 + offsetX
    val centerY = canvasHeight / 2 + offsetY

    // Define the layout of regions in isometric grid
    // Heartlands in center, others around it
    val regionPositions = mapOf(
        WorldRegion.HEARTLANDS to Offset(0f, 0f),
        WorldRegion.THORNWOOD_WILDS to Offset(-250f, 0f),
        WorldRegion.FROSTPEAK_MOUNTAINS to Offset(0f, -200f),
        WorldRegion.ASHENVEIL_DESERT to Offset(0f, 200f),
        WorldRegion.STORMCOAST_REACHES to Offset(250f, 0f)
    )

    // Draw each region
    WorldRegion.entries.forEach { region ->
        val regionOffset = regionPositions[region] ?: Offset(0f, 0f)
        drawRegion(
            region = region,
            baseX = centerX + regionOffset.x * scale,
            baseY = centerY + regionOffset.y * scale,
            scale = scale,
            characters = characters
        )
    }
}

/**
 * Draw a single region with its locations
 */
private fun DrawScope.drawRegion(
    region: WorldRegion,
    baseX: Float,
    baseY: Float,
    scale: Float,
    characters: List<Character>
) {
    val tileWidth = 200f * scale
    val tileHeight = 150f * scale

    // Draw region base as isometric diamond
    drawIsometricTile(
        centerX = baseX,
        centerY = baseY,
        width = tileWidth,
        height = tileHeight,
        color = Color(region.color).copy(alpha = 0.6f)
    )

    // Draw region name
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 14f * scale
            color = android.graphics.Color.WHITE
            isFakeBoldText = true
        }
        drawText(
            region.displayName,
            baseX,
            baseY - tileHeight / 3,
            paint
        )
    }

    // Draw locations as smaller tiles
    region.getLocations().forEach { location ->
        val locX = baseX + (location.x - 0.5f) * tileWidth * 0.8f
        val locY = baseY + (location.y - 0.5f) * tileHeight * 0.8f

        // Draw location marker
        drawCircle(
            color = Color.White,
            radius = 6f * scale,
            center = Offset(locX, locY)
        )
        drawCircle(
            color = Color(region.color),
            radius = 4f * scale,
            center = Offset(locX, locY)
        )

        // Draw location name
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 10f * scale
                color = android.graphics.Color.WHITE
            }
            drawText(
                location.name,
                locX,
                locY + 15f * scale,
                paint
            )
        }

        // Draw characters at this location
        val charactersHere = characters.filter { it.currentLocation == location.name }
        charactersHere.forEachIndexed { index, character ->
            val charX = locX + (index * 12f * scale)
            val charY = locY - 20f * scale

            // Draw character marker
            drawCircle(
                color = Color.Yellow,
                radius = 5f * scale,
                center = Offset(charX, charY),
                style = Stroke(width = 2f * scale)
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 3f * scale,
                center = Offset(charX, charY)
            )
        }
    }
}

/**
 * Draw an isometric tile (diamond shape)
 */
private fun DrawScope.drawIsometricTile(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val halfWidth = width / 2
    val halfHeight = height / 2

    val path = Path().apply {
        moveTo(centerX, centerY - halfHeight) // Top
        lineTo(centerX + halfWidth, centerY) // Right
        lineTo(centerX, centerY + halfHeight) // Bottom
        lineTo(centerX - halfWidth, centerY) // Left
        close()
    }

    // Fill
    drawPath(path, color, style = Fill)

    // Border
    drawPath(path, Color.White.copy(alpha = 0.3f), style = Stroke(width = 2f))
}

/**
 * Dialog showing location details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailDialog(
    region: WorldRegion,
    location: Location,
    characters: List<Character>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = location.type.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Region info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Region",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = region.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Level Range",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${region.minLevel}-${region.maxLevel}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Characters at location
                if (characters.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Characters Here (${characters.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.height(150.dp)
                    ) {
                        items(characters) { character ->
                            CharacterAtLocationItem(character)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Character item in location dialog
 */
@Composable
fun CharacterAtLocationItem(character: Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lv ${character.level} ${character.jobClass.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${character.currentHp}/${character.maxHp} HP",
                style = MaterialTheme.typography.labelSmall,
                color = if (character.currentHp < character.maxHp / 2) Color.Red else Color.Green
            )
        }
    }
}

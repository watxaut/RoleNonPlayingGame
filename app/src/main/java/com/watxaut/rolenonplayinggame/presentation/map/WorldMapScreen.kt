package com.watxaut.rolenonplayinggame.presentation.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.Region
import kotlin.math.sqrt

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
    var selectedLocation by remember { mutableStateOf<Pair<Region, MapLocation>?>(null) }
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
                .pointerInput(scale, offsetX, offsetY) {
                    detectTapGestures { tapOffset ->
                        // Transform tap coordinates to map coordinates
                        val canvasWidth = size.width.toFloat()
                        val canvasHeight = size.height.toFloat()

                        // Reverse the transformations applied to the canvas
                        val pivotX = canvasWidth / 2
                        val pivotY = canvasHeight / 2

                        // Reverse translate
                        val translatedX = tapOffset.x - offsetX
                        val translatedY = tapOffset.y - offsetY

                        // Reverse scale (scale is applied around pivot point)
                        val scaledX = (translatedX - pivotX) / scale + pivotX
                        val scaledY = (translatedY - pivotY) / scale + pivotY

                        // Find which location was tapped
                        val tappedLocation = findLocationAt(
                            x = scaledX,
                            y = scaledY,
                            canvasWidth = canvasWidth,
                            canvasHeight = canvasHeight
                        )

                        tappedLocation?.let { (region, location) ->
                            selectedLocation = region to location
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Apply transformations to the entire canvas
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.translate(offsetX, offsetY)
                canvas.scale(scale, scale, canvasWidth / 2, canvasHeight / 2)

                // Draw the world map
                drawIsometricWorld(
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    characters = uiState.characters
                )

                canvas.restore()
            }
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

        // Legend card at bottom left
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
                Region.entries.forEach { region ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(region.getMapColor()))
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "${region.displayName} (${region.levelRange.first}-${region.levelRange.last})",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Zoom controls and info at bottom right
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Zoom: ${String.format("%.1f", scale)}x",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pinch to zoom\nDrag to pan\nTap locations for info",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.clickable {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Reset View",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Location detail dialog
        selectedLocation?.let { (region, location) ->
            LocationDetailDialog(
                region = region,
                location = location,
                characters = uiState.characters.filter { it.currentLocation == location.locationName },
                onDismiss = { selectedLocation = null }
            )
        }
    }
}

/**
 * Find which location was tapped based on coordinates
 */
private fun findLocationAt(
    x: Float,
    y: Float,
    canvasWidth: Float,
    canvasHeight: Float
): Pair<Region, MapLocation>? {
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2

    val tileWidth = 200f
    val tileHeight = 150f
    val hitRadius = 30f // Hit detection radius in pixels

    var closestLocation: Pair<Region, MapLocation>? = null
    var closestDistance = Float.MAX_VALUE

    // Check each region and its locations
    Region.entries.forEach { region ->
        val regionOffset = region.getMapPosition()
        val baseX = centerX + regionOffset.x
        val baseY = centerY + regionOffset.y

        region.getMapLocations().forEach { location ->
            val locX = baseX + (location.x - 0.5f) * tileWidth * 0.8f
            val locY = baseY + (location.y - 0.5f) * tileHeight * 0.8f

            // Calculate distance from tap to location
            val dx = x - locX
            val dy = y - locY
            val distance = sqrt(dx * dx + dy * dy)

            // Check if this is the closest location within hit radius
            if (distance < hitRadius && distance < closestDistance) {
                closestDistance = distance
                closestLocation = region to location
            }
        }
    }

    return closestLocation
}

/**
 * Draw the isometric world map
 */
private fun DrawScope.drawIsometricWorld(
    canvasWidth: Float,
    canvasHeight: Float,
    characters: List<Character>
) {
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2

    // Draw each region
    Region.entries.forEach { region ->
        val regionOffset = region.getMapPosition()
        drawRegion(
            region = region,
            baseX = centerX + regionOffset.x,
            baseY = centerY + regionOffset.y,
            characters = characters
        )
    }
}

/**
 * Draw a single region with its locations
 */
private fun DrawScope.drawRegion(
    region: Region,
    baseX: Float,
    baseY: Float,
    characters: List<Character>
) {
    val tileWidth = 200f
    val tileHeight = 150f

    // Draw region base as isometric diamond
    drawIsometricTile(
        centerX = baseX,
        centerY = baseY,
        width = tileWidth,
        height = tileHeight,
        color = Color(region.getMapColor()).copy(alpha = 0.6f)
    )

    // Draw region name using native canvas
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 14f
            color = android.graphics.Color.WHITE
            isFakeBoldText = true
        }
        canvas.nativeCanvas.drawText(
            region.displayName,
            baseX,
            baseY - tileHeight / 3,
            paint
        )
    }

    // Draw locations as smaller tiles
    region.getMapLocations().forEach { location ->
        val locX = baseX + (location.x - 0.5f) * tileWidth * 0.8f
        val locY = baseY + (location.y - 0.5f) * tileHeight * 0.8f

        // Draw location marker
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = Offset(locX, locY)
        )
        drawCircle(
            color = Color(region.getMapColor()),
            radius = 4f,
            center = Offset(locX, locY)
        )

        // Draw location name
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 10f
                color = android.graphics.Color.WHITE
            }
            canvas.nativeCanvas.drawText(
                location.locationName,
                locX,
                locY + 15f,
                paint
            )
        }

        // Draw characters at this location
        val charactersHere = characters.filter { it.currentLocation == location.locationName }
        charactersHere.forEachIndexed { index, _ ->
            val charX = locX + (index * 12f)
            val charY = locY - 20f

            // Draw character marker
            drawCircle(
                color = Color.Yellow,
                radius = 5f,
                center = Offset(charX, charY),
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 3f,
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
    region: Region,
    location: MapLocation,
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
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = location.locationName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = location.locationType.displayName,
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
                            text = "${region.levelRange.first}-${region.levelRange.last}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = region.description,
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

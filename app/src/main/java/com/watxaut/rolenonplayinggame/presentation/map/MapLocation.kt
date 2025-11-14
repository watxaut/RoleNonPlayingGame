package com.watxaut.rolenonplayinggame.presentation.map

import androidx.compose.ui.geometry.Offset
import com.watxaut.rolenonplayinggame.domain.model.Region

/**
 * Map-specific data for rendering locations on the isometric world map.
 * Contains visual positioning data separate from the domain Location model.
 */
data class MapLocation(
    val locationName: String,
    val x: Float, // Normalized position 0.0 to 1.0 relative to region center
    val y: Float, // Normalized position 0.0 to 1.0 relative to region center
    val locationType: MapLocationType
)

/**
 * Visual types for map markers
 */
enum class MapLocationType(val displayName: String) {
    TOWN("Town"),
    VILLAGE("Village"),
    OUTPOST("Outpost"),
    FORTRESS("Fortress"),
    TEMPLE("Temple"),
    RUINS("Ruins"),
    DUNGEON("Dungeon"),
    FOREST("Forest"),
    OASIS("Oasis"),
    LANDMARK("Landmark"),
    PASS("Mountain Pass"),
    SHIPWRECK("Shipwreck")
}

/**
 * Extension to get map visual data for each region
 */
fun Region.getMapLocations(): List<MapLocation> {
    return when (this) {
        Region.HEARTLANDS -> listOf(
            MapLocation("Havenmoor", 0.5f, 0.5f, MapLocationType.TOWN),
            MapLocation("Whispering Grove", 0.3f, 0.4f, MapLocationType.FOREST),
            MapLocation("Broken Bridge", 0.6f, 0.6f, MapLocationType.RUINS),
            MapLocation("Miller's Rest", 0.4f, 0.7f, MapLocationType.VILLAGE)
        )
        Region.THORNWOOD_WILDS -> listOf(
            MapLocation("Eldergrove Ruins", 0.2f, 0.3f, MapLocationType.RUINS),
            MapLocation("The Webbed Hollow", 0.15f, 0.5f, MapLocationType.DUNGEON),
            MapLocation("Moonwell Glade", 0.25f, 0.6f, MapLocationType.LANDMARK),
            MapLocation("Thornguard Outpost", 0.3f, 0.4f, MapLocationType.OUTPOST)
        )
        Region.ASHENVEIL_DESERT -> listOf(
            MapLocation("Sandstone Bazaar", 0.5f, 0.8f, MapLocationType.TOWN),
            MapLocation("The Bone Canyons", 0.4f, 0.85f, MapLocationType.DUNGEON),
            MapLocation("Mirage Springs", 0.6f, 0.75f, MapLocationType.OASIS),
            MapLocation("Sunscorch Ruins", 0.5f, 0.9f, MapLocationType.RUINS)
        )
        Region.FROSTPEAK_MOUNTAINS -> listOf(
            MapLocation("Irondelve Hold", 0.5f, 0.15f, MapLocationType.FORTRESS),
            MapLocation("Crystalfall Caverns", 0.6f, 0.1f, MapLocationType.DUNGEON),
            MapLocation("The Sky Monastery", 0.45f, 0.05f, MapLocationType.TEMPLE),
            MapLocation("Avalanche Pass", 0.55f, 0.2f, MapLocationType.PASS)
        )
        Region.STORMCOAST_REACHES -> listOf(
            MapLocation("Wrecker's Cove", 0.85f, 0.5f, MapLocationType.TOWN),
            MapLocation("The Drowned Cathedral", 0.9f, 0.6f, MapLocationType.TEMPLE),
            MapLocation("Lighthouse Point", 0.95f, 0.4f, MapLocationType.LANDMARK),
            MapLocation("The Shattered Fleet", 0.85f, 0.7f, MapLocationType.SHIPWRECK)
        )
    }
}

/**
 * Get region color for map visualization
 */
fun Region.getMapColor(): Long {
    return when (this) {
        Region.HEARTLANDS -> 0xFF8BC34A // Green
        Region.THORNWOOD_WILDS -> 0xFF2E7D32 // Dark Green
        Region.ASHENVEIL_DESERT -> 0xFFFFB74D // Orange/Sand
        Region.FROSTPEAK_MOUNTAINS -> 0xFF81D4FA // Light Blue
        Region.STORMCOAST_REACHES -> 0xFF1976D2 // Deep Blue
    }
}

/**
 * Get region position on the isometric map
 */
fun Region.getMapPosition(): Offset {
    return when (this) {
        Region.HEARTLANDS -> Offset(0f, 0f)
        Region.THORNWOOD_WILDS -> Offset(-250f, 0f)
        Region.FROSTPEAK_MOUNTAINS -> Offset(0f, -200f)
        Region.ASHENVEIL_DESERT -> Offset(0f, 200f)
        Region.STORMCOAST_REACHES -> Offset(250f, 0f)
    }
}

package com.watxaut.rolenonplayinggame.presentation.map

import androidx.compose.ui.geometry.Offset
import com.watxaut.rolenonplayinggame.domain.model.Region

/**
 * Map-specific data for rendering locations on the isometric world map.
 * Contains visual positioning data separate from the domain Location model.
 */
data class MapLocation(
    val locationName: String, // Display name (e.g., "Whispering Grove")
    val locationId: String, // Normalized ID for matching (e.g., "whispering_grove")
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
            MapLocation("Havenmoor", "heartlands_havenmoor", 0.5f, 0.5f, MapLocationType.TOWN),
            MapLocation("Whispering Grove", "heartlands_whispering_grove", 0.2f, 0.3f, MapLocationType.FOREST),
            MapLocation("Broken Bridge", "heartlands_broken_bridge", 0.8f, 0.7f, MapLocationType.RUINS),
            MapLocation("Miller's Rest", "heartlands_millers_rest", 0.3f, 0.8f, MapLocationType.VILLAGE),
            MapLocation("Meadowbrook Fields", "heartlands_meadowbrook_fields", 0.6f, 0.4f, MapLocationType.LANDMARK)
        )
        Region.THORNWOOD_WILDS -> listOf(
            MapLocation("Eldergrove Ruins", "thornwood_wilds_eldergrove_ruins", 0.3f, 0.2f, MapLocationType.RUINS),
            MapLocation("The Webbed Hollow", "thornwood_wilds_webbed_hollow", 0.1f, 0.6f, MapLocationType.DUNGEON),
            MapLocation("Moonwell Glade", "thornwood_wilds_moonwell_glade", 0.5f, 0.8f, MapLocationType.LANDMARK),
            MapLocation("Thornguard Outpost", "thornwood_wilds_thornguard_outpost", 0.7f, 0.4f, MapLocationType.OUTPOST)
        )
        Region.ASHENVEIL_DESERT -> listOf(
            MapLocation("Sandstone Bazaar", "ashenveil_desert_sandstone_bazaar", 0.5f, 0.5f, MapLocationType.TOWN),
            MapLocation("The Bone Canyons", "ashenveil_desert_bone_canyons", 0.2f, 0.8f, MapLocationType.DUNGEON),
            MapLocation("Mirage Springs", "ashenveil_desert_mirage_springs", 0.8f, 0.3f, MapLocationType.OASIS),
            MapLocation("Sunscorch Ruins", "ashenveil_desert_sunscorch_ruins", 0.5f, 0.9f, MapLocationType.RUINS)
        )
        Region.FROSTPEAK_MOUNTAINS -> listOf(
            MapLocation("Irondelve Hold", "frostpeak_mountains_irondelve_hold", 0.5f, 0.5f, MapLocationType.FORTRESS),
            MapLocation("Crystalfall Caverns", "frostpeak_mountains_crystalfall_caverns", 0.8f, 0.3f, MapLocationType.DUNGEON),
            MapLocation("The Sky Monastery", "frostpeak_mountains_sky_monastery", 0.2f, 0.2f, MapLocationType.TEMPLE),
            MapLocation("Avalanche Pass", "frostpeak_mountains_avalanche_pass", 0.7f, 0.7f, MapLocationType.PASS)
        )
        Region.STORMCOAST_REACHES -> listOf(
            MapLocation("Wrecker's Cove", "stormcoast_reaches_wreckers_cove", 0.5f, 0.5f, MapLocationType.TOWN),
            MapLocation("The Drowned Cathedral", "stormcoast_reaches_drowned_cathedral", 0.8f, 0.7f, MapLocationType.TEMPLE),
            MapLocation("Lighthouse Point", "stormcoast_reaches_lighthouse_point", 0.9f, 0.2f, MapLocationType.LANDMARK),
            MapLocation("The Shattered Fleet", "stormcoast_reaches_shattered_fleet", 0.3f, 0.8f, MapLocationType.SHIPWRECK)
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
 * Positioned to create a connected island layout
 */
fun Region.getMapPosition(): Offset {
    // Regions are positioned to touch each other in an island formation
    return when (this) {
        Region.HEARTLANDS -> Offset(0f, 0f) // Center
        Region.THORNWOOD_WILDS -> Offset(-150f, 0f) // West (touching Heartlands)
        Region.FROSTPEAK_MOUNTAINS -> Offset(0f, -150f) // North (touching Heartlands)
        Region.ASHENVEIL_DESERT -> Offset(0f, 150f) // South (touching Heartlands)
        Region.STORMCOAST_REACHES -> Offset(150f, 0f) // East (touching Heartlands)
    }
}

/**
 * Convert a location ID to a display name
 * e.g., "heartlands_meadowbrook_fields" -> "Meadowbrook Fields"
 */
fun getLocationDisplayName(locationId: String): String {
    // Find the location in all regions
    Region.entries.forEach { region ->
        region.getMapLocations().find { it.locationId == locationId }?.let {
            return it.locationName
        }
    }

    // Fallback: convert snake_case to Title Case
    return locationId
        .split("_")
        .drop(1) // Remove region prefix
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}

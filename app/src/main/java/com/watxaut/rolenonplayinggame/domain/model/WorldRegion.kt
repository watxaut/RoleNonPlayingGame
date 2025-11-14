package com.watxaut.rolenonplayinggame.domain.model

/**
 * Represents a region in the world of Aethermoor.
 * Based on WORLD_LORE.md Chapter II: The Five Regions
 */
enum class WorldRegion(
    val displayName: String,
    val description: String,
    val minLevel: Int,
    val maxLevel: Int,
    val difficulty: RegionDifficulty,
    val climate: String,
    val color: Long // Color for map visualization
) {
    HEARTLANDS(
        displayName = "The Heartlands",
        description = "Rolling plains, gentle hills, scattered groves",
        minLevel = 1,
        maxLevel = 10,
        difficulty = RegionDifficulty.BEGINNER,
        climate = "Temperate, mild seasons",
        color = 0xFF8BC34A // Green
    ),
    THORNWOOD_WILDS(
        displayName = "The Thornwood Wilds",
        description = "Dense forests, tangled undergrowth, hidden clearings",
        minLevel = 11,
        maxLevel = 25,
        difficulty = RegionDifficulty.INTERMEDIATE,
        climate = "Humid, frequent rain, perpetual twilight",
        color = 0xFF2E7D32 // Dark Green
    ),
    ASHENVEIL_DESERT(
        displayName = "The Ashenveil Desert",
        description = "Vast sand dunes, rocky badlands, dry canyons",
        minLevel = 26,
        maxLevel = 40,
        difficulty = RegionDifficulty.ADVANCED,
        climate = "Scorching days, freezing nights",
        color = 0xFFFFB74D // Orange/Sand
    ),
    FROSTPEAK_MOUNTAINS(
        displayName = "The Frostpeak Mountains",
        description = "Towering peaks, frozen glaciers, treacherous passes",
        minLevel = 26,
        maxLevel = 40,
        difficulty = RegionDifficulty.ADVANCED,
        climate = "Perpetual winter, blizzards",
        color = 0xFF81D4FA // Light Blue
    ),
    STORMCOAST_REACHES(
        displayName = "The Stormcoast Reaches",
        description = "Rocky coastline, sea cliffs, storm-battered shores",
        minLevel = 35,
        maxLevel = 50,
        difficulty = RegionDifficulty.EXPERT,
        climate = "Constant storms, high winds",
        color = 0xFF1976D2 // Deep Blue
    );

    companion object {
        /**
         * Get region by location name
         */
        fun getRegionByLocation(locationName: String): WorldRegion? {
            return entries.firstOrNull { region ->
                region.getLocations().any { it.name == locationName }
            }
        }
    }

    /**
     * Get all locations in this region
     */
    fun getLocations(): List<Location> {
        return when (this) {
            HEARTLANDS -> listOf(
                Location("Havenmoor", "The central town and starting point", 0.5f, 0.5f, LocationType.TOWN),
                Location("Whispering Grove", "A peaceful forest perfect for early exploration", 0.3f, 0.4f, LocationType.FOREST),
                Location("Broken Bridge", "Ruins of an old crossing, now home to goblin camps", 0.6f, 0.6f, LocationType.RUINS),
                Location("Miller's Rest", "A small farming village", 0.4f, 0.7f, LocationType.VILLAGE)
            )
            THORNWOOD_WILDS -> listOf(
                Location("Eldergrove Ruins", "Remnants of an ancient elven settlement", 0.2f, 0.3f, LocationType.RUINS),
                Location("The Webbed Hollow", "Cave system infested with giant spiders", 0.15f, 0.5f, LocationType.DUNGEON),
                Location("Moonwell Glade", "A mystical clearing where strange lights dance", 0.25f, 0.6f, LocationType.LANDMARK),
                Location("Thornguard Outpost", "A small fortified camp of rangers and hunters", 0.3f, 0.4f, LocationType.OUTPOST)
            )
            ASHENVEIL_DESERT -> listOf(
                Location("Sandstone Bazaar", "A mobile trading post that moves between oases", 0.5f, 0.8f, LocationType.TOWN),
                Location("The Bone Canyons", "Labyrinthine ravines filled with undead", 0.4f, 0.85f, LocationType.DUNGEON),
                Location("Mirage Springs", "A hidden oasis and rare safe zone", 0.6f, 0.75f, LocationType.OASIS),
                Location("Sunscorch Ruins", "A buried city slowly being revealed by shifting sands", 0.5f, 0.9f, LocationType.RUINS)
            )
            FROSTPEAK_MOUNTAINS -> listOf(
                Location("Irondelve Hold", "An abandoned dwarven fortress, now overrun", 0.5f, 0.15f, LocationType.FORTRESS),
                Location("Crystalfall Caverns", "Ice caves filled with frozen treasures", 0.6f, 0.1f, LocationType.DUNGEON),
                Location("The Sky Monastery", "A mysterious temple at the highest accessible peak", 0.45f, 0.05f, LocationType.TEMPLE),
                Location("Avalanche Pass", "The main route through the mountains", 0.55f, 0.2f, LocationType.PASS)
            )
            STORMCOAST_REACHES -> listOf(
                Location("Wrecker's Cove", "A pirate haven built into the cliffs", 0.85f, 0.5f, LocationType.TOWN),
                Location("The Drowned Cathedral", "A partially submerged temple accessible at low tide", 0.9f, 0.6f, LocationType.TEMPLE),
                Location("Lighthouse Point", "An ancient lighthouse that still burns with eternal flame", 0.95f, 0.4f, LocationType.LANDMARK),
                Location("The Shattered Fleet", "A graveyard of ships spanning centuries", 0.85f, 0.7f, LocationType.SHIPWRECK)
            )
        }
    }
}

/**
 * Difficulty level of a region
 */
enum class RegionDifficulty(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    EXPERT("Expert")
}

/**
 * A specific location within a region
 */
data class Location(
    val name: String,
    val description: String,
    val x: Float, // Normalized position 0.0 to 1.0 on the map
    val y: Float, // Normalized position 0.0 to 1.0 on the map
    val type: LocationType
)

/**
 * Type of location
 */
enum class LocationType(val displayName: String) {
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

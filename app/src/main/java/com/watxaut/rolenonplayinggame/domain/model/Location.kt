package com.watxaut.rolenonplayinggame.domain.model

/**
 * Represents a location in the game world.
 * Based on WORLD_LORE.md - The Island of Aethermoor
 */
data class Location(
    val id: String,
    val name: String,
    val region: Region,
    val description: String,
    val levelRange: IntRange,
    val dangerLevel: Int, // 1-5, where 1 is safest
    val hasInn: Boolean = false,
    val hasShop: Boolean = false,
    val hasQuestGiver: Boolean = false,
    val monstersPresent: List<String> = emptyList()
) {
    companion object {
        /**
         * Starting location - safe town in the Heartlands
         */
        fun startingTown() = Location(
            id = "heartlands_havenmoor",
            name = "Havenmoor",
            region = Region.HEARTLANDS,
            description = "A peaceful town where many adventurers begin their journey. " +
                    "The town elder welcomes all newcomers and offers guidance.",
            levelRange = 1..5,
            dangerLevel = 1,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        )

        /**
         * First exploration area - safe fields
         */
        fun startingFields() = Location(
            id = "heartlands_meadowbrook_fields",
            name = "Meadowbrook Fields",
            region = Region.HEARTLANDS,
            description = "Rolling green fields dotted with wildflowers. " +
                    "Small slimes and rabbits hop about, perfect for beginners.",
            levelRange = 1..3,
            dangerLevel = 1,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Slime", "Wild Rabbit", "Grass Snake")
        )

        /**
         * Get all initial locations (Week 2: just 2 locations)
         */
        fun getInitialLocations(): List<Location> {
            return listOf(
                startingTown(),
                startingFields()
            )
        }
    }
}

/**
 * Regions of the Island of Aethermoor
 * From WORLD_LORE.md
 */
enum class Region(
    val displayName: String,
    val description: String,
    val levelRange: IntRange
) {
    HEARTLANDS(
        "The Heartlands",
        "Rolling hills, peaceful villages, and gentle streams. The safest region for new adventurers.",
        1..10
    ),
    THORNWOOD_WILDS(
        "Thornwood Wilds",
        "Dense forests filled with ancient trees and mysterious ruins.",
        11..20
    ),
    ASHENVEIL_DESERT(
        "Ashenveil Desert",
        "A harsh desert landscape with scorching days and freezing nights.",
        21..30
    ),
    FROSTPEAK_MOUNTAINS(
        "Frostpeak Mountains",
        "Towering snow-capped peaks shrouded in eternal winter.",
        31..40
    ),
    STORMCOAST_REACHES(
        "Stormcoast Reaches",
        "Treacherous coastlines battered by relentless storms and waves.",
        41..50
    );

    fun isAccessibleAtLevel(level: Int): Boolean {
        return level >= levelRange.first
    }
}

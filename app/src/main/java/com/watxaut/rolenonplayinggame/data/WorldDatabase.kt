package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.domain.model.Location
import com.watxaut.rolenonplayinggame.domain.model.Region

/**
 * Comprehensive world database for all locations across Aethermoor.
 * Based on WORLD_LORE.md - The Island of Aethermoor
 *
 * Phase 3 - World & Content Implementation
 */
object WorldDatabase {

    // ==================== HEARTLANDS (Levels 1-10) ====================

    private val heartlandsLocations = listOf(
        Location(
            id = "heartlands_havenmoor",
            name = "Havenmoor",
            region = Region.HEARTLANDS,
            description = "The eternal refuge. A safe town protected by ancient magic where violence is impossible. Home to the Adventurer's Guild and countless shops.",
            levelRange = 1..50,
            dangerLevel = 1,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "heartlands_meadowbrook_fields",
            name = "Meadowbrook Fields",
            region = Region.HEARTLANDS,
            description = "Rolling green fields dotted with wildflowers. Perfect for beginners to test their mettle against slimes and rabbits.",
            levelRange = 1..3,
            dangerLevel = 1,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Slime", "Wild Rabbit", "Grass Snake")
        ),
        Location(
            id = "heartlands_whispering_grove",
            name = "Whispering Grove",
            region = Region.HEARTLANDS,
            description = "A peaceful forest where the wind makes the leaves whisper ancient secrets. Home to Ironhide, the legendary boar.",
            levelRange = 3..7,
            dangerLevel = 2,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Forest Wolf", "Wild Boar", "Scarecrow", "Ironhide")
        ),
        Location(
            id = "heartlands_broken_bridge",
            name = "Broken Bridge",
            region = Region.HEARTLANDS,
            description = "Ruins of an old stone bridge, now a goblin encampment ruled by King Skritch.",
            levelRange = 5..8,
            dangerLevel = 2,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Goblin Scout", "Goblin Warrior", "Goblin King Skritch")
        ),
        Location(
            id = "heartlands_millers_rest",
            name = "Miller's Rest",
            region = Region.HEARTLANDS,
            description = "A small farming village offering simple quests and basic supplies.",
            levelRange = 1..10,
            dangerLevel = 1,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "heartlands_old_ruins",
            name = "Old Ruins",
            region = Region.HEARTLANDS,
            description = "Ancient stone ruins covered in moss. Strange magic lingers here.",
            levelRange = 7..10,
            dangerLevel = 3,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Animated Armor", "Lesser Demon", "Bandit Leader")
        )
    )

    // ==================== THORNWOOD WILDS (Levels 11-25) ====================

    private val thornwoodLocations = listOf(
        Location(
            id = "thornwood_eldergrove_ruins",
            name = "Eldergrove Ruins",
            region = Region.THORNWOOD_WILDS,
            description = "Remnants of an ancient elven settlement, overgrown with vines and decay.",
            levelRange = 11..15,
            dangerLevel = 3,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Corrupted Ent", "Vine Horror", "Spectral Elf")
        ),
        Location(
            id = "thornwood_webbed_hollow",
            name = "The Webbed Hollow",
            region = Region.THORNWOOD_WILDS,
            description = "A cave system infested with giant spiders. Home to the Widow Queen.",
            levelRange = 15..20,
            dangerLevel = 4,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Giant Spider", "Web Lurker", "Widow Queen")
        ),
        Location(
            id = "thornwood_moonwell_glade",
            name = "Moonwell Glade",
            region = Region.THORNWOOD_WILDS,
            description = "A mystical clearing where strange lights dance at night. Guarded by Silvermane.",
            levelRange = 13..18,
            dangerLevel = 3,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Dire Wolf", "Moon Spirit", "Silvermane")
        ),
        Location(
            id = "thornwood_thornguard_outpost",
            name = "Thornguard Outpost",
            region = Region.THORNWOOD_WILDS,
            description = "A fortified ranger camp. The only safe haven in the Thornwood.",
            levelRange = 11..25,
            dangerLevel = 1,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "thornwood_deep_forest",
            name = "Deep Thornwood",
            region = Region.THORNWOOD_WILDS,
            description = "The darkest depths of the forest where sunlight never reaches. Thornmaw roams here.",
            levelRange = 18..25,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Forest Troll", "Corrupted Treant", "Thornmaw", "Fae Trickster")
        )
    )

    // ==================== ASHENVEIL DESERT (Levels 21-35) ====================

    private val ashenve ilLocations = listOf(
        Location(
            id = "ashenveil_sandstone_bazaar",
            name = "Sandstone Bazaar",
            region = Region.ASHENVEIL_DESERT,
            description = "A mobile trading post run by the Sandstrider Clans. Moves between oases.",
            levelRange = 21..35,
            dangerLevel = 1,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "ashenveil_bone_canyons",
            name = "The Bone Canyons",
            region = Region.ASHENVEIL_DESERT,
            description = "Labyrinthine ravines filled with undead and ancient tombs. The Bone Empress rules here.",
            levelRange = 28..35,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Skeletal Warrior", "Mummy Guardian", "Bone Empress", "Sand Wraith")
        ),
        Location(
            id = "ashenveil_mirage_springs",
            name = "Mirage Springs",
            region = Region.ASHENVEIL_DESERT,
            description = "A hidden oasis and rare safe zone in the endless desert.",
            levelRange = 21..35,
            dangerLevel = 1,
            hasInn = true,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = emptyList()
        ),
        Location(
            id = "ashenveil_sunscorch_ruins",
            name = "Sunscorch Ruins",
            region = Region.ASHENVEIL_DESERT,
            description = "A buried city slowly being revealed by shifting sands. Ashstorm dwells within.",
            levelRange = 25..32,
            dangerLevel = 4,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Fire Elemental", "Sand Golem", "Ashstorm", "Desert Cultist")
        ),
        Location(
            id = "ashenveil_deep_desert",
            name = "Deep Desert",
            region = Region.ASHENVEIL_DESERT,
            description = "Endless dunes where Sandreaver, the colossal sandworm, surfaces randomly.",
            levelRange = 30..35,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Desert Scorpion", "Sand Wurm", "Sandreaver", "Desert Bandit")
        )
    )

    // ==================== FROSTPEAK MOUNTAINS (Levels 26-40) ====================

    private val frostpeakLocations = listOf(
        Location(
            id = "frostpeak_mountain_base",
            name = "Mountain Base Camp",
            region = Region.FROSTPEAK_MOUNTAINS,
            description = "A small settlement of the Irondelve Reclaimers at the mountain's base.",
            levelRange = 26..40,
            dangerLevel = 1,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "frostpeak_irondelve_hold",
            name = "Irondelve Hold",
            region = Region.FROSTPEAK_MOUNTAINS,
            description = "An abandoned dwarven fortress, now claimed by the Frost Tyrant and Hoarfang.",
            levelRange = 32..40,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Frost Giant", "Ice Troll", "Frost Tyrant", "Hoarfang")
        ),
        Location(
            id = "frostpeak_crystalfall_caverns",
            name = "Crystalfall Caverns",
            region = Region.FROSTPEAK_MOUNTAINS,
            description = "Ice caves filled with frozen treasures and frost elementals.",
            levelRange = 28..35,
            dangerLevel = 4,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Frost Elemental", "Ice Sprite", "Frozen Horror")
        ),
        Location(
            id = "frostpeak_sky_monastery",
            name = "The Sky Monastery",
            region = Region.FROSTPEAK_MOUNTAINS,
            description = "A mysterious temple at the highest accessible peak. Few dare to climb here.",
            levelRange = 35..40,
            dangerLevel = 4,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = true,
            monstersPresent = listOf("Snow Leopard", "Mountain Hermit", "Wind Spirit")
        ),
        Location(
            id = "frostpeak_avalanche_pass",
            name = "Avalanche Pass",
            region = Region.FROSTPEAK_MOUNTAINS,
            description = "The main route through the mountains. Home to Avalanche, the Yeti Patriarch.",
            levelRange = 26..32,
            dangerLevel = 4,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Yeti", "Snow Wolf", "Avalanche")
        )
    )

    // ==================== STORMCOAST REACHES (Levels 35-50) ====================

    private val stormcoastLocations = listOf(
        Location(
            id = "stormcoast_wreckers_cove",
            name = "Wrecker's Cove",
            region = Region.STORMCOAST_REACHES,
            description = "A pirate haven built into the cliffs. Dangerous but has supplies for the right price.",
            levelRange = 35..50,
            dangerLevel = 2,
            hasInn = true,
            hasShop = true,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "stormcoast_drowned_cathedral",
            name = "The Drowned Cathedral",
            region = Region.STORMCOAST_REACHES,
            description = "A partially submerged temple accessible at low tide. The Tide Caller dwells within.",
            levelRange = 38..45,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Sahuagin Warrior", "Sea Elemental", "Tide Caller", "Drowned Priest")
        ),
        Location(
            id = "stormcoast_lighthouse_point",
            name = "Lighthouse Point",
            region = Region.STORMCOAST_REACHES,
            description = "An ancient lighthouse that burns with eternal flame. Home to the Stormwatch scholars.",
            levelRange = 35..50,
            dangerLevel = 1,
            hasInn = true,
            hasShop = false,
            hasQuestGiver = true,
            monstersPresent = emptyList()
        ),
        Location(
            id = "stormcoast_shattered_fleet",
            name = "The Shattered Fleet",
            region = Region.STORMCOAST_REACHES,
            description = "A graveyard of ships spanning centuries. Captain Dreadmoor's ghostly crew guards it.",
            levelRange = 40..48,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Ghost Pirate", "Sea Specter", "Captain Dreadmoor", "Cursed Sailor")
        ),
        Location(
            id = "stormcoast_tempest_waters",
            name = "Tempest Waters",
            region = Region.STORMCOAST_REACHES,
            description = "The open ocean where the fiercest storms rage. Tempest, the Storm Leviathan, appears here.",
            levelRange = 45..50,
            dangerLevel = 5,
            hasInn = false,
            hasShop = false,
            hasQuestGiver = false,
            monstersPresent = listOf("Storm Elemental", "Sea Serpent", "Tempest", "Lightning Drake")
        )
    )

    /**
     * Get all locations across all regions
     */
    fun getAllLocations(): List<Location> {
        return heartlandsLocations +
               thornwoodLocations +
               ashenveilLocations +
               frostpeakLocations +
               stormcoastLocations
    }

    /**
     * Get locations for a specific region
     */
    fun getLocationsForRegion(region: Region): List<Location> {
        return when (region) {
            Region.HEARTLANDS -> heartlandsLocations
            Region.THORNWOOD_WILDS -> thornwoodLocations
            Region.ASHENVEIL_DESERT -> ashenveilLocations
            Region.FROSTPEAK_MOUNTAINS -> frostpeakLocations
            Region.STORMCOAST_REACHES -> stormcoastLocations
        }
    }

    /**
     * Get location by ID
     */
    fun getLocationById(id: String): Location? {
        return getAllLocations().find { it.id == id }
    }

    /**
     * Get location by name
     */
    fun getLocationByName(name: String): Location? {
        return getAllLocations().find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get locations accessible at a given character level
     */
    fun getAccessibleLocations(level: Int): List<Location> {
        return getAllLocations().filter { location ->
            level >= location.levelRange.first - 2 // Allow exploring slightly harder areas
        }
    }

    /**
     * Get random location appropriate for level
     */
    fun getRandomLocationForLevel(level: Int): Location {
        val accessible = getAccessibleLocations(level)
            .filter { it.levelRange.contains(level) || it.levelRange.first <= level + 3 }

        return accessible.randomOrNull() ?: heartlandsLocations.first()
    }

    /**
     * Get safe towns (inns and shops)
     */
    fun getSafeTowns(): List<Location> {
        return getAllLocations().filter { it.hasInn && it.hasShop }
    }

    /**
     * Get nearest safe town for a region
     */
    fun getNearestSafeTown(region: Region): Location {
        return getLocationsForRegion(region).find { it.hasInn && it.hasShop }
            ?: heartlandsLocations.first() // Fallback to Havenmoor
    }

    /**
     * Get initial discovered locations for new characters
     */
    fun getStartingLocations(): List<String> {
        return listOf("heartlands_havenmoor", "heartlands_meadowbrook_fields")
    }
}

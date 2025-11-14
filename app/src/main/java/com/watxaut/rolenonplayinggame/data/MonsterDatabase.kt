package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.core.combat.Enemy
import com.watxaut.rolenonplayinggame.core.combat.EnemyType

/**
 * Comprehensive Monster Database for all regions of Aethermoor (Levels 1-50).
 * Based on WORLD_LORE.md - Legendary Monsters & Bosses
 *
 * Phase 3 - World & Content Implementation
 */
object MonsterDatabase {

    // ==================== HEARTLANDS MONSTERS (Levels 1-10) ====================

    private val heartlandsMonsters = listOf(
        // Starting Enemies (Levels 1-3)
        Enemy(
            name = "Slime",
            level = 1,
            hp = 15,
            strength = 2,
            intelligence = 1,
            agility = 1,
            luck = 1,
            vitality = 2,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Wild Rabbit",
            level = 1,
            hp = 10,
            strength = 1,
            intelligence = 1,
            agility = 4,
            luck = 2,
            vitality = 1,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Grass Snake",
            level = 2,
            hp = 12,
            strength = 3,
            intelligence = 1,
            agility = 3,
            luck = 1,
            vitality = 1,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Scarecrow",
            level = 2,
            hp = 18,
            strength = 3,
            intelligence = 1,
            agility = 1,
            luck = 0,
            vitality = 3,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Wild Boar",
            level = 3,
            hp = 25,
            strength = 4,
            intelligence = 1,
            agility = 2,
            luck = 1,
            vitality = 3,
            enemyType = EnemyType.NORMAL
        ),
        // Early Heartlands (Levels 4-6)
        Enemy(
            name = "Forest Wolf",
            level = 4,
            hp = 30,
            strength = 5,
            intelligence = 2,
            agility = 4,
            luck = 1,
            vitality = 3,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Goblin Scout",
            level = 4,
            hp = 25,
            strength = 4,
            intelligence = 3,
            agility = 3,
            luck = 2,
            vitality = 2,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Giant Spider",
            level = 5,
            hp = 28,
            strength = 4,
            intelligence = 2,
            agility = 5,
            luck = 1,
            vitality = 2,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Bandit",
            level = 5,
            hp = 35,
            strength = 5,
            intelligence = 3,
            agility = 4,
            luck = 3,
            vitality = 3,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Animated Armor",
            level = 6,
            hp = 40,
            strength = 6,
            intelligence = 1,
            agility = 2,
            luck = 0,
            vitality = 5,
            enemyType = EnemyType.NORMAL
        ),
        // Mid Heartlands (Levels 7-10)
        Enemy(
            name = "Goblin Warrior",
            level = 7,
            hp = 45,
            strength = 6,
            intelligence = 3,
            agility = 4,
            luck = 2,
            vitality = 4,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Cave Bear",
            level = 8,
            hp = 55,
            strength = 8,
            intelligence = 1,
            agility = 2,
            luck = 1,
            vitality = 6,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Corrupted Treant",
            level = 10,
            hp = 70,
            strength = 8,
            intelligence = 4,
            agility = 1,
            luck = 1,
            vitality = 7,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Lesser Demon",
            level = 10,
            hp = 60,
            strength = 7,
            intelligence = 7,
            agility = 5,
            luck = 2,
            vitality = 5,
            enemyType = EnemyType.ELITE
        ),
        // Heartlands Bosses
        Enemy(
            name = "Goblin King Skritch",
            level = 8,
            hp = 120,
            strength = 9,
            intelligence = 6,
            agility = 7,
            luck = 5,
            vitality = 7,
            enemyType = EnemyType.BOSS
        ),
        Enemy(
            name = "Ironhide",
            level = 10,
            hp = 200,
            strength = 12,
            intelligence = 2,
            agility = 4,
            luck = 3,
            vitality = 15,
            enemyType = EnemyType.WORLD_BOSS
        ),
        Enemy(
            name = "Bandit Leader",
            level = 9,
            hp = 100,
            strength = 9,
            intelligence = 7,
            agility = 8,
            luck = 6,
            vitality = 6,
            enemyType = EnemyType.ELITE
        )
    )

    // ==================== THORNWOOD WILDS MONSTERS (Levels 11-25) ====================

    private val thornwoodMonsters = listOf(
        Enemy(
            name = "Dire Wolf",
            level = 13,
            hp = 80,
            strength = 10,
            intelligence = 3,
            agility = 9,
            luck = 2,
            vitality = 7,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Forest Troll",
            level = 16,
            hp = 120,
            strength = 13,
            intelligence = 4,
            agility = 3,
            luck = 1,
            vitality = 11,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Corrupted Ent",
            level = 14,
            hp = 100,
            strength = 11,
            intelligence = 5,
            agility = 2,
            luck = 1,
            vitality = 10,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Vine Horror",
            level = 12,
            hp = 70,
            strength = 8,
            intelligence = 6,
            agility = 6,
            luck = 2,
            vitality = 6,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Spectral Elf",
            level = 15,
            hp = 85,
            strength = 7,
            intelligence = 12,
            agility = 10,
            luck = 4,
            vitality = 5,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Web Lurker",
            level = 17,
            hp = 95,
            strength = 11,
            intelligence = 4,
            agility = 12,
            luck = 2,
            vitality = 7,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Moon Spirit",
            level = 14,
            hp = 75,
            strength = 6,
            intelligence = 14,
            agility = 11,
            luck = 8,
            vitality = 5,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Fae Trickster",
            level = 19,
            hp = 90,
            strength = 8,
            intelligence = 15,
            agility = 13,
            luck = 10,
            vitality = 6,
            enemyType = EnemyType.ELITE
        ),
        // Thornwood Bosses
        Enemy(
            name = "Widow Queen",
            level = 18,
            hp = 280,
            strength = 14,
            intelligence = 8,
            agility = 16,
            luck = 5,
            vitality = 12,
            enemyType = EnemyType.BOSS
        ),
        Enemy(
            name = "Silvermane",
            level = 15,
            hp = 220,
            strength = 15,
            intelligence = 6,
            agility = 14,
            luck = 7,
            vitality = 13,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Thornmaw",
            level = 20,
            hp = 400,
            strength = 18,
            intelligence = 10,
            agility = 5,
            luck = 4,
            vitality = 20,
            enemyType = EnemyType.WORLD_BOSS
        )
    )

    // ==================== ASHENVEIL DESERT MONSTERS (Levels 21-35) ====================

    private val ashenveilMonsters = listOf(
        Enemy(
            name = "Desert Scorpion",
            level = 22,
            hp = 110,
            strength = 13,
            intelligence = 2,
            agility = 11,
            luck = 3,
            vitality = 9,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Sand Wurm",
            level = 24,
            hp = 130,
            strength = 15,
            intelligence = 3,
            agility = 7,
            luck = 2,
            vitality = 11,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Fire Elemental",
            level = 26,
            hp = 100,
            strength = 17,
            intelligence = 15,
            agility = 10,
            luck = 3,
            vitality = 8,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Sand Golem",
            level = 27,
            hp = 180,
            strength = 18,
            intelligence = 2,
            agility = 4,
            luck = 1,
            vitality = 16,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Desert Bandit",
            level = 23,
            hp = 105,
            strength = 12,
            intelligence = 8,
            agility = 13,
            luck = 7,
            vitality = 8,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Desert Cultist",
            level = 28,
            hp = 115,
            strength = 10,
            intelligence = 18,
            agility = 12,
            luck = 6,
            vitality = 9,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Skeletal Warrior",
            level = 29,
            hp = 125,
            strength = 16,
            intelligence = 3,
            agility = 11,
            luck = 2,
            vitality = 10,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Mummy Guardian",
            level = 31,
            hp = 160,
            strength = 17,
            intelligence = 12,
            agility = 7,
            luck = 4,
            vitality = 14,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Sand Wraith",
            level = 30,
            hp = 140,
            strength = 14,
            intelligence = 16,
            agility = 15,
            luck = 8,
            vitality = 10,
            enemyType = EnemyType.ELITE
        ),
        // Ashenveil Bosses
        Enemy(
            name = "Ashstorm",
            level = 30,
            hp = 450,
            strength = 22,
            intelligence = 20,
            agility = 16,
            luck = 8,
            vitality = 18,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Bone Empress",
            level = 32,
            hp = 500,
            strength = 20,
            intelligence = 22,
            agility = 12,
            luck = 10,
            vitality = 20,
            enemyType = EnemyType.BOSS
        ),
        Enemy(
            name = "Sandreaver",
            level = 35,
            hp = 800,
            strength = 28,
            intelligence = 8,
            agility = 10,
            luck = 5,
            vitality = 30,
            enemyType = EnemyType.WORLD_BOSS
        )
    )

    // ==================== FROSTPEAK MOUNTAINS MONSTERS (Levels 26-40) ====================

    private val frostpeakMonsters = listOf(
        Enemy(
            name = "Yeti",
            level = 27,
            hp = 150,
            strength = 18,
            intelligence = 4,
            agility = 8,
            luck = 2,
            vitality = 13,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Snow Wolf",
            level = 26,
            hp = 120,
            strength = 14,
            intelligence = 3,
            agility = 14,
            luck = 3,
            vitality = 10,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Frost Elemental",
            level = 30,
            hp = 130,
            strength = 16,
            intelligence = 18,
            agility = 13,
            luck = 4,
            vitality = 11,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Ice Sprite",
            level = 28,
            hp = 100,
            strength = 12,
            intelligence = 16,
            agility = 15,
            luck = 9,
            vitality = 8,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Frozen Horror",
            level = 32,
            hp = 180,
            strength = 20,
            intelligence = 10,
            agility = 6,
            luck = 2,
            vitality = 16,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Ice Troll",
            level = 34,
            hp = 200,
            strength = 22,
            intelligence = 5,
            agility = 7,
            luck = 2,
            vitality = 18,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Frost Giant",
            level = 36,
            hp = 250,
            strength = 25,
            intelligence = 8,
            agility = 9,
            luck = 3,
            vitality = 22,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Snow Leopard",
            level = 33,
            hp = 140,
            strength = 17,
            intelligence = 6,
            agility = 19,
            luck = 6,
            vitality = 11,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Wind Spirit",
            level = 35,
            hp = 160,
            strength = 14,
            intelligence = 20,
            agility = 22,
            luck = 12,
            vitality = 10,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Mountain Hermit",
            level = 37,
            hp = 190,
            strength = 20,
            intelligence = 18,
            agility = 15,
            luck = 8,
            vitality = 15,
            enemyType = EnemyType.ELITE
        ),
        // Frostpeak Bosses
        Enemy(
            name = "Avalanche",
            level = 28,
            hp = 400,
            strength = 24,
            intelligence = 6,
            agility = 10,
            luck = 4,
            vitality = 22,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Frost Tyrant",
            level = 35,
            hp = 600,
            strength = 28,
            intelligence = 15,
            agility = 12,
            luck = 6,
            vitality = 25,
            enemyType = EnemyType.BOSS
        ),
        Enemy(
            name = "Hoarfang",
            level = 38,
            hp = 900,
            strength = 32,
            intelligence = 18,
            agility = 14,
            luck = 8,
            vitality = 35,
            enemyType = EnemyType.WORLD_BOSS
        )
    )

    // ==================== STORMCOAST REACHES MONSTERS (Levels 35-50) ====================

    private val stormcoastMonsters = listOf(
        Enemy(
            name = "Sahuagin Warrior",
            level = 38,
            hp = 220,
            strength = 22,
            intelligence = 10,
            agility = 18,
            luck = 5,
            vitality = 16,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Sea Elemental",
            level = 40,
            hp = 200,
            strength = 20,
            intelligence = 24,
            agility = 20,
            luck = 8,
            vitality = 15,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Drowned Priest",
            level = 39,
            hp = 210,
            strength = 18,
            intelligence = 25,
            agility = 14,
            luck = 7,
            vitality = 16,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Ghost Pirate",
            level = 41,
            hp = 230,
            strength = 23,
            intelligence = 12,
            agility = 20,
            luck = 10,
            vitality = 17,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Sea Specter",
            level = 43,
            hp = 240,
            strength = 20,
            intelligence = 22,
            agility = 22,
            luck = 12,
            vitality = 16,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Cursed Sailor",
            level = 40,
            hp = 215,
            strength = 21,
            intelligence = 14,
            agility = 19,
            luck = 6,
            vitality = 18,
            enemyType = EnemyType.NORMAL
        ),
        Enemy(
            name = "Storm Elemental",
            level = 45,
            hp = 250,
            strength = 24,
            intelligence = 28,
            agility = 24,
            luck = 10,
            vitality = 18,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Sea Serpent",
            level = 46,
            hp = 300,
            strength = 28,
            intelligence = 14,
            agility = 22,
            luck = 8,
            vitality = 24,
            enemyType = EnemyType.ELITE
        ),
        Enemy(
            name = "Lightning Drake",
            level = 48,
            hp = 350,
            strength = 30,
            intelligence = 26,
            agility = 26,
            luck = 12,
            vitality = 26,
            enemyType = EnemyType.ELITE
        ),
        // Stormcoast Bosses
        Enemy(
            name = "Tide Caller",
            level = 40,
            hp = 700,
            strength = 26,
            intelligence = 30,
            agility = 20,
            luck = 10,
            vitality = 28,
            enemyType = EnemyType.BOSS
        ),
        Enemy(
            name = "Captain Dreadmoor",
            level = 42,
            hp = 750,
            strength = 28,
            intelligence = 22,
            agility = 26,
            luck = 15,
            vitality = 30,
            enemyType = EnemyType.BOSS
        ),
        Enemy(
            name = "Tempest",
            level = 50,
            hp = 1500,
            strength = 40,
            intelligence = 35,
            agility = 30,
            luck = 15,
            vitality = 50,
            enemyType = EnemyType.WORLD_BOSS
        )
    )

    /**
     * Get all monsters across all regions
     */
    fun getAllMonsters(): List<Enemy> {
        return heartlandsMonsters +
               thornwoodMonsters +
               ashenveilMonsters +
               frostpeakMonsters +
               stormcoastMonsters
    }

    /**
     * Get a random enemy appropriate for a specific level
     */
    fun getEnemyForLevel(level: Int, location: String = ""): Enemy {
        // Try to get region-specific enemies first
        val regionalEnemies = when {
            location.contains("thornwood", ignoreCase = true) -> thornwoodMonsters
            location.contains("ashenveil", ignoreCase = true) || location.contains("desert", ignoreCase = true) -> ashenveilMonsters
            location.contains("frostpeak", ignoreCase = true) || location.contains("mountain", ignoreCase = true) -> frostpeakMonsters
            location.contains("stormcoast", ignoreCase = true) || location.contains("coast", ignoreCase = true) -> stormcoastMonsters
            else -> heartlandsMonsters
        }

        // Filter to appropriate level range (±3 levels)
        val appropriateEnemies = regionalEnemies.filter {
            it.level in (level - 3)..(level + 3) && it.enemyType == EnemyType.NORMAL
        }

        return if (appropriateEnemies.isNotEmpty()) {
            val template = appropriateEnemies.random()
            scaleEnemyToLevel(template, level)
        } else {
            // Fallback: scale a random normal enemy from the region
            val fallback = regionalEnemies.filter { it.enemyType == EnemyType.NORMAL }.randomOrNull()
                ?: heartlandsMonsters.first()
            scaleEnemyToLevel(fallback, level)
        }
    }

    /**
     * Get a specific enemy by name
     */
    fun getEnemyByName(name: String, level: Int? = null): Enemy? {
        val enemy = getAllMonsters().find { it.name.equals(name, ignoreCase = true) }
        return if (enemy != null && level != null) {
            scaleEnemyToLevel(enemy, level)
        } else {
            enemy
        }
    }

    /**
     * Get enemies for a specific location
     */
    fun getEnemiesInLocation(locationId: String): List<String> {
        return when (locationId) {
            // Heartlands
            "heartlands_meadowbrook_fields" -> listOf("Slime", "Wild Rabbit", "Grass Snake", "Wild Boar")
            "heartlands_whispering_grove" -> listOf("Forest Wolf", "Wild Boar", "Scarecrow", "Ironhide")
            "heartlands_broken_bridge" -> listOf("Goblin Scout", "Goblin Warrior", "Goblin King Skritch")
            "heartlands_old_ruins" -> listOf("Animated Armor", "Lesser Demon", "Bandit Leader")

            // Thornwood
            "thornwood_eldergrove_ruins" -> listOf("Corrupted Ent", "Vine Horror", "Spectral Elf")
            "thornwood_webbed_hollow" -> listOf("Giant Spider", "Web Lurker", "Widow Queen")
            "thornwood_moonwell_glade" -> listOf("Dire Wolf", "Moon Spirit", "Silvermane")
            "thornwood_deep_forest" -> listOf("Forest Troll", "Corrupted Treant", "Thornmaw", "Fae Trickster")

            // Ashenveil
            "ashenveil_bone_canyons" -> listOf("Skeletal Warrior", "Mummy Guardian", "Bone Empress", "Sand Wraith")
            "ashenveil_sunscorch_ruins" -> listOf("Fire Elemental", "Sand Golem", "Ashstorm", "Desert Cultist")
            "ashenveil_deep_desert" -> listOf("Desert Scorpion", "Sand Wurm", "Sandreaver", "Desert Bandit")

            // Frostpeak
            "frostpeak_irondelve_hold" -> listOf("Frost Giant", "Ice Troll", "Frost Tyrant", "Hoarfang")
            "frostpeak_crystalfall_caverns" -> listOf("Frost Elemental", "Ice Sprite", "Frozen Horror")
            "frostpeak_avalanche_pass" -> listOf("Yeti", "Snow Wolf", "Avalanche")
            "frostpeak_sky_monastery" -> listOf("Snow Leopard", "Mountain Hermit", "Wind Spirit")

            // Stormcoast
            "stormcoast_drowned_cathedral" -> listOf("Sahuagin Warrior", "Sea Elemental", "Tide Caller", "Drowned Priest")
            "stormcoast_shattered_fleet" -> listOf("Ghost Pirate", "Sea Specter", "Captain Dreadmoor", "Cursed Sailor")
            "stormcoast_tempest_waters" -> listOf("Storm Elemental", "Sea Serpent", "Tempest", "Lightning Drake")

            else -> listOf("Slime", "Wild Rabbit") // Safe default
        }
    }

    /**
     * Scale enemy stats to a specific level
     */
    private fun scaleEnemyToLevel(template: Enemy, targetLevel: Int): Enemy {
        if (template.level == targetLevel) return template

        val scaleFactor = targetLevel.toDouble() / template.level

        return template.copy(
            level = targetLevel,
            hp = (template.hp * scaleFactor * 1.2).toInt(), // HP scales faster
            strength = (template.strength * scaleFactor).toInt().coerceAtLeast(1),
            intelligence = (template.intelligence * scaleFactor).toInt().coerceAtLeast(1),
            agility = (template.agility * scaleFactor).toInt().coerceAtLeast(1),
            luck = template.luck, // Luck doesn't scale
            vitality = (template.vitality * scaleFactor).toInt().coerceAtLeast(1)
        )
    }

    /**
     * Get boss enemies
     */
    fun getBosses(): List<Enemy> {
        return getAllMonsters().filter { it.enemyType == EnemyType.BOSS || it.enemyType == EnemyType.WORLD_BOSS }
    }

    /**
     * Get world bosses
     */
    fun getWorldBosses(): List<Enemy> {
        return getAllMonsters().filter { it.enemyType == EnemyType.WORLD_BOSS }
    }
}

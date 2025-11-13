package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.core.combat.Enemy
import com.watxaut.rolenonplayinggame.core.combat.EnemyType

/**
 * Enemy Database for the game.
 * Per WORLD_LORE.md, enemies are region-specific and scale with level.
 *
 * Week 3: Starting enemies for Heartlands region (levels 1-10).
 * Future weeks will add enemies for other regions.
 */
object EnemyDatabase {

    /**
     * Get a random enemy appropriate for a specific level.
     * @param level Character's level (used to match enemy difficulty)
     * @param location Optional location name for region-specific enemies
     * @return Random enemy scaled to the level
     */
    fun getEnemyForLevel(level: Int, location: String = "Meadowbrook Fields"): Enemy {
        val enemies = when {
            level <= 3 -> startingEnemies
            level <= 6 -> earlyHeartlandsEnemies
            level <= 10 -> midHeartlandsEnemies
            else -> midHeartlandsEnemies // Fallback for Week 3
        }

        return enemies.random().let { template ->
            // Scale enemy stats to character level
            scaleEnemyToLevel(template, level)
        }
    }

    /**
     * Get a specific enemy by name.
     */
    fun getEnemyByName(name: String, level: Int = 1): Enemy? {
        return allEnemies.find { it.name.equals(name, ignoreCase = true) }
            ?.let { scaleEnemyToLevel(it, level) }
    }

    /**
     * Scale enemy stats to a specific level.
     * Base stats are for level 1, then scaled up.
     */
    private fun scaleEnemyToLevel(template: Enemy, targetLevel: Int): Enemy {
        val scaleFactor = targetLevel.toDouble() / template.level

        return template.copy(
            level = targetLevel,
            hp = (template.hp * scaleFactor * 1.2).toInt(), // HP scales faster
            strength = (template.strength * scaleFactor).toInt(),
            intelligence = (template.intelligence * scaleFactor).toInt(),
            agility = (template.agility * scaleFactor).toInt(),
            luck = template.luck, // Luck doesn't scale
            vitality = (template.vitality * scaleFactor).toInt()
        )
    }

    // ====================
    // HEARTLANDS ENEMIES
    // ====================

    /**
     * Starting enemies (Levels 1-3)
     * Found in: Meadowbrook Fields, Willowdale Outskirts
     */
    private val startingEnemies = listOf(
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
            agility = 4, // Fast!
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
        )
    )

    /**
     * Early Heartlands enemies (Levels 4-6)
     * Found in: Whispering Woods, River Crossing
     */
    private val earlyHeartlandsEnemies = listOf(
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
        )
    )

    /**
     * Mid Heartlands enemies (Levels 7-10)
     * Found in: Old Ruins, Bandit Camp, Dark Cave
     */
    private val midHeartlandsEnemies = listOf(
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
            name = "Bandit Leader",
            level = 9,
            hp = 50,
            strength = 7,
            intelligence = 5,
            agility = 6,
            luck = 4,
            vitality = 4,
            enemyType = EnemyType.ELITE
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
        )
    )

    private val allEnemies = startingEnemies + earlyHeartlandsEnemies + midHeartlandsEnemies

    /**
     * Get list of enemies for a specific location.
     * Used for AI decision-making (what enemies are nearby).
     */
    fun getEnemiesInLocation(location: String): List<String> {
        return when (location.lowercase()) {
            "meadowbrook fields" -> listOf("Slime", "Wild Rabbit", "Grass Snake", "Wild Boar")
            "willowdale village" -> emptyList() // Safe zone
            "whispering woods" -> listOf("Forest Wolf", "Goblin Scout", "Giant Spider")
            "river crossing" -> listOf("Goblin Scout", "Bandit", "Animated Armor")
            "old ruins" -> listOf("Goblin Warrior", "Animated Armor", "Lesser Demon")
            "bandit camp" -> listOf("Bandit", "Bandit Leader", "Goblin Warrior")
            "dark cave" -> listOf("Cave Bear", "Giant Spider", "Corrupted Treant")
            else -> startingEnemies.map { it.name } // Default to starting enemies
        }
    }
}

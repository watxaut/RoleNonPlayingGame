package com.watxaut.rolenonplayinggame.data.repository

import com.watxaut.rolenonplayinggame.domain.model.*

/**
 * Repository containing 100 secondary missions with varied win conditions.
 * Secondary missions can be discovered during gameplay (1% chance) and heroes can
 * have multiple active at once.
 *
 * Rewards: Always XP and gold, 20% equipment chance, 2% rare equipment chance
 */
object SecondaryMissionsRepository {

    private val missions = listOf(
        // EXPLORATION MISSIONS (1-20)
        SecondaryMission(
            id = "sm_001",
            name = "Tourist of the Heartlands",
            description = "Visit all major locations in the Heartlands region",
            category = SecondaryMissionCategory.EXPLORATION,
            winCondition = SecondaryWinCondition.VisitLocation("heartlands_whispering_grove", "Whispering Grove"),
            baseExperience = 200,
            baseGold = 100,
            completionText = "You've explored the Heartlands thoroughly. The veterans nod approvingly."
        ),
        SecondaryMission(
            id = "sm_002",
            name = "Into the Wilds",
            description = "Brave the depths of the Thornwood Wilds",
            category = SecondaryMissionCategory.EXPLORATION,
            winCondition = SecondaryWinCondition.VisitLocation("thornwood_deep_forest", "Deep Thornwood"),
            baseExperience = 500,
            baseGold = 250,
            completionText = "The Thornwood's secrets are now yours to discover."
        ),
        SecondaryMission(
            id = "sm_003",
            name = "Desert Wanderer",
            description = "Survive a journey to the Ashenveil Desert",
            category = SecondaryMissionCategory.EXPLORATION,
            winCondition = SecondaryWinCondition.VisitLocation("ashenveil_desert", "Ashenveil Desert"),
            baseExperience = 800,
            baseGold = 400,
            completionText = "The scorching sands have tested you. You survived."
        ),
        SecondaryMission(
            id = "sm_004",
            name = "Mountain Climber",
            description = "Reach the Frostpeak Mountains",
            category = SecondaryMissionCategory.EXPLORATION,
            winCondition = SecondaryWinCondition.VisitLocation("frostpeak_mountains", "Frostpeak Mountains"),
            baseExperience = 800,
            baseGold = 400,
            completionText = "The peaks bow before your determination."
        ),
        SecondaryMission(
            id = "sm_005",
            name = "Storm Chaser",
            description = "Venture into the Stormcoast Reaches",
            category = SecondaryMissionCategory.EXPLORATION,
            winCondition = SecondaryWinCondition.VisitLocation("stormcoast_reaches", "Stormcoast Reaches"),
            baseExperience = 1200,
            baseGold = 600,
            completionText = "Lightning and waves couldn't stop you."
        ),

        // COLLECTION MISSIONS (6-25)
        SecondaryMission(
            id = "sm_006",
            name = "Goblin Trophy Hunter",
            description = "Collect trophies from defeated goblins",
            category = SecondaryMissionCategory.COLLECTION,
            winCondition = SecondaryWinCondition.DefeatEnemy("Goblin", 20),
            baseExperience = 300,
            baseGold = 150,
            completionText = "Your trophy collection impresses even veteran goblin slayers."
        ),
        SecondaryMission(
            id = "sm_007",
            name = "Wolf Pack Hunter",
            description = "Hunt down wolf packs in the Heartlands",
            category = SecondaryMissionCategory.COLLECTION,
            winCondition = SecondaryWinCondition.DefeatEnemy("Wolf", 15),
            baseExperience = 250,
            baseGold = 125,
            completionText = "The wolves now fear your presence."
        ),
        SecondaryMission(
            id = "sm_008",
            name = "Spider Exterminator",
            description = "Clear out spider infestations",
            category = SecondaryMissionCategory.COLLECTION,
            winCondition = SecondaryWinCondition.DefeatEnemy("Giant Spider", 10),
            baseExperience = 400,
            baseGold = 200,
            completionText = "The webs are cleared. Travelers can pass safely now."
        ),
        SecondaryMission(
            id = "sm_009",
            name = "Undead Bane",
            description = "Destroy undead creatures in the Bone Canyons",
            category = SecondaryMissionCategory.COLLECTION,
            winCondition = SecondaryWinCondition.DefeatEnemy("Skeleton", 25),
            baseExperience = 600,
            baseGold = 300,
            completionText = "The dead return to their rest, thanks to you."
        ),
        SecondaryMission(
            id = "sm_010",
            name = "Slime Scientist",
            description = "A scholar needs slime samples for research",
            category = SecondaryMissionCategory.COLLECTION,
            winCondition = SecondaryWinCondition.DefeatEnemy("Slime", 30),
            baseExperience = 200,
            baseGold = 100,
            completionText = "The scholar thanks you. Apparently slimes are 'fascinating organisms.'"
        ),

        // COMBAT PROGRESSION (11-30)
        SecondaryMission(
            id = "sm_011",
            name = "Novice Warrior",
            description = "Prove yourself in early battles",
            category = SecondaryMissionCategory.COMBAT,
            winCondition = SecondaryWinCondition.ReachLevel(5),
            baseExperience = 150,
            baseGold = 75,
            completionText = "You're no longer a complete beginner. Barely."
        ),
        SecondaryMission(
            id = "sm_012",
            name = "Seasoned Adventurer",
            description = "Reach a respectable level of power",
            category = SecondaryMissionCategory.COMBAT,
            winCondition = SecondaryWinCondition.ReachLevel(10),
            baseExperience = 500,
            baseGold = 250,
            completionText = "Veterans start taking you seriously."
        ),
        SecondaryMission(
            id = "sm_013",
            name = "Veteran Fighter",
            description = "Achieve veteran status among adventurers",
            category = SecondaryMissionCategory.COMBAT,
            winCondition = SecondaryWinCondition.ReachLevel(20),
            baseExperience = 1000,
            baseGold = 500,
            completionText = "Rookie adventurers now look up to you."
        ),
        SecondaryMission(
            id = "sm_014",
            name = "Master Adventurer",
            description = "Become a master of your craft",
            category = SecondaryMissionCategory.COMBAT,
            winCondition = SecondaryWinCondition.ReachLevel(30),
            baseExperience = 2000,
            baseGold = 1000,
            completionText = "Your name is spoken with respect across Aethermoor."
        ),
        SecondaryMission(
            id = "sm_015",
            name = "Legendary Hero",
            description = "Achieve legendary status",
            category = SecondaryMissionCategory.COMBAT,
            winCondition = SecondaryWinCondition.ReachLevel(50),
            baseExperience = 5000,
            baseGold = 2500,
            completionText = "Songs will be sung of your deeds."
        ),

        // WEALTH & TRADING (16-35)
        SecondaryMission(
            id = "sm_016",
            name = "Small Fortune",
            description = "Accumulate a modest sum of gold",
            category = SecondaryMissionCategory.SOCIAL,
            winCondition = SecondaryWinCondition.AccumulateGold(1000),
            baseExperience = 200,
            baseGold = 100,
            completionText = "You're no longer penniless. Congratulations."
        ),
        SecondaryMission(
            id = "sm_017",
            name = "Wealthy Merchant",
            description = "Become quite wealthy",
            category = SecondaryMissionCategory.SOCIAL,
            winCondition = SecondaryWinCondition.AccumulateGold(5000),
            baseExperience = 500,
            baseGold = 250,
            completionText = "Money talks, and yours is shouting."
        ),
        SecondaryMission(
            id = "sm_018",
            name = "Treasure Hoarder",
            description = "Amass a dragon's hoard of gold",
            category = SecondaryMissionCategory.SOCIAL,
            winCondition = SecondaryWinCondition.AccumulateGold(10000),
            baseExperience = 1000,
            baseGold = 500,
            completionText = "You could retire. But you won't, will you?"
        ),

        // EQUIPMENT GOALS (19-40)
        SecondaryMission(
            id = "sm_019",
            name = "Uncommon Quality",
            description = "Equip an uncommon item",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.EquipRarity(Rarity.UNCOMMON),
            baseExperience = 100,
            baseGold = 50,
            completionText = "Better than the rusty starter gear, at least."
        ),
        SecondaryMission(
            id = "sm_020",
            name = "Rare Find",
            description = "Equip a rare quality item",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.EquipRarity(Rarity.RARE),
            baseExperience = 300,
            baseGold = 150,
            completionText = "Your equipment shines with quality."
        ),
        SecondaryMission(
            id = "sm_021",
            name = "Epic Gear",
            description = "Obtain and equip epic equipment",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.EquipRarity(Rarity.EPIC),
            baseExperience = 800,
            baseGold = 400,
            completionText = "Heroes across the land envy your gear."
        ),
        SecondaryMission(
            id = "sm_022",
            name = "Legendary Equipment",
            description = "Wield a legendary item",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.EquipRarity(Rarity.LEGENDARY),
            baseExperience = 2000,
            baseGold = 1000,
            completionText = "Legends are told of the weapon you wield."
        ),

        // STAT SPECIALIZATION (23-45)
        SecondaryMission(
            id = "sm_023",
            name = "Strength Training",
            description = "Become incredibly strong",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.UseStat(StatType.STRENGTH, 30),
            baseExperience = 600,
            baseGold = 300,
            completionText = "Your muscles have muscles."
        ),
        SecondaryMission(
            id = "sm_024",
            name = "Arcane Mastery",
            description = "Master the arcane arts",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.UseStat(StatType.INTELLIGENCE, 30),
            baseExperience = 600,
            baseGold = 300,
            completionText = "Magic bends to your will."
        ),
        SecondaryMission(
            id = "sm_025",
            name = "Lightning Reflexes",
            description = "Become incredibly agile",
            category = SecondaryMissionCategory.ACHIEVEMENT,
            winCondition = SecondaryWinCondition.UseStat(StatType.AGILITY, 30),
            baseExperience = 600,
            baseGold = 300,
            completionText = "You're a blur of motion."
        ),

        // FUN/HUMOROUS MISSIONS (26-60)
        SecondaryMission(
            id = "sm_026",
            name = "Lucky Streak",
            description = "Build up incredible luck",
            category = SecondaryMissionCategory.LUCK,
            winCondition = SecondaryWinCondition.UseStat(StatType.LUCK, 25),
            baseExperience = 500,
            baseGold = 250,
            completionText = "Fortune smiles upon you. Constantly."
        ),
        SecondaryMission(
            id = "sm_027",
            name = "Critical Success",
            description = "Land multiple critical hits",
            category = SecondaryMissionCategory.LUCK,
            winCondition = SecondaryWinCondition.CriticalHits(10),
            baseExperience = 400,
            baseGold = 200,
            completionText = "Your enemies fear your devastating strikes."
        ),
        SecondaryMission(
            id = "sm_028",
            name = "Critical Master",
            description = "Become a critical hit expert",
            category = SecondaryMissionCategory.LUCK,
            winCondition = SecondaryWinCondition.CriticalHits(50),
            baseExperience = 1000,
            baseGold = 500,
            completionText = "Every strike finds the perfect spot."
        ),
        SecondaryMission(
            id = "sm_029",
            name = "Social Butterfly",
            description = "Trade with many merchants",
            category = SecondaryMissionCategory.SOCIAL,
            winCondition = SecondaryWinCondition.TradeWithNPC("Merchant", 10),
            baseExperience = 300,
            baseGold = 150,
            completionText = "Merchants know you by name now."
        ),
        SecondaryMission(
            id = "sm_030",
            name = "Survivor",
            description = "Survive for multiple days on Aethermoor",
            category = SecondaryMissionCategory.SURVIVAL,
            winCondition = SecondaryWinCondition.SurviveDays(7),
            baseExperience = 200,
            baseGold = 100,
            completionText = "You've lasted a full week. Not bad."
        ),

        // Additional missions to reach 100 (abbreviated for space)
        // Using templates for the remaining 70 missions
        *generateExplorationMissions(31, 40),
        *generateCombatMissions(41, 50),
        *generateCollectionMissions(51, 65),
        *generateSocialMissions(66, 75),
        *generateLuckMissions(76, 85),
        *generateSurvivalMissions(86, 95),
        *generateAchievementMissions(96, 100)
    )

    private fun generateExplorationMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        val locations = listOf(
            Triple("thornwood_eldergrove_ruins", "Eldergrove Ruins", 400),
            Triple("thornwood_moonwell_glade", "Moonwell Glade", 450),
            Triple("ashenveil_bone_canyons", "Bone Canyons", 700),
            Triple("ashenveil_mirage_springs", "Mirage Springs", 650),
            Triple("frostpeak_irondelve_hold", "Irondelve Hold", 900),
            Triple("frostpeak_sky_monastery", "Sky Monastery", 950),
            Triple("stormcoast_wreckers_cove", "Wrecker's Cove", 1100),
            Triple("stormcoast_drowned_cathedral", "Drowned Cathedral", 1150),
            Triple("stormcoast_lighthouse_point", "Lighthouse Point", 1000),
            Triple("heartlands_broken_bridge", "Broken Bridge", 150)
        )

        return locations.take(endId - startId + 1).mapIndexed { index, (locId, locName, gold) ->
            SecondaryMission(
                id = "sm_%03d".format(startId + index),
                name = "Discover $locName",
                description = "Find and explore the $locName",
                category = SecondaryMissionCategory.EXPLORATION,
                winCondition = SecondaryWinCondition.VisitLocation(locId, locName),
                baseExperience = (gold * 1.5).toLong(),
                baseGold = gold,
                completionText = "The $locName revealed its secrets to you."
            )
        }.toTypedArray()
    }

    private fun generateCombatMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        val enemies = listOf(
            Triple("Bandit", 12, 180),
            Triple("Troll", 8, 320),
            Triple("Orc", 15, 225),
            Triple("Dire Wolf", 10, 300),
            Triple("Treant", 5, 500),
            Triple("Elemental", 6, 600),
            Triple("Dragon Whelp", 3, 800),
            Triple("Frost Giant", 4, 900),
            Triple("Ghost Pirate", 7, 700),
            Triple("Sandworm", 2, 1000)
        )

        return enemies.take(endId - startId + 1).mapIndexed { index, (enemy, count, gold) ->
            SecondaryMission(
                id = "sm_%03d".format(startId + index),
                name = "$enemy Slayer",
                description = "Defeat multiple ${enemy}s",
                category = SecondaryMissionCategory.COMBAT,
                winCondition = SecondaryWinCondition.DefeatEnemy(enemy, count),
                baseExperience = (gold * 2).toLong(),
                baseGold = gold,
                completionText = "${enemy}s now fear your name."
            )
        }.toTypedArray()
    }

    private fun generateCollectionMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        return (startId..endId).map { id ->
            val itemTypes = listOf("Potion", "Scroll", "Gem", "Ore", "Herb", "Relic", "Crystal", "Essence", "Rune", "Artifact", "Treasure", "Token", "Fragment", "Shard", "Core")
            val itemType = itemTypes[id % itemTypes.size]
            SecondaryMission(
                id = "sm_%03d".format(id),
                name = "$itemType Collector",
                description = "Collect rare ${itemType}s",
                category = SecondaryMissionCategory.COLLECTION,
                winCondition = SecondaryWinCondition.CollectItem("${itemType.lowercase()}_common", itemType, 5 + (id % 10)),
                baseExperience = (100 + id * 10).toLong(),
                baseGold = 50 + id * 5,
                completionText = "Your collection of ${itemType}s is impressive."
            )
        }.toTypedArray()
    }

    private fun generateSocialMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        return (startId..endId).map { id ->
            val npcTypes = listOf("Merchant", "Innkeeper", "Blacksmith", "Alchemist", "Scholar", "Guard", "Priest", "Wizard", "Ranger", "Miner")
            val npcType = npcTypes[id % npcTypes.size]
            SecondaryMission(
                id = "sm_%03d".format(id),
                name = "$npcType Regular",
                description = "Build a relationship with ${npcType}s",
                category = SecondaryMissionCategory.SOCIAL,
                winCondition = SecondaryWinCondition.TradeWithNPC(npcType, 3 + (id % 5)),
                baseExperience = (150 + id * 15).toLong(),
                baseGold = 75 + id * 8,
                completionText = "${npcType}s greet you warmly now."
            )
        }.toTypedArray()
    }

    private fun generateLuckMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        return (startId..endId).map { id ->
            val critCount = 20 + (id - startId) * 10
            SecondaryMission(
                id = "sm_%03d".format(id),
                name = "Critical Hit Master ${id - startId + 1}",
                description = "Land $critCount critical hits",
                category = SecondaryMissionCategory.LUCK,
                winCondition = SecondaryWinCondition.CriticalHits(critCount),
                baseExperience = (critCount * 15).toLong(),
                baseGold = critCount * 8,
                completionText = "Your critical hit streak is legendary."
            )
        }.toTypedArray()
    }

    private fun generateSurvivalMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        return (startId..endId).map { id ->
            val days = 10 + (id - startId) * 3
            SecondaryMission(
                id = "sm_%03d".format(id),
                name = "Survive $days Days",
                description = "Stay alive on Aethermoor for $days days",
                category = SecondaryMissionCategory.SURVIVAL,
                winCondition = SecondaryWinCondition.SurviveDays(days),
                baseExperience = (days * 50).toLong(),
                baseGold = days * 25,
                completionText = "You've survived $days days on this cursed island."
            )
        }.toTypedArray()
    }

    private fun generateAchievementMissions(startId: Int, endId: Int): Array<SecondaryMission> {
        return (startId..endId).map { id ->
            val level = 15 + (id - startId) * 5
            SecondaryMission(
                id = "sm_%03d".format(id),
                name = "Level $level Achievement",
                description = "Reach level $level",
                category = SecondaryMissionCategory.ACHIEVEMENT,
                winCondition = SecondaryWinCondition.ReachLevel(level),
                baseExperience = (level * 100).toLong(),
                baseGold = level * 50,
                completionText = "Level $level achieved. Power grows within you."
            )
        }.toTypedArray()
    }

    /**
     * Get all 100 secondary missions
     */
    fun getAllSecondaryMissions(): List<SecondaryMission> {
        return missions
    }

    /**
     * Get a random secondary mission that hasn't been completed
     */
    fun getRandomMission(excludeIds: Set<String> = emptySet()): SecondaryMission? {
        return missions.filterNot { it.id in excludeIds }.randomOrNull()
    }

    /**
     * Get mission by ID
     */
    fun getMissionById(id: String): SecondaryMission? {
        return missions.firstOrNull { it.id == id }
    }

    /**
     * Get missions by category
     */
    fun getMissionsByCategory(category: SecondaryMissionCategory): List<SecondaryMission> {
        return missions.filter { it.category == category }
    }
}

package com.watxaut.rolenonplayinggame.data

import com.watxaut.rolenonplayinggame.domain.model.Achievement
import com.watxaut.rolenonplayinggame.domain.model.AchievementCategory
import com.watxaut.rolenonplayinggame.domain.model.AchievementRequirement
import com.watxaut.rolenonplayinggame.domain.model.AchievementTier
import com.watxaut.rolenonplayinggame.domain.model.ItemRarity

/**
 * Achievement Database for Aethermoor.
 * Based on TONE_AND_STYLE_GUIDE.md - Achievement naming conventions
 *
 * Naming pattern: [Action] + [Context/Flavor] + [Tier indicator if needed]
 * Examples: "Slime Slayer", "Explorer Extraordinaire", "Legendary Warrior"
 *
 * Phase 3 - World & Content Implementation
 */
object AchievementDatabase {

    private val achievements = listOf(
        // ==================== PROGRESSION ACHIEVEMENTS ====================
        Achievement(
            id = "ach_first_steps",
            name = "First Steps",
            description = "Reach level 5",
            category = AchievementCategory.PROGRESSION,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.ReachLevel(5),
            flavorText = "Every journey begins somewhere."
        ),
        Achievement(
            id = "ach_seasoned_adventurer",
            name = "Seasoned Adventurer",
            description = "Reach level 20",
            category = AchievementCategory.PROGRESSION,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.ReachLevel(20),
            flavorText = "You've seen some things."
        ),
        Achievement(
            id = "ach_veteran_hero",
            name = "Veteran Hero",
            description = "Reach level 35",
            category = AchievementCategory.PROGRESSION,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.ReachLevel(35),
            flavorText = "Few reach this pinnacle."
        ),
        Achievement(
            id = "ach_legend_of_aethermoor",
            name = "Legend of Aethermoor",
            description = "Reach level 50",
            category = AchievementCategory.PROGRESSION,
            tier = AchievementTier.DIAMOND,
            requirements = AchievementRequirement.ReachLevel(50),
            flavorText = "Your name will be remembered forever."
        ),

        // ==================== COMBAT ACHIEVEMENTS ====================
        Achievement(
            id = "ach_slime_slayer",
            name = "Slime Slayer",
            description = "Defeat 100 slimes",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.KillCount("Slime", 100),
            flavorText = "They never saw it coming. Because they don't have eyes."
        ),
        Achievement(
            id = "ach_goblin_hunter",
            name = "Goblin Hunter",
            description = "Defeat 50 goblins",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.KillCount("Goblin", 50)
        ),
        Achievement(
            id = "ach_wolf_pack_decimator",
            name = "Wolf Pack Decimator",
            description = "Defeat 100 wolves",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.KillCount("Wolf", 100)
        ),
        Achievement(
            id = "ach_undead_slayer",
            name = "Undead Slayer",
            description = "Defeat 200 undead enemies",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.KillCount("Undead", 200),
            flavorText = "They were already dead. You just made it permanent."
        ),
        Achievement(
            id = "ach_dragon_slayer",
            name = "Dragonslayer",
            description = "Defeat Hoarfang the Glacier Drake",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.PLATINUM,
            requirements = AchievementRequirement.DefeatBoss("Hoarfang"),
            flavorText = "Where dragons fall, legends rise."
        ),
        Achievement(
            id = "ach_storm_breaker",
            name = "Storm Breaker",
            description = "Defeat Tempest, the Storm Leviathan",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.DIAMOND,
            requirements = AchievementRequirement.DefeatBoss("Tempest"),
            flavorText = "The storm has been broken. Aethermoor may finally know peace."
        ),
        Achievement(
            id = "ach_boss_hunter",
            name = "Boss Hunter",
            description = "Defeat any 5 boss enemies",
            category = AchievementCategory.COMBAT,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.Custom("bosses_defeated", 5)
        ),

        // ==================== EXPLORATION ACHIEVEMENTS ====================
        Achievement(
            id = "ach_explorer",
            name = "Explorer",
            description = "Discover 5 locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.DiscoverLocations(5)
        ),
        Achievement(
            id = "ach_cartographer",
            name = "Cartographer",
            description = "Discover 15 locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.DiscoverLocations(15),
            flavorText = "You're mapping the unmappable."
        ),
        Achievement(
            id = "ach_master_explorer",
            name = "Master Explorer",
            description = "Discover all locations on Aethermoor",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.PLATINUM,
            requirements = AchievementRequirement.DiscoverLocations(26), // Total locations
            flavorText = "No corner of Aethermoor remains unknown to you."
        ),
        Achievement(
            id = "ach_heartlands_hero",
            name = "Heartlands Hero",
            description = "Discover all Heartlands locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.Custom("heartlands_explored", 6)
        ),
        Achievement(
            id = "ach_thornwood_pathfinder",
            name = "Thornwood Pathfinder",
            description = "Discover all Thornwood Wilds locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.Custom("thornwood_explored", 5)
        ),
        Achievement(
            id = "ach_desert_wanderer",
            name = "Desert Wanderer",
            description = "Discover all Ashenveil Desert locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.Custom("ashenveil_explored", 5)
        ),
        Achievement(
            id = "ach_peak_conqueror",
            name = "Peak Conqueror",
            description = "Discover all Frostpeak Mountains locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.Custom("frostpeak_explored", 5)
        ),
        Achievement(
            id = "ach_storm_braver",
            name = "Storm Braver",
            description = "Discover all Stormcoast Reaches locations",
            category = AchievementCategory.EXPLORATION,
            tier = AchievementTier.PLATINUM,
            requirements = AchievementRequirement.Custom("stormcoast_explored", 5)
        ),

        // ==================== WEALTH ACHIEVEMENTS ====================
        Achievement(
            id = "ach_first_fortune",
            name = "First Fortune",
            description = "Accumulate 1,000 gold",
            category = AchievementCategory.WEALTH,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.GoldEarned(1000)
        ),
        Achievement(
            id = "ach_merchant_prince",
            name = "Merchant Prince",
            description = "Accumulate 10,000 gold",
            category = AchievementCategory.WEALTH,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.GoldEarned(10000)
        ),
        Achievement(
            id = "ach_treasure_hoarder",
            name = "Treasure Hoarder",
            description = "Accumulate 50,000 gold",
            category = AchievementCategory.WEALTH,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.GoldEarned(50000),
            flavorText = "Dragons would be jealous."
        ),
        Achievement(
            id = "ach_king_of_coin",
            name = "King of Coin",
            description = "Accumulate 100,000 gold",
            category = AchievementCategory.WEALTH,
            tier = AchievementTier.PLATINUM,
            requirements = AchievementRequirement.GoldEarned(100000),
            flavorText = "Your wealth is legendary."
        ),

        // ==================== COLLECTION ACHIEVEMENTS ====================
        Achievement(
            id = "ach_rare_collector",
            name = "Rare Collector",
            description = "Collect 5 Rare items",
            category = AchievementCategory.COLLECTION,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.CollectItems(ItemRarity.RARE, 5)
        ),
        Achievement(
            id = "ach_epic_hoarder",
            name = "Epic Hoarder",
            description = "Collect 3 Epic items",
            category = AchievementCategory.COLLECTION,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.CollectItems(ItemRarity.EPIC, 3)
        ),
        Achievement(
            id = "ach_legend_collector",
            name = "Legend Collector",
            description = "Collect a Legendary item",
            category = AchievementCategory.COLLECTION,
            tier = AchievementTier.PLATINUM,
            requirements = AchievementRequirement.CollectItems(ItemRarity.LEGENDARY, 1),
            flavorText = "History will remember this moment."
        ),

        // ==================== QUEST ACHIEVEMENTS ====================
        Achievement(
            id = "ach_quest_beginner",
            name = "Quest Beginner",
            description = "Complete 5 quests",
            category = AchievementCategory.QUEST,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.CompleteQuests(5)
        ),
        Achievement(
            id = "ach_quest_adept",
            name = "Quest Adept",
            description = "Complete 20 quests",
            category = AchievementCategory.QUEST,
            tier = AchievementTier.SILVER,
            requirements = AchievementRequirement.CompleteQuests(20)
        ),
        Achievement(
            id = "ach_quest_master",
            name = "Quest Master",
            description = "Complete 50 quests",
            category = AchievementCategory.QUEST,
            tier = AchievementTier.GOLD,
            requirements = AchievementRequirement.CompleteQuests(50),
            flavorText = "The island's problems are your specialty."
        ),
        Achievement(
            id = "ach_savior_of_aethermoor",
            name = "Savior of Aethermoor",
            description = "Complete all main story quests",
            category = AchievementCategory.QUEST,
            tier = AchievementTier.DIAMOND,
            requirements = AchievementRequirement.Custom("main_quests_complete", 12)
        ),

        // ==================== LEGENDARY ACHIEVEMENTS ====================
        Achievement(
            id = "ach_immortal",
            name = "Immortal",
            description = "Reach level 50 without dying",
            category = AchievementCategory.LEGENDARY,
            tier = AchievementTier.DIAMOND,
            requirements = AchievementRequirement.Custom("deaths_at_50", 0),
            flavorText = "Death itself fears you."
        ),
        Achievement(
            id = "ach_wanderer_reborn",
            name = "Wanderer Reborn",
            description = "Find the First Wanderer's Compass",
            category = AchievementCategory.LEGENDARY,
            tier = AchievementTier.DIAMOND,
            requirements = AchievementRequirement.Custom("first_wanderer_compass", 1),
            flavorText = "You walk the path of legends."
        ),
        Achievement(
            id = "ach_unfortunate",
            name = "Unfortunate Soul",
            description = "Die 100 times",
            category = AchievementCategory.LEGENDARY,
            tier = AchievementTier.BRONZE,
            requirements = AchievementRequirement.Die(100),
            flavorText = "At least you're persistent."
        )
    )

    /**
     * Get all achievements
     */
    fun getAllAchievements(): List<Achievement> = achievements

    /**
     * Get achievement by ID
     */
    fun getAchievementById(id: String): Achievement? {
        return achievements.find { it.id == id }
    }

    /**
     * Get achievements by category
     */
    fun getAchievementsByCategory(category: AchievementCategory): List<Achievement> {
        return achievements.filter { it.category == category }
    }

    /**
     * Get achievements by tier
     */
    fun getAchievementsByTier(tier: AchievementTier): List<Achievement> {
        return achievements.filter { it.tier == tier }
    }

    /**
     * Get legendary achievements
     */
    fun getLegendaryAchievements(): List<Achievement> {
        return achievements.filter { it.category == AchievementCategory.LEGENDARY }
    }
}

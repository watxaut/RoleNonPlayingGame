package com.watxaut.rolenonplayinggame.domain.model

/**
 * Achievement model for tracking player milestones.
 * Based on TONE_AND_STYLE_GUIDE.md - Achievement naming conventions
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val requirements: AchievementRequirement,
    val iconId: String? = null,
    val flavorText: String? = null
)

enum class AchievementCategory {
    COMBAT,        // Combat-related achievements
    EXPLORATION,   // Discovery and travel
    PROGRESSION,   // Leveling and growth
    WEALTH,        // Gold and treasure
    COLLECTION,    // Item collection
    QUEST,         // Quest completion
    SOCIAL,        // Multiplayer interactions
    LEGENDARY      // Legendary accomplishments
}

enum class AchievementTier {
    BRONZE,    // Common achievements
    SILVER,    // Uncommon achievements
    GOLD,      // Rare achievements
    PLATINUM,  // Epic achievements
    DIAMOND    // Legendary achievements
}

/**
 * Requirements for unlocking an achievement
 */
sealed class AchievementRequirement {
    data class KillCount(val enemyName: String?, val count: Int) : AchievementRequirement()
    data class ReachLevel(val level: Int) : AchievementRequirement()
    data class GoldEarned(val amount: Long) : AchievementRequirement()
    data class DiscoverLocations(val count: Int) : AchievementRequirement()
    data class CompleteQuests(val count: Int) : AchievementRequirement()
    data class DefeatBoss(val bossName: String) : AchievementRequirement()
    data class CollectItems(val rarity: ItemRarity?, val count: Int) : AchievementRequirement()
    data class Die(val count: Int) : AchievementRequirement()
    data class Custom(val key: String, val threshold: Int) : AchievementRequirement()
}

/**
 * Active achievement progress for a character
 */
data class UnlockedAchievement(
    val achievementId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

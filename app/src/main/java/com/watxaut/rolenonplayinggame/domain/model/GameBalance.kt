package com.watxaut.rolenonplayinggame.domain.model

/**
 * GameBalance - Centralized configuration for all game chances and probabilities.
 * Per user requirements:
 * - Single place to manage all RNG chances for easy balancing
 * - Includes mission discovery, loot drops, combat outcomes, etc.
 *
 * All values are percentages (0.0 to 1.0 range) unless otherwise noted.
 */
object GameBalance {

    // ========================================
    // MISSION SYSTEM CHANCES
    // ========================================

    /**
     * Chance to discover a principal mission step during exploration
     * User requirement: 2%
     */
    const val PRINCIPAL_MISSION_STEP_DISCOVERY_CHANCE = 0.02f

    /**
     * Chance to encounter a principal mission boss (after all steps complete)
     * User requirement: 2%
     */
    const val PRINCIPAL_MISSION_BOSS_ENCOUNTER_CHANCE = 0.02f

    /**
     * Chance to discover a secondary mission during any action
     * User requirement: 1%
     */
    const val SECONDARY_MISSION_DISCOVERY_CHANCE = 0.01f

    /**
     * Chance for a secondary mission to reward equipment
     * User requirement: 20%
     */
    const val SECONDARY_MISSION_EQUIPMENT_REWARD_CHANCE = 0.20f

    /**
     * Chance for a secondary mission to reward rare equipment
     * User requirement: 2%
     */
    const val SECONDARY_MISSION_RARE_EQUIPMENT_CHANCE = 0.02f

    // ========================================
    // COMBAT LOOT CHANCES
    // ========================================

    /**
     * Base chance for a regular monster to drop loot
     */
    const val MONSTER_LOOT_DROP_CHANCE = 0.30f // 30%

    /**
     * Chance for monster loot to be common equipment
     */
    const val MONSTER_COMMON_EQUIPMENT_CHANCE = 0.15f // 15%

    /**
     * Chance for monster loot to be uncommon equipment
     */
    const val MONSTER_UNCOMMON_EQUIPMENT_CHANCE = 0.08f // 8%

    /**
     * Chance for monster loot to be rare equipment
     */
    const val MONSTER_RARE_EQUIPMENT_CHANCE = 0.02f // 2%

    /**
     * Bonus loot chance for elite monsters
     */
    const val ELITE_MONSTER_BONUS_LOOT_MULTIPLIER = 2.0f

    /**
     * Bonus loot chance for boss monsters
     */
    const val BOSS_MONSTER_BONUS_LOOT_MULTIPLIER = 3.0f

    /**
     * Guaranteed rare loot from world bosses
     */
    const val WORLD_BOSS_RARE_LOOT_GUARANTEE = true

    // ========================================
    // EXPLORATION & DISCOVERY CHANCES
    // ========================================

    /**
     * Chance to find treasure chest during exploration
     */
    const val TREASURE_CHEST_DISCOVERY_CHANCE = 0.05f // 5%

    /**
     * Chance for treasure chest to contain rare loot
     */
    const val TREASURE_CHEST_RARE_LOOT_CHANCE = 0.10f // 10%

    /**
     * Chance to discover a new location while exploring
     */
    const val NEW_LOCATION_DISCOVERY_CHANCE = 0.15f // 15%

    /**
     * Chance to encounter a random event during exploration
     */
    const val RANDOM_EVENT_CHANCE = 0.08f // 8%

    // ========================================
    // COMBAT CHANCES
    // ========================================

    /**
     * Base critical hit chance (modified by LUCK stat)
     * Natural 21 on d21 is automatic critical
     */
    const val BASE_CRITICAL_HIT_CHANCE = 0.05f // 5% base

    /**
     * Critical hit chance increase per LUCK point
     */
    const val LUCK_CRITICAL_HIT_BONUS = 0.01f // 1% per LUCK

    /**
     * Chance to dodge an attack (modified by AGILITY stat)
     */
    const val BASE_DODGE_CHANCE = 0.05f // 5% base

    /**
     * Dodge chance increase per AGILITY point
     */
    const val AGILITY_DODGE_BONUS = 0.01f // 1% per AGI

    /**
     * Chance for a character to flee from combat when health is low
     */
    const val FLEE_ATTEMPT_SUCCESS_BASE = 0.50f // 50% base

    // ========================================
    // DICE ROLL SYSTEM (d21)
    // ========================================

    /**
     * Luck reroll chance for rolling a natural 1
     * Per FUNCTIONAL_DESIGN_DOCUMENT.md: +1% per 5 LUK
     */
    fun getLuckRerollChance(luckStat: Int): Float {
        return (luckStat / 5) * 0.01f
    }

    /**
     * Expanded critical range for high LUCK
     * Per FUNCTIONAL_DESIGN_DOCUMENT.md: at 20 LUK, rolls of 20-21 are both crits
     */
    fun getCriticalRange(luckStat: Int): IntRange {
        val expansion = luckStat / 10
        return (21 - expansion)..21
    }

    // ========================================
    // MERCHANT & TRADING CHANCES
    // ========================================

    /**
     * Chance to encounter a traveling merchant
     */
    const val TRAVELING_MERCHANT_ENCOUNTER_CHANCE = 0.03f // 3%

    /**
     * Discount multiplier based on CHARISMA
     * Higher charisma = better prices
     */
    fun getMerchantDiscountMultiplier(charismaStat: Int): Float {
        return 1.0f - (charismaStat * 0.01f).coerceAtMost(0.30f) // Max 30% discount
    }

    // ========================================
    // NPC INTERACTION CHANCES
    // ========================================

    /**
     * Base chance for successful NPC interaction (modified by CHARISMA)
     */
    const val BASE_NPC_INTERACTION_SUCCESS = 0.50f // 50%

    /**
     * Charisma bonus to NPC interactions
     */
    const val CHARISMA_NPC_INTERACTION_BONUS = 0.02f // 2% per CHA

    // ========================================
    // ITEM QUALITY & RARITY DISTRIBUTION
    // ========================================

    /**
     * Rarity distribution for randomly generated items
     */
    object RarityDistribution {
        const val COMMON_CHANCE = 0.60f      // 60%
        const val UNCOMMON_CHANCE = 0.30f    // 30%
        const val RARE_CHANCE = 0.08f        // 8%
        const val EPIC_CHANCE = 0.015f       // 1.5%
        const val LEGENDARY_CHANCE = 0.005f  // 0.5%

        /**
         * Get rarity based on roll (0.0 to 1.0)
         */
        fun getRarityFromRoll(roll: Float): Rarity {
            return when {
                roll < LEGENDARY_CHANCE -> Rarity.LEGENDARY
                roll < LEGENDARY_CHANCE + EPIC_CHANCE -> Rarity.EPIC
                roll < LEGENDARY_CHANCE + EPIC_CHANCE + RARE_CHANCE -> Rarity.RARE
                roll < LEGENDARY_CHANCE + EPIC_CHANCE + RARE_CHANCE + UNCOMMON_CHANCE -> Rarity.UNCOMMON
                else -> Rarity.COMMON
            }
        }
    }

    // ========================================
    // LUCK MODIFIERS
    // ========================================

    /**
     * Luck modifier for loot quality
     * Higher luck = better loot quality
     */
    fun getLootQualityBonus(luckStat: Int): Float {
        return luckStat * 0.005f // 0.5% per LUCK point
    }

    // ========================================
    // SPECIAL EVENT CHANCES
    // ========================================

    /**
     * Chance to trigger a special rare event
     */
    const val RARE_EVENT_CHANCE = 0.01f // 1%

    /**
     * Chance to find a legendary item in the world (extremely rare)
     */
    const val LEGENDARY_ITEM_WORLD_FIND_CHANCE = 0.001f // 0.1%

    // ========================================
    // QUEST SYSTEM CHANCES
    // ========================================

    /**
     * Chance for an NPC to offer a quest
     */
    const val NPC_QUEST_OFFER_CHANCE = 0.25f // 25%

    /**
     * Chance for a quest to have bonus rewards
     */
    const val QUEST_BONUS_REWARD_CHANCE = 0.15f // 15%

    // ========================================
    // DEATH & RESPAWN
    // ========================================

    /**
     * Percentage of gold lost on death
     */
    const val DEATH_GOLD_LOSS_PERCENTAGE = 0.10f // 10%

    /**
     * Chance to keep an item on death (otherwise random items may be lost)
     */
    const val DEATH_ITEM_RETENTION_CHANCE = 0.90f // 90% chance to keep items

    // ========================================
    // LEVEL SCALING
    // ========================================

    /**
     * Experience multiplier for killing higher-level enemies
     */
    fun getExpMultiplierForLevelDifference(levelDiff: Int): Float {
        return when {
            levelDiff >= 5 -> 2.0f  // 200% XP for enemies 5+ levels higher
            levelDiff >= 3 -> 1.5f  // 150% XP for enemies 3-4 levels higher
            levelDiff >= 1 -> 1.25f // 125% XP for enemies 1-2 levels higher
            levelDiff <= -3 -> 0.5f // 50% XP for enemies 3+ levels lower
            levelDiff <= -1 -> 0.75f // 75% XP for enemies 1-2 levels lower
            else -> 1.0f // 100% XP for same level
        }
    }

    // ========================================
    // BALANCING NOTES
    // ========================================

    /**
     * Guidelines for adjusting chances:
     *
     * TOO EASY (increase difficulty):
     * - Decrease MONSTER_LOOT_DROP_CHANCE
     * - Decrease TREASURE_CHEST_DISCOVERY_CHANCE
     * - Decrease SECONDARY_MISSION_EQUIPMENT_REWARD_CHANCE
     *
     * TOO HARD (decrease difficulty):
     * - Increase MONSTER_LOOT_DROP_CHANCE
     * - Increase BASE_NPC_INTERACTION_SUCCESS
     * - Increase QUEST_BONUS_REWARD_CHANCE
     *
     * MISSIONS TOO SLOW:
     * - Increase PRINCIPAL_MISSION_STEP_DISCOVERY_CHANCE
     * - Increase PRINCIPAL_MISSION_BOSS_ENCOUNTER_CHANCE
     * - Increase SECONDARY_MISSION_DISCOVERY_CHANCE
     *
     * LOOT TOO COMMON/RARE:
     * - Adjust RarityDistribution percentages
     * - Adjust MONSTER_*_EQUIPMENT_CHANCE values
     */
}

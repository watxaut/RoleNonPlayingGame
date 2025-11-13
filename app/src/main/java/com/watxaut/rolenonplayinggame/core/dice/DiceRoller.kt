package com.watxaut.rolenonplayinggame.core.dice

import kotlin.random.Random

/**
 * Core d21 dice system for the game.
 * All uncertain actions use a 21-sided die roll system:
 * - Roll 21 = automatic critical success
 * - Roll 1 = automatic critical failure
 * - Rolls 2-20 = success/failure based on: Difficulty - Stat - Roll ≤ 0
 */
class DiceRoller(private val random: Random = Random.Default) {

    /**
     * Roll a d21 die (returns 1-21)
     */
    fun roll(): Int = random.nextInt(1, 22)

    /**
     * Roll with a specific stat and difficulty.
     * @param stat Character's relevant stat value
     * @param difficulty The difficulty of the action
     * @return RollResult containing the roll value and whether it succeeded
     */
    fun rollWithStat(stat: Int, difficulty: Int): RollResult {
        val rollValue = roll()

        val success = when (rollValue) {
            21 -> true  // Critical success
            1 -> false  // Critical failure
            else -> (difficulty - stat - rollValue) <= 0
        }

        val isCritical = rollValue == 21 || rollValue == 1

        return RollResult(
            value = rollValue,
            success = success,
            isCriticalSuccess = rollValue == 21,
            isCriticalFailure = rollValue == 1
        )
    }

    /**
     * Roll with luck stat special effects:
     * - On roll of 1: Can reroll once if luck >= 10
     * - Expanded crit range: Crit on 21, or (21 - luck/5) if luck >= 15
     */
    fun rollWithLuck(stat: Int, difficulty: Int, luckStat: Int): RollResult {
        var rollValue = roll()
        var wasRerolled = false

        // Handle luck reroll on critical failure
        if (rollValue == 1 && luckStat >= 10) {
            rollValue = roll()
            wasRerolled = true
        }

        // Calculate if it's a critical success with expanded range
        val critThreshold = if (luckStat >= 15) {
            21 - (luckStat / 5)
        } else {
            21
        }

        val isCriticalSuccess = rollValue >= critThreshold
        val isCriticalFailure = rollValue == 1 && !wasRerolled

        val success = when {
            isCriticalSuccess -> true
            isCriticalFailure -> false
            else -> (difficulty - stat - rollValue) <= 0
        }

        return RollResult(
            value = rollValue,
            success = success,
            isCriticalSuccess = isCriticalSuccess,
            isCriticalFailure = isCriticalFailure,
            wasRerolled = wasRerolled
        )
    }

    /**
     * Roll multiple dice and return the results
     */
    fun rollMultiple(count: Int): List<Int> {
        return (1..count).map { roll() }
    }

    /**
     * Roll and take the best of N rolls
     */
    fun rollWithAdvantage(count: Int = 2): Int {
        return rollMultiple(count).maxOrNull() ?: roll()
    }

    /**
     * Roll and take the worst of N rolls
     */
    fun rollWithDisadvantage(count: Int = 2): Int {
        return rollMultiple(count).minOrNull() ?: roll()
    }
}

/**
 * Result of a dice roll
 */
data class RollResult(
    val value: Int,
    val success: Boolean,
    val isCriticalSuccess: Boolean = false,
    val isCriticalFailure: Boolean = false,
    val wasRerolled: Boolean = false
)

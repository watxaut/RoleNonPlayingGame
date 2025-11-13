package com.watxaut.rolenonplayinggame.domain.model

import kotlin.random.Random

/**
 * Hidden personality traits that influence character decisions.
 * Values range from 0.0 to 1.0
 */
data class PersonalityTraits(
    val courage: Float,      // 0.0 = cautious, 1.0 = brave/reckless
    val greed: Float,        // 0.0 = generous, 1.0 = greedy
    val curiosity: Float,    // 0.0 = focused, 1.0 = curious/exploratory
    val aggression: Float,   // 0.0 = peaceful, 1.0 = aggressive
    val social: Float,       // 0.0 = independent, 1.0 = social
    val impulsive: Float     // 0.0 = methodical, 1.0 = impulsive
) {
    init {
        require(courage in 0f..1f) { "Courage must be between 0 and 1" }
        require(greed in 0f..1f) { "Greed must be between 0 and 1" }
        require(curiosity in 0f..1f) { "Curiosity must be between 0 and 1" }
        require(aggression in 0f..1f) { "Aggression must be between 0 and 1" }
        require(social in 0f..1f) { "Social must be between 0 and 1" }
        require(impulsive in 0f..1f) { "Impulsive must be between 0 and 1" }
    }

    /**
     * Calculate risk tolerance (used in decision-making)
     * Higher values = more willing to take risks
     */
    fun getRiskTolerance(): Float {
        return (courage + impulsive - 0.5f * (1f - greed)) / 2.5f
    }

    /**
     * Calculate social compatibility with another character
     */
    fun getSocialCompatibility(other: PersonalityTraits): Float {
        // Similar personalities get along better
        val differences = listOf(
            kotlin.math.abs(courage - other.courage),
            kotlin.math.abs(greed - other.greed),
            kotlin.math.abs(curiosity - other.curiosity),
            kotlin.math.abs(aggression - other.aggression),
            kotlin.math.abs(social - other.social),
            kotlin.math.abs(impulsive - other.impulsive)
        )

        val avgDifference = differences.average().toFloat()
        return 1f - avgDifference
    }

    companion object {
        /**
         * Generate random personality traits
         */
        fun random(random: Random = Random.Default): PersonalityTraits {
            return PersonalityTraits(
                courage = random.nextFloat(),
                greed = random.nextFloat(),
                curiosity = random.nextFloat(),
                aggression = random.nextFloat(),
                social = random.nextFloat(),
                impulsive = random.nextFloat()
            )
        }

        /**
         * Generate personality traits influenced by job class
         */
        fun forJobClass(jobClass: JobClass, random: Random = Random.Default): PersonalityTraits {
            val base = random()

            return when (jobClass) {
                JobClass.WARRIOR -> base.copy(
                    courage = (base.courage + 0.3f).coerceAtMost(1f),
                    aggression = (base.aggression + 0.2f).coerceAtMost(1f)
                )
                JobClass.ASSASSIN -> base.copy(
                    aggression = (base.aggression + 0.3f).coerceAtMost(1f),
                    impulsive = (base.impulsive - 0.2f).coerceAtLeast(0f)
                )
                JobClass.ROGUE -> base.copy(
                    curiosity = (base.curiosity + 0.3f).coerceAtMost(1f),
                    greed = (base.greed + 0.2f).coerceAtMost(1f)
                )
                JobClass.MAGE, JobClass.SCHOLAR -> base.copy(
                    curiosity = (base.curiosity + 0.3f).coerceAtMost(1f),
                    impulsive = (base.impulsive - 0.2f).coerceAtLeast(0f)
                )
                JobClass.PRIEST, JobClass.PALADIN -> base.copy(
                    courage = (base.courage + 0.2f).coerceAtMost(1f),
                    greed = (base.greed - 0.3f).coerceAtLeast(0f)
                )
                JobClass.BARD, JobClass.MERCHANT -> base.copy(
                    social = (base.social + 0.4f).coerceAtMost(1f),
                    greed = (base.greed + 0.2f).coerceAtMost(1f)
                )
                else -> base
            }
        }
    }
}

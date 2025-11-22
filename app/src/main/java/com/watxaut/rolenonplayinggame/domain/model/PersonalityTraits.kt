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

        /**
         * Generate personality traits from questionnaire answers
         * Combines questionnaire effects with random base values and job class influence
         *
         * @param answers The player's answers to personality questions
         * @param questions The questions that were answered
         * @param jobClass The character's job class
         * @param random Random number generator for base values
         * @return PersonalityTraits influenced by answers, job class, and randomness
         */
        fun fromQuestionnaire(
            answers: List<QuestionAnswer>,
            questions: List<PersonalityQuestion>,
            jobClass: JobClass,
            random: Random = Random.Default
        ): PersonalityTraits {
            // Start with random base values (30% influence)
            val baseRandom = random()
            val randomWeight = 0.3f

            // Calculate effects from questionnaire answers (40% influence)
            var totalEffects = PersonalityEffects()
            answers.forEach { answer ->
                val question = questions.find { it.id == answer.questionId }
                if (question != null) {
                    val effect = when (question.type) {
                        QuestionType.SCALE -> {
                            PersonalityQuestionnaire.getScaleEffects(question.id, answer.scaleValue ?: 0)
                        }
                        QuestionType.YES_NO, QuestionType.MULTIPLE_CHOICE -> {
                            val optionIndex = answer.selectedOptionIndex ?: 0
                            if (optionIndex < question.options.size) {
                                question.options[optionIndex].effects
                            } else {
                                PersonalityEffects()
                            }
                        }
                    }
                    totalEffects += effect
                }
            }

            val questionnaireWeight = 0.4f

            // Calculate job class influence (30% influence)
            val jobClassBase = forJobClass(jobClass, random)
            val jobClassWeight = 0.3f

            // Combine all influences
            val courage = (
                baseRandom.courage * randomWeight +
                (0.5f + totalEffects.courage).coerceIn(0f, 1f) * questionnaireWeight +
                jobClassBase.courage * jobClassWeight
            ).coerceIn(0f, 1f)

            val greed = (
                baseRandom.greed * randomWeight +
                (0.5f + totalEffects.greed).coerceIn(0f, 1f) * questionnaireWeight +
                jobClassBase.greed * jobClassWeight
            ).coerceIn(0f, 1f)

            val curiosity = (
                baseRandom.curiosity * randomWeight +
                (0.5f + totalEffects.curiosity).coerceIn(0f, 1f) * questionnaireWeight +
                jobClassBase.curiosity * jobClassWeight
            ).coerceIn(0f, 1f)

            val aggression = (
                baseRandom.aggression * randomWeight +
                (0.5f + totalEffects.aggression).coerceIn(0f, 1f) * questionnaireWeight +
                jobClassBase.aggression * jobClassWeight
            ).coerceIn(0f, 1f)

            val social = (
                baseRandom.social * randomWeight +
                (0.5f + totalEffects.social).coerceIn(0f, 1f) * questionnaireWeight +
                jobClassBase.social * jobClassWeight
            ).coerceIn(0f, 1f)

            val impulsive = (
                baseRandom.impulsive * randomWeight +
                (0.5f + totalEffects.impulsive).coerceIn(0f, 1f) * questionnaireWeight +
                jobClassBase.impulsive * jobClassWeight
            ).coerceIn(0f, 1f)

            return PersonalityTraits(
                courage = courage,
                greed = greed,
                curiosity = curiosity,
                aggression = aggression,
                social = social,
                impulsive = impulsive
            )
        }
    }
}

package com.watxaut.rolenonplayinggame.domain.model

/**
 * Types of personality questions
 */
enum class QuestionType {
    YES_NO,           // Simple yes/no question
    SCALE,            // 0-5 agreement scale
    MULTIPLE_CHOICE   // Choose one option from several
}

/**
 * A personality question that affects character traits
 */
data class PersonalityQuestion(
    val id: String,
    val text: String,
    val type: QuestionType,
    val options: List<QuestionOption> = emptyList()
) {
    init {
        when (type) {
            QuestionType.YES_NO -> require(options.size == 2) {
                "YES_NO questions must have exactly 2 options"
            }
            QuestionType.SCALE -> require(options.isEmpty()) {
                "SCALE questions should not have options"
            }
            QuestionType.MULTIPLE_CHOICE -> require(options.size >= 2) {
                "MULTIPLE_CHOICE questions must have at least 2 options"
            }
        }
    }
}

/**
 * An answer option for a personality question
 */
data class QuestionOption(
    val text: String,
    val effects: PersonalityEffects
)

/**
 * Effects that an answer has on personality traits
 * Values range from -0.3 to +0.3 (will be averaged with random base values)
 */
data class PersonalityEffects(
    val courage: Float = 0f,      // -0.3 to +0.3
    val greed: Float = 0f,        // -0.3 to +0.3
    val curiosity: Float = 0f,    // -0.3 to +0.3
    val aggression: Float = 0f,   // -0.3 to +0.3
    val social: Float = 0f,       // -0.3 to +0.3
    val impulsive: Float = 0f     // -0.3 to +0.3
) {
    init {
        require(courage in -0.3f..0.3f) { "Courage effect must be between -0.3 and 0.3" }
        require(greed in -0.3f..0.3f) { "Greed effect must be between -0.3 and 0.3" }
        require(curiosity in -0.3f..0.3f) { "Curiosity effect must be between -0.3 and 0.3" }
        require(aggression in -0.3f..0.3f) { "Aggression effect must be between -0.3 and 0.3" }
        require(social in -0.3f..0.3f) { "Social effect must be between -0.3 and 0.3" }
        require(impulsive in -0.3f..0.3f) { "Impulsive effect must be between -0.3 and 0.3" }
    }

    /**
     * Combine multiple effects by averaging
     */
    operator fun plus(other: PersonalityEffects): PersonalityEffects {
        return PersonalityEffects(
            courage = courage + other.courage,
            greed = greed + other.greed,
            curiosity = curiosity + other.curiosity,
            aggression = aggression + other.aggression,
            social = social + other.social,
            impulsive = impulsive + other.impulsive
        )
    }
}

/**
 * An answer to a personality question
 */
data class QuestionAnswer(
    val questionId: String,
    val selectedOptionIndex: Int? = null,  // For YES_NO and MULTIPLE_CHOICE
    val scaleValue: Int? = null            // For SCALE (0-5)
) {
    init {
        if (selectedOptionIndex != null) {
            require(selectedOptionIndex >= 0) { "Selected option index must be non-negative" }
        }
        if (scaleValue != null) {
            require(scaleValue in 0..5) { "Scale value must be between 0 and 5" }
        }
    }
}

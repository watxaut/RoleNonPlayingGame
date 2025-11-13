package com.watxaut.rolenonplayinggame.core.ai

import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits
import javax.inject.Inject
import kotlin.random.Random

/**
 * Basic AI Decision Engine (Week 2 implementation)
 * Implements Priority levels 1-3: Survival, Critical Needs, Active Quest
 *
 * Based on FUNCTIONAL_DESIGN_DOCUMENT.md section 3.1 (Autonomous Behavior)
 */
class BasicDecisionEngine @Inject constructor(
    private val random: Random = Random.Default
) {

    /**
     * Make a decision based on character state and context.
     * Evaluates priorities from highest (1) to lowest (3).
     */
    fun makeDecision(
        character: Character,
        context: DecisionContext
    ): Decision {
        // Priority 1: SURVIVAL - Handle life-threatening situations
        if (context.isInDanger()) {
            return handleSurvival(character, context)
        }

        // Priority 2: CRITICAL_NEEDS - Handle low resources
        if (context.isLowResources() || context.getHealthPercentage() < 0.6f) {
            return handleCriticalNeeds(character, context)
        }

        // Priority 3: ACTIVE_QUEST - Continue quest if one is active
        if (context.hasActiveQuest) {
            return handleActiveQuest(character, context)
        }

        // Default: Explore or idle
        return handleIdle(character, context)
    }

    /**
     * Priority 1: SURVIVAL
     * HP < 30% - character must flee or heal immediately
     */
    private fun handleSurvival(character: Character, context: DecisionContext): Decision {
        // If we have access to an inn and enough gold, heal there
        if (context.hasInnAccess && context.gold >= context.innHealCost) {
            return Decision.HealAtInn(
                location = context.currentLocation,
                cost = context.innHealCost
            )
        }

        // If we can rest safely, do so
        if (context.canRest) {
            return Decision.Rest(location = context.currentLocation)
        }

        // Otherwise, flee to safety
        val safeLocation = findSafeLocation(context)
        return Decision.Flee(
            reason = "Low health (${(context.getHealthPercentage() * 100).toInt()}%), seeking safety"
        )
    }

    /**
     * Priority 2: CRITICAL_NEEDS
     * Low resources or health below 60%
     */
    private fun handleCriticalNeeds(character: Character, context: DecisionContext): Decision {
        // If health is low (but not critical), heal
        if (context.getHealthPercentage() < 0.6f) {
            if (context.hasInnAccess && context.gold >= context.innHealCost) {
                return Decision.HealAtInn(
                    location = context.currentLocation,
                    cost = context.innHealCost
                )
            } else if (context.canRest) {
                return Decision.Rest(location = context.currentLocation)
            }
        }

        // If low on gold, go hunt monsters
        if (context.isLowResources()) {
            // Find a location with monsters appropriate for character level
            val huntingLocation = findHuntingLocation(character, context)
            if (huntingLocation != context.currentLocation) {
                return Decision.Explore(targetLocation = huntingLocation)
            } else {
                return Decision.Combat(
                    enemyType = "Level ${character.level} Monster",
                    estimatedDifficulty = character.level
                )
            }
        }

        // Default: rest to recover
        return Decision.Rest(location = context.currentLocation)
    }

    /**
     * Priority 3: ACTIVE_QUEST
     * Continue working on the current quest
     */
    private fun handleActiveQuest(character: Character, context: DecisionContext): Decision {
        // For Week 2, we don't have a quest system yet, so just explore
        // This will be expanded in later weeks
        return Decision.ContinueQuest(
            questId = "placeholder_quest",

        )
    }

    /**
     * Fallback: IDLE
     * No urgent priorities - explore, hunt, or idle based on personality
     */
    private fun handleIdle(character: Character, context: DecisionContext): Decision {
        val personality = character.personalityTraits

        // Curious characters are more likely to explore
        if (random.nextFloat() < personality.curiosity && context.nearbyLocations.isNotEmpty()) {
            val newLocation = context.nearbyLocations.random(random)
            return Decision.Explore(targetLocation = newLocation)
        }

        // Aggressive characters are more likely to hunt
        if (random.nextFloat() < personality.aggression) {
            return Decision.Combat(
                enemyType = "Level ${character.level} Monster",
                estimatedDifficulty = character.level
            )
        }

        // Greedy characters might shop for better equipment
        if (random.nextFloat() < personality.greed && context.gold > 100) {
            return Decision.Shop(
                location = context.currentLocation,
                itemType = "equipment"
            )
        }

        // Default: rest
        return Decision.Idle
    }

    /**
     * Find a safe location to flee to
     */
    private fun findSafeLocation(context: DecisionContext): String {
        // For Week 2, just return the starting town
        return "heartlands_starting_town"
    }

    /**
     * Find a good location for hunting monsters
     */
    private fun findHuntingLocation(character: Character, context: DecisionContext): String {
        // For Week 2, return the fields if character is low level
        return if (character.level <= 3) {
            "heartlands_meadowbrook_fields"
        } else {
            context.currentLocation
        }
    }

    /**
     * Evaluate if a decision makes sense given the personality
     * Returns a score from 0.0 (bad fit) to 1.0 (perfect fit)
     */
    fun evaluateDecisionFit(decision: Decision, personality: PersonalityTraits): Float {
        return when (decision) {
            is Decision.Combat -> personality.aggression
            is Decision.Explore -> personality.curiosity
            is Decision.Flee -> 1f - personality.courage
            is Decision.Rest -> 1f - personality.impulsive
            is Decision.Shop -> personality.greed
            is Decision.AcceptQuest -> personality.curiosity * 0.5f + (1f - personality.impulsive) * 0.5f
            is Decision.HealAtInn -> (1f - personality.greed) * 0.3f + (1f - personality.impulsive) * 0.7f
            is Decision.ContinueQuest -> 1f - personality.impulsive
            Decision.Idle -> 1f - personality.curiosity
        }
    }
}

package com.watxaut.rolenonplayinggame.domain.usecase

import com.watxaut.rolenonplayinggame.core.ai.Decision
import com.watxaut.rolenonplayinggame.core.combat.CombatSystem
import com.watxaut.rolenonplayinggame.data.EnemyDatabase
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.ActivityRewards
import com.watxaut.rolenonplayinggame.domain.model.ActivityType
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import java.time.Instant
import javax.inject.Inject
import kotlin.math.min

/**
 * Execute Decision Use Case
 *
 * Takes a decision from the AI engine and executes it in the game world.
 * This bridges the AI decision-making with actual game mechanics.
 *
 * Per TECHNICAL_IMPLEMENTATION_DOCUMENT.md Week 3:
 * - Execute decisions from AI engine
 * - Update character state
 * - Log activities
 * - Handle combat, exploration, resting, etc.
 */
class ExecuteDecisionUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val activityRepository: ActivityRepository,
    private val combatSystem: CombatSystem
) {

    /**
     * Execute a decision and return the outcome.
     *
     * @param character The character making the decision
     * @param decision The decision to execute
     * @return Result containing the updated character and activity log
     */
    suspend operator fun invoke(
        character: Character,
        decision: Decision
    ): Result<DecisionOutcome> {
        return try {
            val outcome = when (decision) {
                is Decision.Combat -> executeCombat(character, decision)
                is Decision.Explore -> executeExplore(character, decision)
                is Decision.Rest -> executeRest(character, decision)
                is Decision.HealAtInn -> executeHealAtInn(character, decision)
                is Decision.Flee -> executeFlee(character, decision)
                is Decision.AcceptQuest -> executeAcceptQuest(character, decision)
                is Decision.ContinueQuest -> executeContinueQuest(character, decision)
                is Decision.Shop -> executeShop(character, decision)
                is Decision.Idle -> executeIdle(character, decision)
            }

            // Save updated character
            characterRepository.updateCharacter(outcome.updatedCharacter)

            // Log activity
            activityRepository.logActivity(outcome.activity)

            Result.success(outcome)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Execute combat decision.
     */
    private fun executeCombat(character: Character, decision: Decision.Combat): DecisionOutcome {
        // Get enemy from database
        val enemy = EnemyDatabase.getEnemyForLevel(
            level = decision.estimatedDifficulty.coerceAtLeast(character.level - 2),
            location = character.currentLocation
        )

        // Execute combat encounter
        val combatResult = combatSystem.executeEncounter(character, enemy)

        // Build detailed description from combat log
        val description = buildString {
            appendLine("Combat: ${character.name} vs ${enemy.name}")
            appendLine("Result: ${if (combatResult.victory) "Victory" else "Defeat"}")
            appendLine()
            combatResult.combatLog.take(5).forEach { appendLine(it) } // First 5 lines
            if (combatResult.combatLog.size > 5) {
                appendLine("... (${combatResult.combatLog.size - 5} more lines)")
            }
        }

        // Update character state based on combat result
        val updatedCharacter = if (combatResult.victory) {
            // Victory: gain rewards, update HP
            val newExp = character.experience + (combatResult.rewards?.experience ?: 0)
            val newGold = character.gold + (combatResult.rewards?.gold ?: 0)

            character.copy(
                currentHp = combatResult.characterFinalHp,
                experience = newExp,
                gold = newGold
            )
        } else {
            // Defeat: respawn at 50% HP, lose 10% gold
            val goldLost = (character.gold * 0.1).toInt()
            character.copy(
                currentHp = character.maxHp / 2,
                gold = character.gold - goldLost,
                currentLocation = "Willowdale Village" // Respawn in starting town
            )
        }

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.COMBAT,
            description = description.trim(),
            isMajorEvent = !combatResult.victory || combatResult.totalRounds >= 10,
            rewards = if (combatResult.victory) {
                ActivityRewards(
                    experience = combatResult.rewards?.experience ?: 0,
                    gold = combatResult.rewards?.gold ?: 0
                )
            } else null,
            metadata = mapOf(
                "enemy" to enemy.name,
                "victory" to combatResult.victory.toString(),
                "rounds" to combatResult.totalRounds.toString()
            )
        )

        return DecisionOutcome(
            updatedCharacter = updatedCharacter,
            activity = activity,
            combatLog = combatResult.combatLog
        )
    }

    /**
     * Execute exploration decision.
     */
    private fun executeExplore(character: Character, decision: Decision.Explore): DecisionOutcome {
        val targetLocation = decision.targetLocation

        // Chance of discovery (30%)
        val discovered = Math.random() < 0.3
        val description = if (discovered) {
            "Explored $targetLocation and made an interesting discovery!"
        } else {
            "Explored $targetLocation but found nothing of note."
        }

        val rewards = if (discovered) {
            ActivityRewards(experience = 25, gold = 10)
        } else {
            ActivityRewards(experience = 5)
        }

        val updatedCharacter = character.copy(
            currentLocation = targetLocation,
            experience = character.experience + rewards.experience,
            gold = character.gold + rewards.gold
        )

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.EXPLORATION,
            description = description,
            isMajorEvent = discovered,
            rewards = rewards,
            metadata = mapOf("location" to targetLocation)
        )

        return DecisionOutcome(updatedCharacter, activity)
    }

    /**
     * Execute rest decision (free healing over time).
     */
    private fun executeRest(character: Character, decision: Decision.Rest): DecisionOutcome {
        val healAmount = (character.maxHp * 0.3).toInt() // Heal 30%
        val newHp = min(character.maxHp, character.currentHp + healAmount)

        val updatedCharacter = character.copy(currentHp = newHp)

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.REST,
            description = "Rested at ${decision.location} and recovered $healAmount HP.",
            isMajorEvent = false,
            metadata = mapOf("location" to decision.location, "healed" to healAmount.toString())
        )

        return DecisionOutcome(updatedCharacter, activity)
    }

    /**
     * Execute heal at inn decision (instant healing, costs gold).
     */
    private fun executeHealAtInn(character: Character, decision: Decision.HealAtInn): DecisionOutcome {
        val cost = decision.cost
        val healAmount = character.maxHp - character.currentHp

        // Check if character can afford it
        if (character.gold < cost) {
            // Can't afford, just rest for free
            return executeRest(character, Decision.Rest(decision.location))
        }

        val updatedCharacter = character.copy(
            currentHp = character.maxHp,
            gold = character.gold - cost
        )

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.REST,
            description = "Stayed at inn in ${decision.location} and fully recovered ($healAmount HP) for $cost gold.",
            isMajorEvent = false,
            metadata = mapOf(
                "location" to decision.location,
                "cost" to cost.toString(),
                "healed" to healAmount.toString()
            )
        )

        return DecisionOutcome(updatedCharacter, activity)
    }

    /**
     * Execute flee decision.
     */
    private fun executeFlee(character: Character, decision: Decision.Flee): DecisionOutcome {
        // Flee to safety (starting town)
        val safeLocation = "Willowdale Village"

        val updatedCharacter = character.copy(currentLocation = safeLocation)

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.TRAVEL,
            description = "Fled to safety in $safeLocation. Reason: ${decision.reason}",
            isMajorEvent = false,
            metadata = mapOf("reason" to decision.reason)
        )

        return DecisionOutcome(updatedCharacter, activity)
    }

    /**
     * Execute accept quest decision.
     * TODO: Implement full quest system in future weeks.
     */
    private fun executeAcceptQuest(character: Character, decision: Decision.AcceptQuest): DecisionOutcome {
        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.QUEST,
            description = "Accepted quest: ${decision.questName}",
            isMajorEvent = false,
            metadata = mapOf("questId" to decision.questId, "questName" to decision.questName)
        )

        return DecisionOutcome(character, activity)
    }

    /**
     * Execute continue quest decision.
     * TODO: Implement full quest system in future weeks.
     */
    private fun executeContinueQuest(character: Character, decision: Decision.ContinueQuest): DecisionOutcome {
        // Placeholder: progress quest
        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.QUEST,
            description = "Worked on quest objectives.",
            isMajorEvent = false,
            metadata = mapOf("questId" to decision.questId)
        )

        return DecisionOutcome(character, activity)
    }

    /**
     * Execute shop decision.
     * TODO: Implement full shopping system in future weeks.
     */
    private fun executeShop(character: Character, decision: Decision.Shop): DecisionOutcome {
        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.SHOPPING,
            description = "Visited shop in ${decision.location} looking for ${decision.itemType}.",
            isMajorEvent = false,
            metadata = mapOf("location" to decision.location, "itemType" to decision.itemType)
        )

        return DecisionOutcome(character, activity)
    }

    /**
     * Execute idle decision (do nothing).
     */
    private fun executeIdle(character: Character, decision: Decision.Idle): DecisionOutcome {
        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.IDLE,
            description = "Idled around town, observing the surroundings.",
            isMajorEvent = false
        )

        return DecisionOutcome(character, activity)
    }
}

/**
 * Outcome of executing a decision.
 */
data class DecisionOutcome(
    val updatedCharacter: Character,
    val activity: Activity,
    val combatLog: List<String> = emptyList()
)

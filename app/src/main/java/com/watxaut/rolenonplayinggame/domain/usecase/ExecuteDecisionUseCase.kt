package com.watxaut.rolenonplayinggame.domain.usecase

import com.watxaut.rolenonplayinggame.core.ai.Decision
import com.watxaut.rolenonplayinggame.core.combat.CombatOutcome
import com.watxaut.rolenonplayinggame.core.combat.CombatSystem
import com.watxaut.rolenonplayinggame.core.messages.MessageProvider
import com.watxaut.rolenonplayinggame.data.EnemyDatabase
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.ActivityRewards
import com.watxaut.rolenonplayinggame.domain.model.ActivityType
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.presentation.map.getLocationDisplayName
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
    private val combatSystem: CombatSystem,
    private val messageProvider: MessageProvider
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
     * Execute combat decision using simplified combat system.
     */
    private fun executeCombat(character: Character, decision: Decision.Combat): DecisionOutcome {
        // Get enemy from database
        val enemy = EnemyDatabase.getEnemyForLevel(
            level = decision.estimatedDifficulty.coerceAtLeast(character.level - 2),
            location = character.currentLocation
        )

        // Execute simplified combat (single d21 roll)
        val combatResult = combatSystem.executeSimplifiedCombat(character, enemy)

        // Update character state based on combat outcome
        val updatedCharacter = when (combatResult.outcome) {
            CombatOutcome.WIN -> {
                // Victory: gain rewards
                character.copy(
                    experience = character.experience + (combatResult.rewards?.experience ?: 0),
                    gold = character.gold + (combatResult.rewards?.gold ?: 0)
                )
            }
            CombatOutcome.FLEE -> {
                // Fled successfully: no rewards, no penalties
                character
            }
            CombatOutcome.DEATH -> {
                // Death: respawn at 50% HP, lose gold
                character.copy(
                    currentHp = character.maxHp / 2,
                    gold = character.gold - combatResult.goldLost,
                    currentLocation = "Havenmoor" // Respawn in starting town
                )
            }
        }

        // Build activity description with the fun message from MessageProvider
        val description = buildString {
            appendLine(combatResult.description)
            when (combatResult.outcome) {
                CombatOutcome.WIN -> {
                    combatResult.rewards?.let {
                        appendLine("Gained ${it.experience} XP and ${it.gold} gold!")
                    }
                }
                CombatOutcome.DEATH -> {
                    appendLine("Lost ${combatResult.goldLost} gold and respawned in Havenmoor.")
                }
                CombatOutcome.FLEE -> {
                    appendLine("Escaped without rewards or penalties.")
                }
            }
        }

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.COMBAT,
            description = description.trim(),
            isMajorEvent = combatResult.outcome == CombatOutcome.DEATH || combatResult.roll == 21,
            rewards = combatResult.rewards?.let {
                ActivityRewards(
                    experience = it.experience,
                    gold = it.gold
                )
            },
            metadata = mapOf(
                "enemy" to enemy.name,
                "outcome" to combatResult.outcome.toString(),
                "roll" to combatResult.roll.toString(),
                "combatScore" to combatResult.combatScore.toString()
            )
        )

        return DecisionOutcome(
            updatedCharacter = updatedCharacter,
            activity = activity,
            combatLog = listOf(combatResult.description)
        )
    }

    /**
     * Execute exploration decision.
     */
    private fun executeExplore(character: Character, decision: Decision.Explore): DecisionOutcome {
        val targetLocation = decision.targetLocation
        val locationName = getLocationDisplayName(targetLocation)

        // Determine loot quality based on random roll
        val lootRoll = Math.random()
        val (lootMessage, rewards, isMajorEvent) = when {
            lootRoll < 0.05 -> {
                // 5% - Excellent loot
                val gold = (50..100).random()
                Triple(
                    messageProvider.getLootExcellentMessage(character.name, gold),
                    ActivityRewards(experience = 50, gold = gold),
                    true
                )
            }
            lootRoll < 0.25 -> {
                // 20% - Good loot
                val gold = (20..40).random()
                Triple(
                    messageProvider.getLootGoodMessage(character.name, gold),
                    ActivityRewards(experience = 25, gold = gold),
                    false
                )
            }
            lootRoll < 0.60 -> {
                // 35% - Poor loot
                val gold = (5..15).random()
                Triple(
                    messageProvider.getLootPoorMessage(character.name, gold),
                    ActivityRewards(experience = 10, gold = gold),
                    false
                )
            }
            else -> {
                // 40% - Nothing
                Triple(
                    messageProvider.getLootNothingMessage(character.name),
                    ActivityRewards(experience = 5),
                    false
                )
            }
        }

        val description = "$lootMessage while exploring $locationName."

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
            isMajorEvent = isMajorEvent,
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
        val isFullRecovery = newHp == character.maxHp

        val updatedCharacter = character.copy(currentHp = newHp)

        // Get a fun rest message from MessageProvider
        val restMessage = if (isFullRecovery) {
            messageProvider.getRestFullMessage(character.name, healAmount)
        } else {
            messageProvider.getRestPartialMessage(character.name, healAmount)
        }

        val description = "$restMessage at ${getLocationDisplayName(decision.location)}."

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.REST,
            description = description,
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

        // Get a fun rest message (always full recovery at inn)
        val restMessage = messageProvider.getRestFullMessage(character.name, healAmount)
        val description = "$restMessage at the inn in ${getLocationDisplayName(decision.location)} (Cost: $cost gold)."

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.REST,
            description = description,
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
            description = "Visited shop in ${getLocationDisplayName(decision.location)} looking for ${decision.itemType}.",
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

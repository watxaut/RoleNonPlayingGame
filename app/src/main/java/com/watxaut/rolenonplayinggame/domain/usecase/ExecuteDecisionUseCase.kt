package com.watxaut.rolenonplayinggame.domain.usecase

import com.watxaut.rolenonplayinggame.core.ai.Decision
import com.watxaut.rolenonplayinggame.core.ai.MissionDiscoveryHelper
import com.watxaut.rolenonplayinggame.core.combat.CombatOutcome
import com.watxaut.rolenonplayinggame.core.combat.CombatSystem
import com.watxaut.rolenonplayinggame.core.messages.MessageProvider
import com.watxaut.rolenonplayinggame.data.EnemyDatabase
import com.watxaut.rolenonplayinggame.domain.model.Activity
import com.watxaut.rolenonplayinggame.domain.model.ActivityRewards
import com.watxaut.rolenonplayinggame.domain.model.ActivityType
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.MissionContext
import com.watxaut.rolenonplayinggame.domain.repository.ActivityRepository
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
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
 * - Check for mission discoveries (principal steps, bosses, secondary missions)
 */
class ExecuteDecisionUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val activityRepository: ActivityRepository,
    private val combatSystem: CombatSystem,
    private val messageProvider: MessageProvider,
    private val missionDiscoveryHelper: MissionDiscoveryHelper,
    private val missionProgressRepository: MissionProgressRepository,
    private val socialRepository: com.watxaut.rolenonplayinggame.domain.repository.SocialRepository
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
                is Decision.Encounter -> executeEncounter(character, decision)
                is Decision.Idle -> executeIdle(character, decision)
            }

            // Save updated character
            characterRepository.updateCharacter(outcome.updatedCharacter)

            // Log activity
            activityRepository.logActivity(outcome.activity)

            // Check for mission discoveries (TODO: integrate when DB schema is ready)
            checkMissionDiscoveries(outcome.updatedCharacter, decision)

            Result.success(outcome)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check for mission discoveries based on the action taken.
     * This would be integrated with the database once mission tables are created.
     */
    private suspend fun checkMissionDiscoveries(character: Character, decision: Decision) {
        // TODO: Get actual mission progress from database once schema is implemented
        // For now, this demonstrates the integration points

        // Check secondary mission discovery on ANY action (1% chance)
        val secondaryMission = missionDiscoveryHelper.checkSecondaryMissionDiscovery(
            character = character,
            activeSecondaryMissions = emptySet() // TODO: Get from DB
        )

        if (secondaryMission != null) {
            println("📜 Secondary mission discovered: ${secondaryMission.name}")

            // Save to database
            missionProgressRepository.discoverSecondaryMission(
                characterId = character.id,
                missionId = secondaryMission.id
            ).onSuccess {
                // Log activity
                activityRepository.logActivity(
                    Activity(
                        characterId = character.id,
                        timestamp = Instant.now(),
                        type = ActivityType.QUEST,
                        description = "Discovered secondary mission: ${secondaryMission.name}",
                        rewards = ActivityRewards(),
                        metadata = mapOf("mission_id" to secondaryMission.id),
                        isMajorEvent = true
                    )
                )
            }.onFailure { error ->
                println("Failed to save secondary mission: ${error.message}")
            }
        }

        // Check principal mission progress during exploration
        if (decision is Decision.Explore) {
            // Get current principal mission progress
            val activeMissionProgress = character.activePrincipalMissionId?.let { missionId ->
                println("📍 Character has active mission: $missionId, fetching progress...")
                val result = missionProgressRepository.getActivePrincipalMission(character.id)
                if (result.isSuccess) {
                    val progress = result.getOrNull()
                    if (progress != null) {
                        println("   ✅ Found progress: ${progress.completedSteps.size} steps completed")
                    } else {
                        println("   ⚠️ No progress record found in database")
                    }
                    progress
                } else {
                    println("   ❌ Failed to fetch progress: ${result.exceptionOrNull()?.message}")
                    null
                }
            }

            // Check for mission step discovery (2% chance)
            val discoveredStep = missionDiscoveryHelper.checkPrincipalMissionStepDiscovery(
                character = character,
                currentLocation = character.currentLocation,
                activeMissionProgress = activeMissionProgress
            )

            if (discoveredStep != null) {
                println("🌟 Mission step discovered: ${discoveredStep.name}")
                println("   Lore: ${discoveredStep.loreText}")

                // Save progress to database
                character.activePrincipalMissionId?.let { missionId ->
                    missionProgressRepository.completeStory(
                        characterId = character.id,
                        missionId = missionId,
                        stepId = discoveredStep.id
                    ).onSuccess {
                        // Log activity
                        activityRepository.logActivity(
                            Activity(
                                characterId = character.id,
                                timestamp = Instant.now(),
                                type = ActivityType.QUEST,
                                description = "Discovered: ${discoveredStep.name}\n${discoveredStep.loreText}",
                                rewards = ActivityRewards(),
                                metadata = mapOf(
                                    "mission_id" to missionId,
                                    "step_id" to discoveredStep.id
                                ),
                                isMajorEvent = true
                            )
                        )
                    }.onFailure { error ->
                        println("Failed to save mission step: ${error.message}")
                    }
                }
            }

            // Check for boss encounter (2% chance after all steps complete)
            val bossEncounter = missionDiscoveryHelper.checkBossEncounter(
                character = character,
                currentLocation = character.currentLocation,
                activeMissionProgress = activeMissionProgress
            )

            if (bossEncounter != null) {
                println("⚔️ BOSS ENCOUNTER: ${bossEncounter.bossName}")
                println("   ${bossEncounter.loreText}")
                // TODO: Trigger boss combat and log activity
            }
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
        var updatedCharacter = when (combatResult.outcome) {
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
                    currentLocation = "heartlands_havenmoor" // Respawn in starting town (Havenmoor)
                )
            }
        }

        // Handle equipment drop - auto-equip if better
        combatResult.droppedEquipment?.let { droppedItem ->
            val currentEquipped = updatedCharacter.equipment.getEquipped(droppedItem.slot)
            if (droppedItem.isBetterThan(currentEquipped, updatedCharacter)) {
                // Auto-equip the better item
                updatedCharacter = updatedCharacter.copy(
                    equipment = updatedCharacter.equipment.equip(droppedItem)
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
                    combatResult.droppedEquipment?.let { item ->
                        val currentEquipped = character.equipment.getEquipped(item.slot)
                        if (item.isBetterThan(currentEquipped, character)) {
                            appendLine("Found ${item.name} and equipped it! (+${item.getTotalStatBonus()} total stats)")
                        } else {
                            appendLine("Found ${item.name} but current equipment is better.")
                        }
                    }
                }
                CombatOutcome.DEATH -> {
                    appendLine("Lost ${combatResult.goldLost} gold and respawned in ${getLocationDisplayName("heartlands_havenmoor")}.")
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
     * Balanced: ~70% chance of just visiting, ~30% chance of loot (2:1 ratio with combat)
     */
    private fun executeExplore(character: Character, decision: Decision.Explore): DecisionOutcome {
        val targetLocation = decision.targetLocation
        val locationName = getLocationDisplayName(targetLocation)

        // Determine if this is a simple visit or loot exploration
        val actionRoll = Math.random()

        if (actionRoll < 0.70) {
            // 70% - Just visiting/sightseeing (no rewards)
            val description = messageProvider.getExplorationVisitMessage(
                characterName = character.name,
                locationName = locationName
            )

            val updatedCharacter = character.copy(currentLocation = targetLocation)

            val activity = Activity(
                characterId = character.id,
                timestamp = Instant.now(),
                type = ActivityType.EXPLORATION,
                description = description,
                isMajorEvent = false,
                metadata = mapOf("location" to targetLocation, "action" to "visit")
            )

            return DecisionOutcome(updatedCharacter, activity)
        }

        // 30% - Loot exploration (find items/gold)
        val lootRoll = Math.random()
        val (lootMessage, rewards, isMajorEvent) = when {
            lootRoll < 0.05 -> {
                // 1.5% total (5% of 30%) - Excellent loot
                val gold = (50..100).random()
                Triple(
                    messageProvider.getLootExcellentMessage(character.name, gold),
                    ActivityRewards(experience = 50, gold = gold),
                    true
                )
            }
            lootRoll < 0.20 -> {
                // 6% total (20% of 30%) - Good loot
                val gold = (20..40).random()
                Triple(
                    messageProvider.getLootGoodMessage(character.name, gold),
                    ActivityRewards(experience = 25, gold = gold),
                    false
                )
            }
            lootRoll < 0.60 -> {
                // 18% total (60% of 30%) - Poor loot
                val gold = (5..15).random()
                Triple(
                    messageProvider.getLootPoorMessage(character.name, gold),
                    ActivityRewards(experience = 10, gold = gold),
                    false
                )
            }
            else -> {
                // 4.5% total (40% of 30%) - Nothing found
                Triple(
                    messageProvider.getLootNothingMessage(character.name),
                    ActivityRewards(experience = 5),
                    false
                )
            }
        }

        val description = "$lootMessage (Exploring $locationName)"

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
            metadata = mapOf("location" to targetLocation, "action" to "loot")
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
        // Flee to safety (starting town - Havenmoor)
        val safeLocation = "heartlands_havenmoor"

        val updatedCharacter = character.copy(currentLocation = safeLocation)

        val activity = Activity(
            characterId = character.id,
            timestamp = Instant.now(),
            type = ActivityType.TRAVEL,
            description = "Fled to safety in ${getLocationDisplayName(safeLocation)}. Reason: ${decision.reason}",
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
     * Execute social encounter decision.
     * Coordinates encounter with another character via Edge Function.
     */
    private suspend fun executeEncounter(character: Character, decision: Decision.Encounter): DecisionOutcome {
        // Coordinate encounter via Edge Function
        val encounterResult = socialRepository.coordinateEncounter(
            character1Id = character.id,
            character2Id = decision.otherCharacterId,
            location = character.currentLocation
        )

        return encounterResult.fold(
            onSuccess = { encounter ->
                val outcome = encounter.outcome!!
                val description = outcome.description

                // Apply effects based on encounter type
                var updatedCharacter = character
                if (outcome.goldChange != null && outcome.goldChange != 0L) {
                    updatedCharacter = updatedCharacter.copy(
                        gold = maxOf(0, updatedCharacter.gold + outcome.goldChange)
                    )
                }
                if (outcome.damageDealt != null && outcome.damageDealt > 0) {
                    updatedCharacter = updatedCharacter.copy(
                        currentHp = maxOf(0, updatedCharacter.currentHp - outcome.damageDealt)
                    )
                }

                val activity = Activity(
                    characterId = character.id,
                    timestamp = Instant.now(),
                    type = ActivityType.SOCIAL,
                    description = description,
                    rewards = ActivityRewards(
                        gold = outcome.goldChange?.toInt() ?: 0
                    ),
                    metadata = mapOf(
                        "encounter_id" to encounter.id,
                        "encounter_type" to encounter.encounterType.name,
                        "other_character" to decision.otherCharacterName
                    ),
                    isMajorEvent = encounter.encounterType in listOf(
                        com.watxaut.rolenonplayinggame.domain.model.EncounterType.COMBAT,
                        com.watxaut.rolenonplayinggame.domain.model.EncounterType.PARTY
                    )
                )

                DecisionOutcome(updatedCharacter, activity)
            },
            onFailure = { error ->
                // Encounter failed - log it as idle
                val activity = Activity(
                    characterId = character.id,
                    timestamp = Instant.now(),
                    type = ActivityType.SOCIAL,
                    description = "Approached ${decision.otherCharacterName} but they had already left the area.",
                    isMajorEvent = false
                )
                DecisionOutcome(character, activity)
            }
        )
    }

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

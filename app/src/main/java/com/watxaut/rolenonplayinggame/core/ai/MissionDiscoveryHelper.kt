package com.watxaut.rolenonplayinggame.core.ai

import com.watxaut.rolenonplayinggame.data.repository.PrincipalMissionsRepository
import com.watxaut.rolenonplayinggame.data.repository.SecondaryMissionsRepository
import com.watxaut.rolenonplayinggame.domain.model.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * Helper for AI to discover principal and secondary missions during autonomous gameplay.
 * Implements the discovery chances defined in GameBalance.kt:
 * - Principal mission steps: 2% chance during exploration
 * - Boss encounters: 2% chance after all steps complete
 * - Secondary missions: 1% chance during any action
 */
class MissionDiscoveryHelper @Inject constructor() {
    private val random: Random = Random.Default

    /**
     * Check if a principal mission step should be discovered during this action.
     * Called during exploration actions.
     *
     * @param character Current character
     * @param currentLocation Current location ID
     * @param activeMissionProgress Character's current principal mission progress
     * @return Discovered step or null
     */
    fun checkPrincipalMissionStepDiscovery(
        character: Character,
        currentLocation: String,
        activeMissionProgress: PrincipalMissionProgress?
    ): MissionStep? {
        // No mission assigned yet
        if (activeMissionProgress == null) return null

        // Get the mission data
        val mission = PrincipalMissionsRepository.getMissionById(activeMissionProgress.missionId)
            ?: return null

        // Mission already complete
        if (activeMissionProgress.isComplete()) return null

        // Find next undiscovered step
        val nextStep = mission.steps.firstOrNull { step ->
            !activeMissionProgress.isStepComplete(step.id)
        } ?: return null

        // Check location requirement (if any)
        if (nextStep.locationRequirement != null && nextStep.locationRequirement != currentLocation) {
            return null
        }

        // Roll for discovery (2% chance per GameBalance)
        val roll = random.nextFloat()
        if (roll <= GameBalance.PRINCIPAL_MISSION_STEP_DISCOVERY_CHANCE) {
            return nextStep
        }

        return null
    }

    /**
     * Check if the boss encounter should happen.
     * Called during exploration after all mission steps are complete.
     *
     * @param character Current character
     * @param currentLocation Current location ID
     * @param activeMissionProgress Character's current principal mission progress
     * @return Boss battle or null
     */
    fun checkBossEncounter(
        character: Character,
        currentLocation: String,
        activeMissionProgress: PrincipalMissionProgress?
    ): BossBattle? {
        // No mission or not all steps complete
        if (activeMissionProgress == null) return null

        val mission = PrincipalMissionsRepository.getMissionById(activeMissionProgress.missionId)
            ?: return null

        // Check if can encounter boss
        if (!activeMissionProgress.canEncounterBoss(mission)) return null

        // Check if character meets win conditions
        if (!meetsWinConditions(character, mission.winConditions)) {
            // Character not strong enough yet
            return null
        }

        // Check if in correct location (boss location)
        if (mission.bossBattle.location != currentLocation) {
            return null
        }

        // Roll for encounter (2% chance per GameBalance)
        val roll = random.nextFloat()
        if (roll <= GameBalance.PRINCIPAL_MISSION_BOSS_ENCOUNTER_CHANCE) {
            return mission.bossBattle
        }

        return null
    }

    /**
     * Check if a secondary mission should be discovered.
     * Called during any action (1% chance).
     *
     * @param character Current character
     * @param activeSecondaryMissions List of currently active secondary mission IDs
     * @return Discovered mission or null
     */
    fun checkSecondaryMissionDiscovery(
        character: Character,
        activeSecondaryMissions: Set<String>
    ): SecondaryMission? {
        // Roll for discovery (1% chance per GameBalance)
        val roll = random.nextFloat()
        if (roll > GameBalance.SECONDARY_MISSION_DISCOVERY_CHANCE) {
            return null
        }

        // Get a random mission that's not already active
        return SecondaryMissionsRepository.getRandomMission(excludeIds = activeSecondaryMissions)
    }

    /**
     * Check if character meets the win conditions for a mission
     */
    private fun meetsWinConditions(character: Character, conditions: WinConditions): Boolean {
        // Check level requirement
        if (conditions.minimumLevel != null && character.level < conditions.minimumLevel) {
            return false
        }

        // Check stat requirements
        val totalStats = character.getTotalStats()
        for ((statType, requiredValue) in conditions.requiredStats) {
            val currentValue = when (statType) {
                StatType.STRENGTH -> totalStats.strength
                StatType.INTELLIGENCE -> totalStats.intelligence
                StatType.AGILITY -> totalStats.agility
                StatType.LUCK -> totalStats.luck
                StatType.CHARISMA -> totalStats.charisma
                StatType.VITALITY -> totalStats.vitality
            }
            if (currentValue < requiredValue) {
                return false
            }
        }

        // Check equipment requirements (simplified for now)
        // TODO: Implement equipment checking when inventory system is complete

        return true
    }

    /**
     * Generate activity log text for mission step discovery
     */
    fun generateStepDiscoveryText(step: MissionStep): String {
        return "🌟 MISSION PROGRESS: ${step.name} - ${step.loreText}"
    }

    /**
     * Generate activity log text for boss encounter
     */
    fun generateBossEncounterText(boss: BossBattle): String {
        return "⚔️ BOSS ENCOUNTER: ${boss.bossName} (Level ${boss.bossLevel}) - ${boss.bossDescription}"
    }

    /**
     * Generate activity log text for secondary mission discovery
     */
    fun generateSecondaryMissionDiscoveryText(mission: SecondaryMission): String {
        return "📜 NEW MISSION: ${mission.name} - ${mission.description}"
    }

    /**
     * Check mission completion for secondary missions
     */
    fun checkSecondaryMissionCompletion(
        mission: SecondaryMission,
        character: Character,
        context: MissionContext
    ): Boolean {
        return mission.winCondition.isMet(character, context)
    }

    /**
     * Calculate rewards for completing a secondary mission
     */
    fun calculateSecondaryMissionRewards(mission: SecondaryMission): SecondaryMissionRewards {
        val roll = random.nextFloat()

        val equipment = if (roll <= mission.rareEquipmentChance) {
            "Rare Equipment"
        } else if (roll <= mission.equipmentChance) {
            "Equipment"
        } else {
            null
        }

        return SecondaryMissionRewards(
            experience = mission.baseExperience,
            gold = mission.baseGold,
            equipment = equipment
        )
    }
}

/**
 * Rewards for completing a secondary mission
 */
data class SecondaryMissionRewards(
    val experience: Long,
    val gold: Int,
    val equipment: String?
)

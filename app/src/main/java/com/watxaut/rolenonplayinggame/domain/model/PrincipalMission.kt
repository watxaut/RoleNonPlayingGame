package com.watxaut.rolenonplayinggame.domain.model

/**
 * Principal Mission - main story quest for a character.
 * Per user requirements:
 * - Each job class has 3 different principal missions
 * - Missions are related to the lore from WORLD_LORE.md
 * - Each mission has 4+ steps + a boss battle
 * - Assigned at character creation and after completion
 */
data class PrincipalMission(
    val id: String,
    val name: String,
    val description: String,
    val jobClass: JobClass,
    val loreRegion: LoreRegion,

    // Steps to complete before boss (2% discovery chance each)
    val steps: List<MissionStep>,

    // Final boss encounter (2% chance after all steps complete)
    val bossBattle: BossBattle,

    // Win conditions
    val winConditions: WinConditions,

    // Rewards upon completion
    val rewards: PrincipalMissionRewards,

    // Lore unlocked upon completion
    val loreFragment: String
)

/**
 * Individual step within a principal mission
 */
data class MissionStep(
    val id: String,
    val stepNumber: Int,
    val name: String,
    val description: String,
    val locationRequirement: String?, // Location where this step can be discovered
    val loreText: String, // Lore revealed when step is discovered
    val discoveryChance: Float = 0.02f // 2% chance per exploration action
)

/**
 * Boss battle at the end of a principal mission
 */
data class BossBattle(
    val bossName: String,
    val bossLevel: Int,
    val bossDescription: String,
    val location: String,
    val encounterChance: Float = 0.02f, // 2% chance after all steps complete
    val canRetry: Boolean = true, // Hero can retry if defeated
    val loreText: String // Lore revealed during boss battle
)

/**
 * Win conditions for completing a principal mission
 */
data class WinConditions(
    val minimumLevel: Int? = null,
    val requiredEquipment: List<String> = emptyList(), // Equipment IDs
    val requiredStats: Map<StatType, Int> = emptyMap(), // Minimum stat requirements
    val mustDefeatBoss: Boolean = true
)

/**
 * Rewards for completing a principal mission
 */
data class PrincipalMissionRewards(
    val experience: Long,
    val gold: Int,
    val guaranteedItems: List<String> = emptyList(), // Guaranteed item IDs
    val rareItemChance: Float = 0.0f, // Chance of rare item (0.0 to 1.0)
    val rareItemPool: List<String> = emptyList() // Pool of possible rare items
)

/**
 * Regions from WORLD_LORE.md
 */
enum class LoreRegion(val displayName: String, val levelRange: IntRange) {
    HEARTLANDS("The Heartlands", 1..10),
    THORNWOOD_WILDS("The Thornwood Wilds", 11..25),
    ASHENVEIL_DESERT("The Ashenveil Desert", 26..40),
    FROSTPEAK_MOUNTAINS("The Frostpeak Mountains", 26..40),
    STORMCOAST_REACHES("The Stormcoast Reaches", 35..50),
    AETHERMOOR_MYSTERIES("Mysteries of Aethermoor", 1..50) // Meta-region for overarching mysteries
}

/**
 * Progress tracking for a principal mission
 */
data class PrincipalMissionProgress(
    val missionId: String,
    val completedSteps: Set<String> = emptySet(), // Set of completed step IDs
    val bossEncountered: Boolean = false,
    val bossDefeated: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun isStepComplete(stepId: String): Boolean = stepId in completedSteps

    fun areAllStepsComplete(mission: PrincipalMission): Boolean {
        return mission.steps.all { step -> isStepComplete(step.id) }
    }

    fun canEncounterBoss(mission: PrincipalMission): Boolean {
        return areAllStepsComplete(mission) && !bossDefeated
    }

    fun isComplete(): Boolean = bossDefeated

    fun getProgressPercentage(mission: PrincipalMission): Int {
        val totalSteps = mission.steps.size + 1 // +1 for boss
        val completed = completedSteps.size + (if (bossDefeated) 1 else 0)
        return (completed * 100) / totalSteps
    }
}

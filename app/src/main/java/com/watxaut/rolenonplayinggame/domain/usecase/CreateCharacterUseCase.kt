package com.watxaut.rolenonplayinggame.domain.usecase

import com.watxaut.rolenonplayinggame.data.repository.PrincipalMissionsRepository
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.CharacterStats
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.LoreDiscovery
import com.watxaut.rolenonplayinggame.domain.model.LoreSourceType
import com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits
import com.watxaut.rolenonplayinggame.domain.model.PredefinedLore
import com.watxaut.rolenonplayinggame.domain.model.PrincipalMissionProgress
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for creating a new character.
 * Validates input, generates personality traits, assigns principal mission, and persists the character.
 */
class CreateCharacterUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val missionProgressRepository: MissionProgressRepository
) {

    /**
     * Creates a new character with the given parameters.
     * @return Result containing the created Character or an error
     */
    suspend operator fun invoke(
        userId: String,
        name: String,
        jobClass: JobClass,
        stats: CharacterStats
    ): Result<Character> {
        // Validate name
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Character name cannot be blank"))
        }

        if (name.length < 3) {
            return Result.failure(IllegalArgumentException("Character name must be at least 3 characters"))
        }

        if (name.length > 20) {
            return Result.failure(IllegalArgumentException("Character name must be at most 20 characters"))
        }

        // Validate stats (should total 10 points, minimum 1 per stat)
        val totalPoints = stats.getTotalPoints()
        if (totalPoints != 10) {
            return Result.failure(IllegalArgumentException("Stats must total exactly 10 points (currently: $totalPoints)"))
        }

        if (stats.strength < 1 || stats.intelligence < 1 || stats.agility < 1 ||
            stats.luck < 1 || stats.charisma < 1 || stats.vitality < 1) {
            return Result.failure(IllegalArgumentException("All stats must be at least 1"))
        }

        // Assign a random principal mission for the character's job class
        val assignedMission = PrincipalMissionsRepository.getRandomMissionForJobClass(jobClass)

        // Create character with generated personality traits and assigned mission
        val character = Character.create(
            userId = userId,
            name = name,
            jobClass = jobClass,
            initialStats = stats.toMap()
        ).copy(
            activePrincipalMissionId = assignedMission?.id,
            principalMissionStartedAt = assignedMission?.let { Instant.now() }
        )

        // Log mission assignment for development
        assignedMission?.let { mission ->
            println("✨ Assigned principal mission to ${character.name}: ${mission.name}")
            println("   Mission ID: ${mission.id}")
            println("   Region: ${mission.loreRegion.displayName}")
            println("   Steps: ${mission.steps.size}")
        }

        // Persist character to repository
        val characterResult = characterRepository.createCharacter(character)
        if (characterResult.isFailure) {
            return characterResult
        }

        // Create mission progress entry in Supabase
        assignedMission?.let { mission ->
            val missionResult = missionProgressRepository.assignPrincipalMission(
                characterId = character.id,
                missionId = mission.id
            )
            if (missionResult.isFailure) {
                println("⚠️ Failed to assign mission progress in Supabase: ${missionResult.exceptionOrNull()?.message}")
                // Don't fail character creation if mission assignment fails
            }
        }

        // Add initial lore discoveries
        val defaultLore = PredefinedLore.getDefaultUnlockedLore()
        println("📚 Adding ${defaultLore.size} initial lore entries for ${character.name}")
        defaultLore.forEach { loreEntry ->
            val loreDiscovery = LoreDiscovery(
                id = UUID.randomUUID().toString(),
                category = loreEntry.category,
                title = loreEntry.title,
                content = loreEntry.content,
                discoveredAt = System.currentTimeMillis(),
                sourceType = LoreSourceType.LOCATION_DISCOVERY, // Initial lore from arriving at Aethermoor
                sourceId = "character_creation"
            )

            val loreResult = missionProgressRepository.addLoreDiscovery(character.id, loreDiscovery)
            if (loreResult.isFailure) {
                println("⚠️  Failed to add initial lore '${loreEntry.title}': ${loreResult.exceptionOrNull()?.message}")
                // Don't fail character creation if lore discovery fails
            } else {
                println("📖 Added initial lore: ${loreEntry.title}")
            }
        }

        return characterResult
    }

    /**
     * Validates character name without creating the character
     */
    fun validateName(name: String): Result<Unit> {
        return when {
            name.isBlank() -> Result.failure(IllegalArgumentException("Name cannot be blank"))
            name.length < 3 -> Result.failure(IllegalArgumentException("Name must be at least 3 characters"))
            name.length > 20 -> Result.failure(IllegalArgumentException("Name must be at most 20 characters"))
            !name.matches(Regex("^[a-zA-Z0-9 ]+$")) ->
                Result.failure(IllegalArgumentException("Name can only contain letters, numbers, and spaces"))
            else -> Result.success(Unit)
        }
    }

    /**
     * Validates stat allocation without creating the character
     */
    fun validateStats(stats: CharacterStats): Result<Unit> {
        val totalPoints = stats.getTotalPoints()

        return when {
            totalPoints != 10 ->
                Result.failure(IllegalArgumentException("Stats must total exactly 10 points (currently: $totalPoints)"))
            stats.strength < 1 || stats.intelligence < 1 || stats.agility < 1 ||
            stats.luck < 1 || stats.charisma < 1 || stats.vitality < 1 ->
                Result.failure(IllegalArgumentException("All stats must be at least 1"))
            else -> Result.success(Unit)
        }
    }
}

package com.watxaut.rolenonplayinggame.domain.usecase

import com.watxaut.rolenonplayinggame.data.repository.PrincipalMissionsRepository
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.CharacterStats
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits
import com.watxaut.rolenonplayinggame.domain.model.PrincipalMissionProgress
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * Use case for creating a new character.
 * Validates input, generates personality traits, assigns principal mission, and persists the character.
 */
class CreateCharacterUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
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

        // Create character with generated personality traits
        val character = Character.create(
            userId = userId,
            name = name,
            jobClass = jobClass,
            initialStats = stats.toMap()
        )

        // Assign a random principal mission for the character's job class
        val assignedMission = PrincipalMissionsRepository.getRandomMissionForJobClass(jobClass)

        // TODO: When database is updated, persist the mission assignment
        // For now, the mission will be assigned when DB schema is implemented
        // The mission ID would be stored as: assignedMission?.id

        // Log mission assignment for development
        assignedMission?.let { mission ->
            println("✨ Assigned principal mission to ${character.name}: ${mission.name}")
            println("   Mission ID: ${mission.id}")
            println("   Region: ${mission.loreRegion.displayName}")
            println("   Steps: ${mission.steps.size}")
        }

        // Persist to repository
        return characterRepository.createCharacter(character)
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

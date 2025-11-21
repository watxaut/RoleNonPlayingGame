package com.watxaut.rolenonplayinggame.domain.usecase

import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.CharacterStats
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.repository.CharacterRepository
import com.watxaut.rolenonplayinggame.domain.repository.MissionProgressRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CreateCharacterUseCase
 */
class CreateCharacterUseCaseTest {

    private lateinit var mockRepository: CharacterRepository
    private lateinit var mockMissionProgressRepository: MissionProgressRepository
    private lateinit var useCase: CreateCharacterUseCase

    @Before
    fun setup() {
        mockRepository = mockk()
        mockMissionProgressRepository = mockk(relaxed = true)
        useCase = CreateCharacterUseCase(mockRepository, mockMissionProgressRepository)
    }

    @Test
    fun `validateName should return success for valid name`() {
        val result = useCase.validateName("TestHero")

        assertTrue("Valid name should pass validation", result.isSuccess)
    }

    @Test
    fun `validateName should fail for blank name`() {
        val result = useCase.validateName("")

        assertTrue("Blank name should fail", result.isFailure)
        assertTrue(
            "Error message should mention blank",
            result.exceptionOrNull()?.message?.contains("blank", ignoreCase = true) == true
        )
    }

    @Test
    fun `validateName should fail for name too short`() {
        val result = useCase.validateName("AB")

        assertTrue("Short name should fail", result.isFailure)
        assertTrue(
            "Error message should mention minimum length",
            result.exceptionOrNull()?.message?.contains("3 characters", ignoreCase = true) == true
        )
    }

    @Test
    fun `validateName should fail for name too long`() {
        val result = useCase.validateName("ThisNameIsWayTooLongForACharacter")

        assertTrue("Long name should fail", result.isFailure)
        assertTrue(
            "Error message should mention maximum length",
            result.exceptionOrNull()?.message?.contains("20 characters", ignoreCase = true) == true
        )
    }

    @Test
    fun `validateName should fail for name with special characters`() {
        val result = useCase.validateName("Test@Hero!")

        assertTrue("Name with special chars should fail", result.isFailure)
    }

    @Test
    fun `validateStats should return success for valid stats`() {
        val stats = CharacterStats(2, 2, 2, 1, 1, 2) // Total = 10

        val result = useCase.validateStats(stats)

        assertTrue("Valid stats should pass validation", result.isSuccess)
    }

    @Test
    fun `validateStats should fail when total is not 10`() {
        val stats = CharacterStats(3, 3, 3, 3, 3, 3) // Total = 18

        val result = useCase.validateStats(stats)

        assertTrue("Invalid total should fail", result.isFailure)
        assertTrue(
            "Error message should mention 10 points",
            result.exceptionOrNull()?.message?.contains("10 points", ignoreCase = true) == true
        )
    }

    @Test
    fun `validateStats should fail when any stat is less than 1`() {
        val stats = CharacterStats(0, 2, 2, 2, 2, 2) // Strength = 0

        val result = useCase.validateStats(stats)

        assertTrue("Stat less than 1 should fail", result.isFailure)
        assertTrue(
            "Error message should mention minimum 1",
            result.exceptionOrNull()?.message?.contains("at least 1", ignoreCase = true) == true
        )
    }

    @Test
    fun `invoke should create character with valid inputs`() = runTest {
        val stats = CharacterStats(2, 2, 2, 1, 1, 2)
        val mockCharacter = mockk<Character>()

        coEvery {
            mockRepository.getCharactersByUserId("user123")
        } returns kotlinx.coroutines.flow.flowOf(emptyList())

        coEvery {
            mockRepository.createCharacter(any())
        } returns Result.success(mockCharacter)

        val result = useCase(
            userId = "user123",
            name = "TestHero",
            jobClass = JobClass.WARRIOR,
            stats = stats
        )

        assertTrue("Character creation should succeed", result.isSuccess)
        assertEquals("Should return created character", mockCharacter, result.getOrNull())
    }

    @Test
    fun `invoke should fail for invalid name`() = runTest {
        val stats = CharacterStats(2, 2, 2, 1, 1, 2)

        coEvery {
            mockRepository.getCharactersByUserId("user123")
        } returns kotlinx.coroutines.flow.flowOf(emptyList())

        val result = useCase(
            userId = "user123",
            name = "AB", // Too short
            jobClass = JobClass.WARRIOR,
            stats = stats
        )

        assertTrue("Should fail for invalid name", result.isFailure)
    }

    @Test
    fun `invoke should fail for invalid stats`() = runTest {
        val stats = CharacterStats(5, 5, 5, 5, 5, 5) // Total = 30, not 10

        coEvery {
            mockRepository.getCharactersByUserId("user123")
        } returns kotlinx.coroutines.flow.flowOf(emptyList())

        val result = useCase(
            userId = "user123",
            name = "TestHero",
            jobClass = JobClass.WARRIOR,
            stats = stats
        )

        assertTrue("Should fail for invalid stats", result.isFailure)
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        val stats = CharacterStats(2, 2, 2, 1, 1, 2)
        val error = RuntimeException("Database error")

        coEvery {
            mockRepository.getCharactersByUserId("user123")
        } returns kotlinx.coroutines.flow.flowOf(emptyList())

        coEvery {
            mockRepository.createCharacter(any())
        } returns Result.failure(error)

        val result = useCase(
            userId = "user123",
            name = "TestHero",
            jobClass = JobClass.WARRIOR,
            stats = stats
        )

        assertTrue("Should propagate repository failure", result.isFailure)
        assertEquals("Should return same error", error, result.exceptionOrNull())
    }
}

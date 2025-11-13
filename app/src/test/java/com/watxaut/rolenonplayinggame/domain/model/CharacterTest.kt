package com.watxaut.rolenonplayinggame.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Character model
 */
class CharacterTest {

    @Test
    fun `create should generate character with correct initial values`() {
        val stats = mapOf(
            StatType.STRENGTH to 3,
            StatType.INTELLIGENCE to 2,
            StatType.AGILITY to 2,
            StatType.LUCK to 1,
            StatType.CHARISMA to 1,
            StatType.VITALITY to 1
        )

        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = stats
        )

        assertEquals("Name should match", "Test Hero", character.name)
        assertEquals("Job class should match", JobClass.WARRIOR, character.jobClass)
        assertEquals("Level should be 1", 1, character.level)
        assertEquals("Experience should be 0", 0L, character.experience)
        assertEquals("Strength should match", 3, character.strength)
        assertEquals("Max HP should be calculated correctly", 60, character.maxHp) // 50 + (1*10)
        assertEquals("Current HP should equal max HP", character.maxHp, character.currentHp)
    }

    @Test
    fun `getStat should return correct stat values`() {
        val stats = mapOf(
            StatType.STRENGTH to 5,
            StatType.INTELLIGENCE to 3,
            StatType.AGILITY to 4,
            StatType.LUCK to 2,
            StatType.CHARISMA to 1,
            StatType.VITALITY to 5
        )

        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = stats
        )

        assertEquals("Strength should be 5", 5, character.getStat(StatType.STRENGTH))
        assertEquals("Intelligence should be 3", 3, character.getStat(StatType.INTELLIGENCE))
        assertEquals("Agility should be 4", 4, character.getStat(StatType.AGILITY))
        assertEquals("Luck should be 2", 2, character.getStat(StatType.LUCK))
        assertEquals("Charisma should be 1", 1, character.getStat(StatType.CHARISMA))
        assertEquals("Vitality should be 5", 5, character.getStat(StatType.VITALITY))
    }

    @Test
    fun `experienceForNextLevel should return correct value`() {
        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = CharacterStats.default().toMap()
        )

        assertEquals("Level 1 needs 100 XP", 100L, character.experienceForNextLevel())

        val level5Character = character.copy(level = 5)
        assertEquals("Level 5 needs 500 XP", 500L, level5Character.experienceForNextLevel())
    }

    @Test
    fun `shouldLevelUp should return true when enough experience`() {
        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = CharacterStats.default().toMap()
        )

        val withEnoughXP = character.copy(experience = 100L)
        assertTrue("Should level up at 100 XP", withEnoughXP.shouldLevelUp())

        val withMoreThanEnough = character.copy(experience = 150L)
        assertTrue("Should level up at 150 XP", withMoreThanEnough.shouldLevelUp())

        val withNotEnough = character.copy(experience = 99L)
        assertFalse("Should not level up at 99 XP", withNotEnough.shouldLevelUp())
    }

    @Test
    fun `getHealthPercentage should return correct percentage`() {
        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = CharacterStats.default().toMap()
        ).copy(maxHp = 100, currentHp = 50)

        assertEquals("Health should be 50%", 0.5f, character.getHealthPercentage(), 0.01f)

        val fullHealth = character.copy(currentHp = 100)
        assertEquals("Health should be 100%", 1.0f, fullHealth.getHealthPercentage(), 0.01f)

        val lowHealth = character.copy(currentHp = 25)
        assertEquals("Health should be 25%", 0.25f, lowHealth.getHealthPercentage(), 0.01f)
    }

    @Test
    fun `isInDanger should return true when health is below 30 percent`() {
        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = CharacterStats.default().toMap()
        ).copy(maxHp = 100, currentHp = 29)

        assertTrue("Should be in danger at 29% health", character.isInDanger())

        val safeCharacter = character.copy(currentHp = 31)
        assertFalse("Should not be in danger at 31% health", safeCharacter.isInDanger())
    }

    @Test
    fun `calculateMaxHp should return correct value`() {
        val character = Character.create(
            userId = "test-user-id",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = mapOf(
                StatType.STRENGTH to 1,
                StatType.INTELLIGENCE to 1,
                StatType.AGILITY to 1,
                StatType.LUCK to 1,
                StatType.CHARISMA to 1,
                StatType.VITALITY to 10
            )
        ).copy(level = 5)

        // Formula: 50 + (vitality * 10) + (level * 5)
        // 50 + (10 * 10) + (5 * 5) = 50 + 100 + 25 = 175
        assertEquals("Max HP should be 175", 175, character.calculateMaxHp())
    }

    @Test
    fun `CharacterStats default should have correct values`() {
        val stats = CharacterStats.default()

        assertEquals("Total should be 10 points", 10, stats.getTotalPoints())
        assertEquals("Strength should be 1", 1, stats.strength)
        assertEquals("Intelligence should be 1", 1, stats.intelligence)
        assertEquals("Agility should be 1", 1, stats.agility)
        assertEquals("Luck should be 1", 1, stats.luck)
        assertEquals("Charisma should be 1", 1, stats.charisma)
        assertEquals("Vitality should be 5", 5, stats.vitality)
    }

    @Test
    fun `CharacterStats toMap should return correct map`() {
        val stats = CharacterStats(
            strength = 2,
            intelligence = 3,
            agility = 4,
            luck = 5,
            charisma = 6,
            vitality = 7
        )

        val map = stats.toMap()

        assertEquals("Strength should be 2", 2, map[StatType.STRENGTH])
        assertEquals("Intelligence should be 3", 3, map[StatType.INTELLIGENCE])
        assertEquals("Agility should be 4", 4, map[StatType.AGILITY])
        assertEquals("Luck should be 5", 5, map[StatType.LUCK])
        assertEquals("Charisma should be 6", 6, map[StatType.CHARISMA])
        assertEquals("Vitality should be 7", 7, map[StatType.VITALITY])
    }
}

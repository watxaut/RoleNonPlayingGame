package com.watxaut.rolenonplayinggame.core.combat

import com.watxaut.rolenonplayinggame.core.dice.DiceRoller
import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for CombatSystem, focusing on the simplified combat system.
 */
class CombatSystemTest {

    private lateinit var combatSystem: CombatSystem
    private lateinit var testCharacter: Character
    private lateinit var testEnemy: Enemy

    @Before
    fun setup() {
        // Create a test character with moderate stats
        testCharacter = Character(
            id = "test-char",
            userId = "test-user",
            name = "Test Hero",
            level = 5,
            experience = 0,
            strength = 10,
            intelligence = 10,
            agility = 10,
            vitality = 10,
            luck = 10,
            currentHp = 100,
            maxHp = 100,
            gold = 1000,
            personalityTraits = PersonalityTraits(
                courage = 0.5f,
                greed = 0.5f,
                curiosity = 0.5f,
                aggression = 0.5f,
                social = 0.5f,
                impulsive = 0.5f
            ),
            jobClass = JobClass.WARRIOR
        )

        // Create a test enemy with similar stats
        testEnemy = Enemy(
            name = "Test Monster",
            level = 5,
            hp = 50,
            strength = 10,
            intelligence = 10,
            agility = 10,
            vitality = 10,
            luck = 0
        )
    }

    @Test
    fun `executeSimplifiedCombat should always WIN on critical roll 21`() {
        // Create dice roller that always rolls 21
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 21
        }
        combatSystem = CombatSystem(fixedDice)

        // Even against a much stronger enemy
        val strongEnemy = testEnemy.copy(
            level = 20,
            strength = 50,
            intelligence = 50,
            agility = 50,
            vitality = 50
        )

        val result = combatSystem.executeSimplifiedCombat(testCharacter, strongEnemy)

        assertEquals("Roll 21 should always WIN", CombatOutcome.WIN, result.outcome)
        assertEquals("Roll should be 21", 21, result.roll)
        assertNotNull("Should have rewards on win", result.rewards)
        assertEquals("Should have no gold lost on win", 0, result.goldLost)
        assertTrue("Should have bonus rewards on critical",
            result.rewards!!.experience > 0 && result.rewards!!.gold > 0)
    }

    @Test
    fun `executeSimplifiedCombat should always DEATH on critical roll 1`() {
        // Create dice roller that always rolls 1
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 1
        }
        combatSystem = CombatSystem(fixedDice)

        // Even against a much weaker enemy
        val weakEnemy = testEnemy.copy(
            level = 1,
            strength = 1,
            intelligence = 1,
            agility = 1,
            vitality = 1
        )

        val result = combatSystem.executeSimplifiedCombat(testCharacter, weakEnemy)

        assertEquals("Roll 1 should always DEATH", CombatOutcome.DEATH, result.outcome)
        assertEquals("Roll should be 1", 1, result.roll)
        assertNull("Should have no rewards on death", result.rewards)
        assertEquals("Should lose half gold on death", 500, result.goldLost)
    }

    @Test
    fun `executeSimplifiedCombat should WIN when combat score is 15 or higher`() {
        // Character power: (10+10+10+10)/4 + 5*2 = 10 + 10 = 20
        // Enemy power: (10+10+10+10)/4 + 5*2 = 10 + 10 = 20
        // Power difference: 0
        // Need roll of 15 to reach score of 15
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 15
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        assertEquals("Score 15 should WIN", CombatOutcome.WIN, result.outcome)
        assertEquals("Roll should be 15", 15, result.roll)
        assertEquals("Combat score should be 15.0", 15.0, result.combatScore, 0.01)
        assertNotNull("Should have rewards on win", result.rewards)
        assertEquals("Should have no gold lost on win", 0, result.goldLost)
    }

    @Test
    fun `executeSimplifiedCombat should FLEE when combat score is between 8 and 14`() {
        // Same power levels, roll 10 gives score 10 (in FLEE range)
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 10
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        assertEquals("Score 10 should FLEE", CombatOutcome.FLEE, result.outcome)
        assertEquals("Roll should be 10", 10, result.roll)
        assertEquals("Combat score should be 10.0", 10.0, result.combatScore, 0.01)
        assertNull("Should have no rewards on flee", result.rewards)
        assertEquals("Should have no gold lost on flee", 0, result.goldLost)
    }

    @Test
    fun `executeSimplifiedCombat should DEATH when combat score is below 8`() {
        // Same power levels, roll 5 gives score 5 (in DEATH range)
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 5
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        assertEquals("Score 5 should DEATH", CombatOutcome.DEATH, result.outcome)
        assertEquals("Roll should be 5", 5, result.roll)
        assertEquals("Combat score should be 5.0", 5.0, result.combatScore, 0.01)
        assertNull("Should have no rewards on death", result.rewards)
        assertEquals("Should lose half gold on death", 500, result.goldLost)
    }

    @Test
    fun `executeSimplifiedCombat should calculate power correctly`() {
        // Stronger character
        val strongCharacter = testCharacter.copy(
            level = 10,
            strength = 20,
            intelligence = 20,
            agility = 20,
            vitality = 20
        )

        // Roll 2 (lowest non-critical)
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 2
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(strongCharacter, testEnemy)

        // Character power: (20+20+20+20)/4 + 10*2 = 20 + 20 = 40
        // Enemy power: (10+10+10+10)/4 + 5*2 = 10 + 10 = 20
        // Power difference: 20
        // Combat score: 2 + 20 = 22
        assertEquals("Character power should be 40.0", 40.0, result.characterPower, 0.01)
        assertEquals("Enemy power should be 20.0", 20.0, result.enemyPower, 0.01)
        assertEquals("Combat score should be 22.0", 22.0, result.combatScore, 0.01)
        assertEquals("Strong character should WIN even with low roll", CombatOutcome.WIN, result.outcome)
    }

    @Test
    fun `executeSimplifiedCombat should give bonus rewards on critical success`() {
        // Roll 21 for critical success
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 21
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        // Get normal rewards for comparison
        val normalDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 15
        }
        val normalCombat = CombatSystem(normalDice)
        val normalResult = normalCombat.executeSimplifiedCombat(testCharacter, testEnemy)

        assertTrue("Critical rewards should be higher than normal",
            result.rewards!!.experience > normalResult.rewards!!.experience)
        assertTrue("Critical gold should be higher than normal",
            result.rewards!!.gold > normalResult.rewards!!.gold)
    }

    @Test
    fun `executeSimplifiedCombat should calculate gold loss correctly on death`() {
        // Test with different gold amounts
        val richCharacter = testCharacter.copy(gold = 2000)
        val poorCharacter = testCharacter.copy(gold = 100)

        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 1 // Critical failure
        }
        combatSystem = CombatSystem(fixedDice)

        val richResult = combatSystem.executeSimplifiedCombat(richCharacter, testEnemy)
        val poorResult = combatSystem.executeSimplifiedCombat(poorCharacter, testEnemy)

        assertEquals("Rich character should lose 1000 gold", 1000, richResult.goldLost)
        assertEquals("Poor character should lose 50 gold", 50, poorResult.goldLost)
    }

    @Test
    fun `executeSimplifiedCombat should handle edge case at score exactly 15`() {
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 15
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        assertEquals("Score exactly 15 should WIN", CombatOutcome.WIN, result.outcome)
    }

    @Test
    fun `executeSimplifiedCombat should handle edge case at score exactly 8`() {
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 8
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        assertEquals("Score exactly 8 should FLEE", CombatOutcome.FLEE, result.outcome)
    }

    @Test
    fun `executeSimplifiedCombat should include descriptive combat text`() {
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 15
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, testEnemy)

        assertNotNull("Description should not be null", result.description)
        assertTrue("Description should contain character name",
            result.description.contains(testCharacter.name))
        assertTrue("Description should contain enemy name",
            result.description.contains(testEnemy.name))
        assertTrue("Description should contain roll value",
            result.description.contains("15"))
    }

    @Test
    fun `executeSimplifiedCombat should handle very weak enemy`() {
        val veryWeakEnemy = testEnemy.copy(
            level = 1,
            strength = 1,
            intelligence = 1,
            agility = 1,
            vitality = 1
        )

        // Even with roll 2 (lowest non-critical), should still win
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 2
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, veryWeakEnemy)

        assertEquals("Should WIN against very weak enemy", CombatOutcome.WIN, result.outcome)
    }

    @Test
    fun `executeSimplifiedCombat should handle very strong enemy`() {
        val veryStrongEnemy = testEnemy.copy(
            level = 50,
            strength = 100,
            intelligence = 100,
            agility = 100,
            vitality = 100
        )

        // Even with roll 20 (highest non-critical), should probably die
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 20
        }
        combatSystem = CombatSystem(fixedDice)

        val result = combatSystem.executeSimplifiedCombat(testCharacter, veryStrongEnemy)

        // Character power: 20, Enemy power: 175, difference: -155, score: 20-155 = -135
        assertNotEquals("Should not WIN against vastly superior enemy (unless critical)",
            CombatOutcome.WIN, result.outcome)
    }
}

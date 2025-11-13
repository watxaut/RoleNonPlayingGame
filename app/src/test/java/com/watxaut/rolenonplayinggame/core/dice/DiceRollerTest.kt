package com.watxaut.rolenonplayinggame.core.dice

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for DiceRoller
 */
class DiceRollerTest {

    private lateinit var diceRoller: DiceRoller
    private lateinit var mockRandom: Random

    @Before
    fun setup() {
        mockRandom = Random(42) // Fixed seed for reproducible tests
        diceRoller = DiceRoller(mockRandom)
    }

    @Test
    fun `roll should return value between 1 and 21`() {
        repeat(100) {
            val result = diceRoller.roll()
            assertTrue("Roll should be >= 1", result >= 1)
            assertTrue("Roll should be <= 21", result <= 21)
        }
    }

    @Test
    fun `rollWithStat should return success when roll 21`() {
        // Create a dice roller that always rolls 21
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 21
        }

        val result = fixedDice.rollWithStat(stat = 1, difficulty = 100)

        assertTrue("Roll 21 should always succeed", result.success)
        assertTrue("Roll 21 should be critical success", result.isCriticalSuccess)
        assertEquals("Roll value should be 21", 21, result.value)
    }

    @Test
    fun `rollWithStat should return failure when roll 1`() {
        // Create a dice roller that always rolls 1
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 1
        }

        val result = fixedDice.rollWithStat(stat = 100, difficulty = 1)

        assertFalse("Roll 1 should always fail", result.success)
        assertTrue("Roll 1 should be critical failure", result.isCriticalFailure)
        assertEquals("Roll value should be 1", 1, result.value)
    }

    @Test
    fun `rollWithStat should calculate success correctly for normal rolls`() {
        // Create a dice roller that always rolls 10
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 10
        }

        // Test case: difficulty 15, stat 5, roll 10
        // Formula: difficulty - stat - roll <= 0
        // 15 - 5 - 10 = 0, should succeed
        val result1 = fixedDice.rollWithStat(stat = 5, difficulty = 15)
        assertTrue("Should succeed when difficulty - stat - roll = 0", result1.success)

        // Test case: difficulty 20, stat 5, roll 10
        // 20 - 5 - 10 = 5, should fail
        val result2 = fixedDice.rollWithStat(stat = 5, difficulty = 20)
        assertFalse("Should fail when difficulty - stat - roll > 0", result2.success)
    }

    @Test
    fun `rollWithLuck should reroll on 1 when luck is high enough`() {
        var rollCount = 0
        val customDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int {
                rollCount++
                return if (rollCount == 1) 1 else 15
            }
        }

        val result = customDice.rollWithLuck(stat = 10, difficulty = 20, luckStat = 10)

        assertTrue("Should have rerolled", result.wasRerolled)
        assertEquals("Roll value should be 15 after reroll", 15, result.value)
    }

    @Test
    fun `rollWithLuck should not reroll on 1 when luck is too low`() {
        val fixedDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int = 1
        }

        val result = fixedDice.rollWithLuck(stat = 10, difficulty = 20, luckStat = 9)

        assertFalse("Should not have rerolled", result.wasRerolled)
        assertEquals("Roll value should be 1", 1, result.value)
        assertTrue("Should be critical failure", result.isCriticalFailure)
    }

    @Test
    fun `rollWithLuck should expand crit range when luck is 15 or higher`() {
        // With luck 15, crit threshold is 21 - (15/5) = 18
        // So rolls 18, 19, 20, 21 should all be critical successes

        val critRolls = listOf(18, 19, 20, 21)
        critRolls.forEach { rollValue ->
            val fixedDice = object : DiceRoller(Random(0)) {
                override fun roll(): Int = rollValue
            }

            val result = fixedDice.rollWithLuck(stat = 1, difficulty = 100, luckStat = 15)

            assertTrue(
                "Roll $rollValue should be critical success with luck 15",
                result.isCriticalSuccess
            )
        }
    }

    @Test
    fun `rollMultiple should return correct number of rolls`() {
        val results = diceRoller.rollMultiple(5)

        assertEquals("Should return 5 rolls", 5, results.size)
        results.forEach { roll ->
            assertTrue("Each roll should be between 1 and 21", roll in 1..21)
        }
    }

    @Test
    fun `rollWithAdvantage should return the highest roll`() {
        var callCount = 0
        val customDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int {
                callCount++
                return if (callCount == 1) 5 else 15
            }
        }

        val result = customDice.rollWithAdvantage(2)

        assertEquals("Should return highest roll", 15, result)
    }

    @Test
    fun `rollWithDisadvantage should return the lowest roll`() {
        var callCount = 0
        val customDice = object : DiceRoller(Random(0)) {
            override fun roll(): Int {
                callCount++
                return if (callCount == 1) 5 else 15
            }
        }

        val result = customDice.rollWithDisadvantage(2)

        assertEquals("Should return lowest roll", 5, result)
    }

    @Test
    fun `RollResult should correctly identify critical rolls`() {
        val critSuccess = RollResult(value = 21, success = true, isCriticalSuccess = true)
        val critFailure = RollResult(value = 1, success = false, isCriticalFailure = true)
        val normalRoll = RollResult(value = 10, success = true)

        assertTrue("Should be critical success", critSuccess.isCriticalSuccess)
        assertTrue("Should be critical failure", critFailure.isCriticalFailure)
        assertFalse("Normal roll should not be critical", normalRoll.isCriticalSuccess)
        assertFalse("Normal roll should not be critical failure", normalRoll.isCriticalFailure)
    }
}

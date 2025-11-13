package com.watxaut.rolenonplayinggame.domain.model

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for PersonalityTraits
 */
class PersonalityTraitsTest {

    @Test
    fun `should create valid personality traits`() {
        val traits = PersonalityTraits(
            courage = 0.5f,
            greed = 0.3f,
            curiosity = 0.7f,
            aggression = 0.2f,
            social = 0.8f,
            impulsive = 0.4f
        )

        assertEquals("Courage should be 0.5", 0.5f, traits.courage, 0.01f)
        assertEquals("Greed should be 0.3", 0.3f, traits.greed, 0.01f)
        assertEquals("Curiosity should be 0.7", 0.7f, traits.curiosity, 0.01f)
        assertEquals("Aggression should be 0.2", 0.2f, traits.aggression, 0.01f)
        assertEquals("Social should be 0.8", 0.8f, traits.social, 0.01f)
        assertEquals("Impulsive should be 0.4", 0.4f, traits.impulsive, 0.01f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when courage is out of range`() {
        PersonalityTraits(
            courage = 1.5f,
            greed = 0.5f,
            curiosity = 0.5f,
            aggression = 0.5f,
            social = 0.5f,
            impulsive = 0.5f
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when greed is negative`() {
        PersonalityTraits(
            courage = 0.5f,
            greed = -0.1f,
            curiosity = 0.5f,
            aggression = 0.5f,
            social = 0.5f,
            impulsive = 0.5f
        )
    }

    @Test
    fun `random should generate valid traits`() {
        val random = Random(42)
        val traits = PersonalityTraits.random(random)

        assertTrue("Courage should be in range", traits.courage in 0f..1f)
        assertTrue("Greed should be in range", traits.greed in 0f..1f)
        assertTrue("Curiosity should be in range", traits.curiosity in 0f..1f)
        assertTrue("Aggression should be in range", traits.aggression in 0f..1f)
        assertTrue("Social should be in range", traits.social in 0f..1f)
        assertTrue("Impulsive should be in range", traits.impulsive in 0f..1f)
    }

    @Test
    fun `forJobClass should influence traits for Warrior`() {
        val random = Random(42)
        val traits = PersonalityTraits.forJobClass(JobClass.WARRIOR, random)

        // Warriors should have boosted courage and aggression
        assertTrue("Warriors should have decent courage", traits.courage >= 0.3f)
        assertTrue("Warriors should have decent aggression", traits.aggression >= 0.2f)
    }

    @Test
    fun `forJobClass should influence traits for Priest`() {
        val random = Random(42)
        val traits = PersonalityTraits.forJobClass(JobClass.PRIEST, random)

        // Priests should have reduced greed
        assertTrue("Priests should have lower greed", traits.greed <= 0.7f)
    }

    @Test
    fun `getRiskTolerance should calculate correctly`() {
        val cautiousTraits = PersonalityTraits(
            courage = 0.1f,
            greed = 0.1f,
            curiosity = 0.5f,
            aggression = 0.5f,
            social = 0.5f,
            impulsive = 0.1f
        )

        val recklessTraits = PersonalityTraits(
            courage = 0.9f,
            greed = 0.9f,
            curiosity = 0.5f,
            aggression = 0.5f,
            social = 0.5f,
            impulsive = 0.9f
        )

        assertTrue(
            "Reckless character should have higher risk tolerance",
            recklessTraits.getRiskTolerance() > cautiousTraits.getRiskTolerance()
        )
    }

    @Test
    fun `getSocialCompatibility should return high value for similar personalities`() {
        val traits1 = PersonalityTraits(
            courage = 0.5f,
            greed = 0.5f,
            curiosity = 0.5f,
            aggression = 0.5f,
            social = 0.5f,
            impulsive = 0.5f
        )

        val traits2 = PersonalityTraits(
            courage = 0.5f,
            greed = 0.5f,
            curiosity = 0.5f,
            aggression = 0.5f,
            social = 0.5f,
            impulsive = 0.5f
        )

        val compatibility = traits1.getSocialCompatibility(traits2)

        assertEquals("Identical personalities should have 1.0 compatibility", 1.0f, compatibility, 0.01f)
    }

    @Test
    fun `getSocialCompatibility should return low value for opposite personalities`() {
        val traits1 = PersonalityTraits(
            courage = 0.0f,
            greed = 0.0f,
            curiosity = 0.0f,
            aggression = 0.0f,
            social = 0.0f,
            impulsive = 0.0f
        )

        val traits2 = PersonalityTraits(
            courage = 1.0f,
            greed = 1.0f,
            curiosity = 1.0f,
            aggression = 1.0f,
            social = 1.0f,
            impulsive = 1.0f
        )

        val compatibility = traits1.getSocialCompatibility(traits2)

        assertEquals("Opposite personalities should have 0.0 compatibility", 0.0f, compatibility, 0.01f)
    }

    @Test
    fun `getSocialCompatibility should return medium value for somewhat different personalities`() {
        val traits1 = PersonalityTraits(
            courage = 0.3f,
            greed = 0.4f,
            curiosity = 0.5f,
            aggression = 0.2f,
            social = 0.6f,
            impulsive = 0.3f
        )

        val traits2 = PersonalityTraits(
            courage = 0.7f,
            greed = 0.6f,
            curiosity = 0.5f,
            aggression = 0.8f,
            social = 0.4f,
            impulsive = 0.7f
        )

        val compatibility = traits1.getSocialCompatibility(traits2)

        assertTrue("Different personalities should have medium compatibility", compatibility in 0.3f..0.7f)
    }
}

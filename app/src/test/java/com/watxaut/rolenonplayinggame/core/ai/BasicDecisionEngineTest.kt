package com.watxaut.rolenonplayinggame.core.ai

import com.watxaut.rolenonplayinggame.domain.model.Character
import com.watxaut.rolenonplayinggame.domain.model.CharacterStats
import com.watxaut.rolenonplayinggame.domain.model.JobClass
import com.watxaut.rolenonplayinggame.domain.model.PersonalityTraits
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for BasicDecisionEngine
 */
class BasicDecisionEngineTest {

    private lateinit var engine: BasicDecisionEngine
    private lateinit var testCharacter: Character

    @Before
    fun setup() {
        engine = BasicDecisionEngine()

        testCharacter = Character.create(
            userId = "test-user",
            name = "Test Hero",
            jobClass = JobClass.WARRIOR,
            initialStats = CharacterStats.default().toMap()
        )
    }

    @Test
    fun `makeDecision should prioritize survival when HP is low`() {
        val lowHealthCharacter = testCharacter.copy(
            currentHp = 20,  // 20% of default maxHp (60)
            maxHp = 100
        )

        val context = DecisionContext(
            currentLocation = "test_location",
            currentHp = lowHealthCharacter.currentHp,
            maxHp = lowHealthCharacter.maxHp,
            gold = 100L,
            level = 1,
            hasInnAccess = true
        )

        val decision = engine.makeDecision(lowHealthCharacter, context)

        assertTrue(
            "Should choose healing decision when HP is low",
            decision is Decision.HealAtInn || decision is Decision.Rest || decision is Decision.Flee
        )
    }

    @Test
    fun `makeDecision should heal at inn when available and affordable`() {
        val lowHealthCharacter = testCharacter.copy(
            currentHp = 25,
            maxHp = 100
        )

        val context = DecisionContext(
            currentLocation = "town",
            currentHp = 25,
            maxHp = 100,
            gold = 50L,
            level = 1,
            hasInnAccess = true,
            innHealCost = 10L
        )

        val decision = engine.makeDecision(lowHealthCharacter, context)

        assertTrue(
            "Should heal at inn when affordable",
            decision is Decision.HealAtInn
        )
    }

    @Test
    fun `makeDecision should rest when cannot afford inn`() {
        val lowHealthCharacter = testCharacter.copy(
            currentHp = 25,
            maxHp = 100
        )

        val context = DecisionContext(
            currentLocation = "town",
            currentHp = 25,
            maxHp = 100,
            gold = 5L, // Not enough for inn
            level = 1,
            hasInnAccess = true,
            innHealCost = 10L,
            canRest = true
        )

        val decision = engine.makeDecision(lowHealthCharacter, context)

        assertTrue(
            "Should rest when cannot afford inn",
            decision is Decision.Rest
        )
    }

    @Test
    fun `makeDecision should handle critical needs when resources are low`() {
        val context = DecisionContext(
            currentLocation = "test_location",
            currentHp = 80,
            maxHp = 100,
            gold = 30L, // Low resources
            level = 1
        )

        val decision = engine.makeDecision(testCharacter, context)

        // Should try to gather resources (explore or combat)
        assertTrue(
            "Should try to gather resources when low on gold",
            decision is Decision.Explore || decision is Decision.Combat
        )
    }

    @Test
    fun `makeDecision should continue quest when active`() {
        val context = DecisionContext(
            currentLocation = "test_location",
            currentHp = 100,
            maxHp = 100,
            gold = 100L,
            level = 1,
            hasActiveQuest = true
        )

        val decision = engine.makeDecision(testCharacter, context)

        assertTrue(
            "Should continue quest when one is active",
            decision is Decision.ContinueQuest
        )
    }

    @Test
    fun `DecisionContext isInDanger should return true when HP below 30 percent`() {
        val context = DecisionContext(
            currentLocation = "test",
            currentHp = 29,
            maxHp = 100,
            gold = 50L,
            level = 1
        )

        assertTrue("Should be in danger at 29% HP", context.isInDanger())
    }

    @Test
    fun `DecisionContext isInDanger should return false when HP above 30 percent`() {
        val context = DecisionContext(
            currentLocation = "test",
            currentHp = 31,
            maxHp = 100,
            gold = 50L,
            level = 1
        )

        assertFalse("Should not be in danger at 31% HP", context.isInDanger())
    }

    @Test
    fun `DecisionContext isLowResources should return true when gold below 50`() {
        val context = DecisionContext(
            currentLocation = "test",
            currentHp = 100,
            maxHp = 100,
            gold = 40L,
            level = 1
        )

        assertTrue("Should be low on resources with 40 gold", context.isLowResources())
    }

    @Test
    fun `evaluateDecisionFit should rate combat higher for aggressive personality`() {
        val aggressivePersonality = PersonalityTraits(
            courage = 0.8f,
            greed = 0.5f,
            curiosity = 0.5f,
            aggression = 0.9f, // High aggression
            social = 0.5f,
            impulsive = 0.5f
        )

        val combatDecision = Decision.Combat("Slime", 1)
        val restDecision = Decision.Rest("town")

        val combatFit = engine.evaluateDecisionFit(combatDecision, aggressivePersonality)
        val restFit = engine.evaluateDecisionFit(restDecision, aggressivePersonality)

        assertTrue(
            "Combat should fit better for aggressive character",
            combatFit > restFit
        )
    }

    @Test
    fun `evaluateDecisionFit should rate explore higher for curious personality`() {
        val curiousPersonality = PersonalityTraits(
            courage = 0.5f,
            greed = 0.5f,
            curiosity = 0.9f, // High curiosity
            aggression = 0.3f,
            social = 0.5f,
            impulsive = 0.5f
        )

        val exploreDecision = Decision.Explore("new_location")
        val idleDecision = Decision.Idle

        val exploreFit = engine.evaluateDecisionFit(exploreDecision, curiousPersonality)
        val idleFit = engine.evaluateDecisionFit(idleDecision, curiousPersonality)

        assertTrue(
            "Explore should fit better for curious character",
            exploreFit > idleFit
        )
    }

    @Test
    fun `evaluateDecisionFit should rate flee higher for cautious personality`() {
        val cautiousPersonality = PersonalityTraits(
            courage = 0.1f, // Low courage (cautious)
            greed = 0.5f,
            curiosity = 0.5f,
            aggression = 0.2f,
            social = 0.5f,
            impulsive = 0.2f
        )

        val fleeDecision = Decision.Flee("danger")
        val combatDecision = Decision.Combat("Dragon", 50)

        val fleeFit = engine.evaluateDecisionFit(fleeDecision, cautiousPersonality)
        val combatFit = engine.evaluateDecisionFit(combatDecision, cautiousPersonality)

        assertTrue(
            "Flee should fit better for cautious character",
            fleeFit > combatFit
        )
    }

    @Test
    fun `DecisionPriority getBasicPriorities should return only top 3 priorities`() {
        val basicPriorities = DecisionPriority.getBasicPriorities()

        assertEquals("Should return 3 priorities", 3, basicPriorities.size)
        assertTrue(
            "Should include SURVIVAL",
            basicPriorities.contains(DecisionPriority.SURVIVAL)
        )
        assertTrue(
            "Should include CRITICAL_NEEDS",
            basicPriorities.contains(DecisionPriority.CRITICAL_NEEDS)
        )
        assertTrue(
            "Should include ACTIVE_QUEST",
            basicPriorities.contains(DecisionPriority.ACTIVE_QUEST)
        )
    }
}

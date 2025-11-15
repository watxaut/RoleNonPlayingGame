package com.watxaut.rolenonplayinggame.core.combat

import com.watxaut.rolenonplayinggame.core.dice.DiceRoller
import com.watxaut.rolenonplayinggame.core.messages.MessageProvider
import com.watxaut.rolenonplayinggame.domain.model.Character
import kotlin.math.max
import kotlin.random.Random

/**
 * Combat System using the d21 mechanic.
 *
 * Per FUNCTIONAL_DESIGN_DOCUMENT.md section 3.2 (d21 dice mechanic):
 * - Roll 21 = automatic critical success (2x damage)
 * - Roll 1 = automatic critical failure (miss)
 * - Rolls 2-20 = success/failure based on Difficulty - Stat - Roll ≤ 0
 *
 * Combat flow:
 * 1. Attacker rolls to hit (AGI-based for attack accuracy)
 * 2. If hit, roll damage based on STR (physical) or INT (magical)
 * 3. Defender can dodge (AGI-based) or reduce damage (VIT-based)
 * 4. Apply damage to HP
 * 5. Check for victory/defeat
 */
class CombatSystem(
    private val diceRoller: DiceRoller = DiceRoller(),
    private val messageProvider: MessageProvider = MessageProvider()
) {

    /**
     * Execute a single combat round between two combatants.
     *
     * @param attacker The attacking character
     * @param defender The defending character/enemy
     * @param attackerLevel Level of attacker (for difficulty calculation)
     * @param defenderLevel Level of defender (for difficulty calculation)
     * @return CombatResult with detailed outcome
     */
    fun executeAttack(
        attacker: Combatant,
        defender: Combatant,
        attackerLevel: Int = 1,
        defenderLevel: Int = 1
    ): CombatResult {
        // 1. Attack Roll (AGI-based to-hit check)
        val attackDifficulty = calculateAttackDifficulty(defenderLevel, defender.agility)
        val attackRoll = diceRoller.rollWithLuck(
            stat = attacker.agility,
            difficulty = attackDifficulty,
            luckStat = attacker.luck
        )

        // If attack misses, return miss result
        if (!attackRoll.success) {
            return CombatResult(
                hit = false,
                damage = 0,
                attackRoll = attackRoll.value,
                isCriticalHit = false,
                isCriticalMiss = attackRoll.isCriticalFailure,
                defenderDodged = false,
                description = if (attackRoll.isCriticalFailure) {
                    "${attacker.name} completely whiffs the attack!"
                } else {
                    "${attacker.name} misses ${defender.name}."
                }
            )
        }

        // 2. Defender Dodge Check (if attacker hit)
        // Dodge difficulty scales with attacker's agility
        val dodgeDifficulty = calculateDodgeDifficulty(attackerLevel, attacker.agility)
        val dodgeRoll = diceRoller.rollWithLuck(
            stat = defender.agility,
            difficulty = dodgeDifficulty,
            luckStat = defender.luck
        )

        // If defender dodges, return dodge result
        if (dodgeRoll.success) {
            return CombatResult(
                hit = false,
                damage = 0,
                attackRoll = attackRoll.value,
                dodgeRoll = dodgeRoll.value,
                isCriticalHit = false,
                isCriticalMiss = false,
                defenderDodged = true,
                description = "${defender.name} dodges ${attacker.name}'s attack!"
            )
        }

        // 3. Calculate Damage
        val baseDamage = calculateBaseDamage(attacker, defender, attackerLevel)
        val finalDamage = if (attackRoll.isCriticalSuccess) {
            baseDamage * 2 // Critical hit = 2x damage
        } else {
            baseDamage
        }

        // 4. Apply damage reduction based on defender's vitality
        val damageReduction = calculateDamageReduction(defender.vitality)
        val actualDamage = max(1, finalDamage - damageReduction) // Always at least 1 damage

        // 5. Build result
        val description = when {
            attackRoll.isCriticalSuccess -> {
                "${attacker.name} lands a CRITICAL HIT on ${defender.name} for $actualDamage damage!"
            }
            else -> {
                "${attacker.name} hits ${defender.name} for $actualDamage damage."
            }
        }

        return CombatResult(
            hit = true,
            damage = actualDamage,
            attackRoll = attackRoll.value,
            dodgeRoll = dodgeRoll.value,
            isCriticalHit = attackRoll.isCriticalSuccess,
            isCriticalMiss = false,
            defenderDodged = false,
            damageReduction = damageReduction,
            description = description
        )
    }

    /**
     * Execute a full combat encounter between a character and an enemy.
     * Combatants take turns until one is defeated.
     *
     * @param character The player's character
     * @param enemy The enemy combatant
     * @return CombatEncounterResult with full combat log
     */
    fun executeEncounter(
        character: Character,
        enemy: Enemy
    ): CombatEncounterResult {
        val combatLog = mutableListOf<String>()
        val rounds = mutableListOf<CombatRound>()

        var characterHp = character.currentHp
        var enemyHp = enemy.hp

        var roundNumber = 1
        val maxRounds = 50 // Prevent infinite loops

        combatLog.add("Combat begins: ${character.name} vs ${enemy.name}!")
        combatLog.add("${character.name}: $characterHp HP | ${enemy.name}: $enemyHp HP")
        combatLog.add("")

        // Combat loop: character and enemy alternate attacks
        while (characterHp > 0 && enemyHp > 0 && roundNumber <= maxRounds) {
            combatLog.add("=== Round $roundNumber ===")

            // Character attacks first (player advantage)
            val characterAttack = executeAttack(
                attacker = Combatant(
                    name = character.name,
                    strength = character.strength,
                    intelligence = character.intelligence,
                    agility = character.agility,
                    luck = character.luck,
                    vitality = character.vitality
                ),
                defender = Combatant(
                    name = enemy.name,
                    strength = enemy.strength,
                    intelligence = enemy.intelligence,
                    agility = enemy.agility,
                    luck = enemy.luck,
                    vitality = enemy.vitality
                ),
                attackerLevel = character.level,
                defenderLevel = enemy.level
            )

            combatLog.add(characterAttack.description)
            if (characterAttack.hit) {
                enemyHp -= characterAttack.damage
            }

            // Check if enemy defeated
            if (enemyHp <= 0) {
                combatLog.add("")
                combatLog.add("${enemy.name} has been defeated!")
                rounds.add(CombatRound(roundNumber, characterAttack, null))
                break
            }

            // Enemy attacks back
            val enemyAttack = executeAttack(
                attacker = Combatant(
                    name = enemy.name,
                    strength = enemy.strength,
                    intelligence = enemy.intelligence,
                    agility = enemy.agility,
                    luck = enemy.luck,
                    vitality = enemy.vitality
                ),
                defender = Combatant(
                    name = character.name,
                    strength = character.strength,
                    intelligence = character.intelligence,
                    agility = character.agility,
                    luck = character.luck,
                    vitality = character.vitality
                ),
                attackerLevel = enemy.level,
                defenderLevel = character.level
            )

            combatLog.add(enemyAttack.description)
            if (enemyAttack.hit) {
                characterHp -= enemyAttack.damage
            }

            // Check if character defeated
            if (characterHp <= 0) {
                combatLog.add("")
                combatLog.add("${character.name} has been defeated!")
                rounds.add(CombatRound(roundNumber, characterAttack, enemyAttack))
                break
            }

            combatLog.add("${character.name}: $characterHp HP | ${enemy.name}: $enemyHp HP")
            combatLog.add("")

            rounds.add(CombatRound(roundNumber, characterAttack, enemyAttack))
            roundNumber++
        }

        val victory = characterHp > 0
        val rewards = if (victory) {
            CombatRewards(
                experience = calculateExperienceReward(enemy.level, character.level),
                gold = calculateGoldReward(enemy.level)
            )
        } else {
            null
        }

        if (victory && rewards != null) {
            combatLog.add("Victory! Gained ${rewards.experience} XP and ${rewards.gold} gold.")
        }

        return CombatEncounterResult(
            victory = victory,
            characterFinalHp = max(0, characterHp),
            enemyFinalHp = max(0, enemyHp),
            rounds = rounds,
            combatLog = combatLog,
            rewards = rewards,
            totalRounds = roundNumber - 1
        )
    }

    /**
     * Execute simplified combat encounter with a single d21 roll.
     *
     * Formula:
     * 1. Roll d21
     * 2. Calculate power: (STR + INT + AGI + VIT) / 4 + Level * 2
     * 3. Combat Score = Roll + (CharacterPower - EnemyPower) + LevelBonus
     *
     * Outcomes:
     * - Roll 21: Critical Success → WIN (bonus rewards)
     * - Roll 1: Critical Failure → DEATH
     * - Score >= 12: WIN (lowered from 15 for better balance)
     * - Score >= 7: FLEE (lowered from 8 for better balance)
     * - Score < 7: DEATH (lose half gold on respawn)
     *
     * Low level characters (1-3) get a +2 combat bonus to help early survival.
     *
     * @param character The player's character
     * @param enemy The enemy to fight
     * @return SimplifiedCombatResult with outcome and details
     */
    fun executeSimplifiedCombat(
        character: Character,
        enemy: Enemy
    ): SimplifiedCombatResult {
        val roll = diceRoller.roll()

        // Get character's total stats including equipment bonuses
        val totalStats = character.getTotalStats()

        // Calculate power levels
        val characterPower = calculatePower(
            strength = totalStats.strength,
            intelligence = totalStats.intelligence,
            agility = totalStats.agility,
            vitality = totalStats.vitality,
            level = character.level
        )

        val enemyPower = calculatePower(
            strength = enemy.strength,
            intelligence = enemy.intelligence,
            agility = enemy.agility,
            vitality = enemy.vitality,
            level = enemy.level
        )

        val powerDifference = characterPower - enemyPower

        // Give low-level characters a small combat bonus to improve early survival
        val levelBonus = if (character.level <= 3) 2.0 else 0.0

        val combatScore = roll.toDouble() + powerDifference + levelBonus

        // Determine outcome (more forgiving thresholds)
        val outcome = when {
            roll == 21 -> CombatOutcome.WIN // Critical success
            roll == 1 -> CombatOutcome.DEATH // Critical failure
            combatScore >= 12.0 -> CombatOutcome.WIN // Lowered from 15
            combatScore >= 7.0 -> CombatOutcome.FLEE // Lowered from 8
            else -> CombatOutcome.DEATH
        }

        // Calculate rewards and item drops for wins
        val (rewards, droppedEquipment) = if (outcome == CombatOutcome.WIN) {
            val baseXp = calculateExperienceReward(enemy.level, character.level)
            val baseGold = calculateGoldReward(enemy.level)

            // Bonus rewards on critical success (roll 21)
            val bonusMultiplier = if (roll == 21) 1.5 else 1.0

            val combatRewards = CombatRewards(
                experience = (baseXp * bonusMultiplier).toInt(),
                gold = (baseGold * bonusMultiplier).toInt()
            )

            // Check for equipment drop (using EquipmentDatabase)
            val droppedItem = com.watxaut.rolenonplayinggame.data.EquipmentDatabase.getRandomDrop()

            Pair(combatRewards, droppedItem)
        } else {
            Pair(null, null)
        }

        // Calculate gold penalty for death
        val goldLost = if (outcome == CombatOutcome.DEATH) {
            (character.gold / 2).toInt()
        } else {
            0
        }

        // Generate combat description
        val description = generateCombatDescription(
            characterName = character.name,
            enemyName = enemy.name,
            outcome = outcome,
            roll = roll,
            combatScore = combatScore,
            isCritical = roll == 21 || roll == 1
        )

        return SimplifiedCombatResult(
            outcome = outcome,
            roll = roll,
            combatScore = combatScore,
            characterPower = characterPower,
            enemyPower = enemyPower,
            rewards = rewards,
            goldLost = goldLost,
            description = description,
            droppedEquipment = droppedEquipment
        )
    }

    /**
     * Calculate overall power level for a combatant.
     */
    private fun calculatePower(
        strength: Int,
        intelligence: Int,
        agility: Int,
        vitality: Int,
        level: Int
    ): Double {
        val avgStat = (strength + intelligence + agility + vitality) / 4.0
        return avgStat + (level * 2)
    }

    /**
     * Generate descriptive text for combat outcome using MessageProvider.
     */
    private fun generateCombatDescription(
        characterName: String,
        enemyName: String,
        outcome: CombatOutcome,
        roll: Int,
        combatScore: Double,
        isCritical: Boolean
    ): String {
        // Get fun, varied message from MessageProvider
        val baseMessage = messageProvider.getCombatMessage(
            outcome = outcome,
            characterName = characterName,
            enemyName = enemyName,
            wasCritical = isCritical
        )

        // Append technical details for debugging/info
        return "$baseMessage (Rolled: $roll, Score: ${"%.1f".format(combatScore)})"
    }

    /**
     * Calculate attack difficulty based on defender's level and agility.
     * Higher difficulty = harder to hit.
     */
    private fun calculateAttackDifficulty(defenderLevel: Int, defenderAgility: Int): Int {
        return 10 + (defenderLevel / 2) + (defenderAgility / 3)
    }

    /**
     * Calculate dodge difficulty based on attacker's level and agility.
     * Higher difficulty = harder to dodge.
     */
    private fun calculateDodgeDifficulty(attackerLevel: Int, attackerAgility: Int): Int {
        return 12 + (attackerLevel / 2) + (attackerAgility / 2)
    }

    /**
     * Calculate base damage based on attacker's stats.
     * Physical attackers use STR, magical attackers use INT.
     */
    private fun calculateBaseDamage(
        attacker: Combatant,
        defender: Combatant,
        attackerLevel: Int
    ): Int {
        // Determine if physical or magical attack (use whichever stat is higher)
        val physicalDamage = (attacker.strength * 2) + (attackerLevel / 2)
        val magicalDamage = (attacker.intelligence * 2) + (attackerLevel / 2)

        // Use the higher damage type
        val baseDamage = max(physicalDamage, magicalDamage)

        // Add some randomness (±20%)
        val randomFactor = Random.nextDouble(0.8, 1.2)
        return max(1, (baseDamage * randomFactor).toInt())
    }

    /**
     * Calculate damage reduction based on defender's vitality.
     * Higher vitality = more damage blocked.
     */
    private fun calculateDamageReduction(vitality: Int): Int {
        return vitality / 2
    }

    /**
     * Calculate experience reward for defeating an enemy.
     * Based on enemy level relative to character level.
     */
    private fun calculateExperienceReward(enemyLevel: Int, characterLevel: Int): Int {
        val baseXp = enemyLevel * 10
        val levelDifference = enemyLevel - characterLevel

        // Bonus for fighting higher level enemies, penalty for lower
        val multiplier = when {
            levelDifference >= 3 -> 1.5
            levelDifference >= 1 -> 1.2
            levelDifference <= -3 -> 0.5
            levelDifference <= -1 -> 0.8
            else -> 1.0
        }

        return (baseXp * multiplier).toInt()
    }

    /**
     * Calculate gold reward for defeating an enemy.
     */
    private fun calculateGoldReward(enemyLevel: Int): Int {
        val baseGold = enemyLevel * 5
        val randomFactor = Random.nextDouble(0.8, 1.5)
        return (baseGold * randomFactor).toInt()
    }
}

/**
 * Represents a combatant in battle.
 * Can be a character or an enemy.
 */
data class Combatant(
    val name: String,
    val strength: Int,
    val intelligence: Int,
    val agility: Int,
    val luck: Int,
    val vitality: Int
)

/**
 * Represents an enemy that the character can fight.
 */
data class Enemy(
    val name: String,
    val level: Int,
    val hp: Int,
    val strength: Int,
    val intelligence: Int,
    val agility: Int,
    val luck: Int,
    val vitality: Int,
    val enemyType: EnemyType = EnemyType.NORMAL
)

enum class EnemyType {
    NORMAL,      // Regular monsters
    ELITE,       // Tougher than normal, special encounters
    BOSS,        // Major boss fight, dungeon guardians
    WORLD_BOSS,  // Legendary world bosses (monthly respawn)
    LEGENDARY    // Mythical entities (unconfirmed/special events)
}

/**
 * Simplified combat outcome.
 */
enum class CombatOutcome {
    WIN,    // Character wins, gains rewards
    FLEE,   // Character escapes but gets no rewards
    DEATH   // Character dies, respawns with gold penalty
}

/**
 * Result of a single attack in combat.
 */
data class CombatResult(
    val hit: Boolean,
    val damage: Int,
    val attackRoll: Int,
    val dodgeRoll: Int? = null,
    val isCriticalHit: Boolean,
    val isCriticalMiss: Boolean,
    val defenderDodged: Boolean,
    val damageReduction: Int = 0,
    val description: String
)

/**
 * Represents a single round of combat (both combatants attack).
 */
data class CombatRound(
    val roundNumber: Int,
    val characterAttack: CombatResult,
    val enemyAttack: CombatResult?
)

/**
 * Result of a full combat encounter.
 */
data class CombatEncounterResult(
    val victory: Boolean,
    val characterFinalHp: Int,
    val enemyFinalHp: Int,
    val rounds: List<CombatRound>,
    val combatLog: List<String>,
    val rewards: CombatRewards?,
    val totalRounds: Int
)

/**
 * Rewards from winning a combat encounter.
 */
data class CombatRewards(
    val experience: Int,
    val gold: Int,
    val items: List<String> = emptyList() // Future: loot drops
)

/**
 * Result of a simplified combat encounter (single d21 roll).
 */
data class SimplifiedCombatResult(
    val outcome: CombatOutcome,
    val roll: Int,
    val combatScore: Double,
    val characterPower: Double,
    val enemyPower: Double,
    val rewards: CombatRewards?,
    val goldLost: Int,
    val description: String,
    val droppedEquipment: com.watxaut.rolenonplayinggame.domain.model.Equipment? = null
)

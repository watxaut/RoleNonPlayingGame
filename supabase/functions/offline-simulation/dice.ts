// d21 Dice Rolling System
// Matches the implementation from Kotlin DiceRoller.kt

export interface DiceRollResult {
  roll: number;
  isNaturalSuccess: boolean; // rolled 21
  isNaturalFailure: boolean; // rolled 1
  succeeded: boolean; // overall success/failure
  description: string;
}

/**
 * Roll a d21 dice
 * @returns a number between 1 and 21 (inclusive)
 */
export function rollD21(): number {
  return Math.floor(Math.random() * 21) + 1;
}

/**
 * Perform a d21 skill check
 * Success formula: Difficulty - Stat - Roll <= 0
 *
 * @param stat - The character's relevant stat (STR, INT, AGI, etc.)
 * @param difficulty - The difficulty of the task (higher = harder)
 * @param luck - The character's luck stat (affects natural 1 rerolls)
 * @returns DiceRollResult with outcome and description
 */
export function performSkillCheck(
  stat: number,
  difficulty: number,
  luck: number = 1
): DiceRollResult {
  let roll = rollD21();
  let rerolled = false;

  // Natural 1: Critical failure (but luck can allow reroll)
  if (roll === 1) {
    // Luck stat gives chance to reroll on natural 1
    // Luck 1-5: 0%, 6-10: 20%, 11-15: 40%, 16-20: 60%, 21+: 80%
    const rerollChance = Math.max(0, (luck - 5) * 0.2);
    if (Math.random() < rerollChance) {
      roll = rollD21();
      rerolled = true;
    } else {
      return {
        roll: 1,
        isNaturalSuccess: false,
        isNaturalFailure: true,
        succeeded: false,
        description: rerolled
          ? "Rolled 1, rerolled but still failed"
          : "Rolled 1 - Critical Failure!",
      };
    }
  }

  // Natural 21: Critical success
  if (roll === 21) {
    return {
      roll: 21,
      isNaturalSuccess: true,
      isNaturalFailure: false,
      succeeded: true,
      description: "Rolled 21 - Critical Success!",
    };
  }

  // Regular roll (2-20): Check if successful
  // Success if: Difficulty - Stat - Roll <= 0
  const checkValue = difficulty - stat - roll;
  const succeeded = checkValue <= 0;

  return {
    roll,
    isNaturalSuccess: false,
    isNaturalFailure: false,
    succeeded,
    description: succeeded
      ? `Rolled ${roll} - Success! (${difficulty} - ${stat} - ${roll} = ${checkValue})`
      : `Rolled ${roll} - Failure (${difficulty} - ${stat} - ${roll} = ${checkValue})`,
  };
}

/**
 * Perform an attack roll in combat
 * @param attackerStat - Attacker's relevant stat (STR for physical, INT for magic)
 * @param defenderDefense - Defender's defense value
 * @param luck - Attacker's luck stat
 * @returns DiceRollResult
 */
export function performAttackRoll(
  attackerStat: number,
  defenderDefense: number,
  luck: number = 1
): DiceRollResult {
  return performSkillCheck(attackerStat, defenderDefense, luck);
}

/**
 * Calculate critical hit damage multiplier based on luck
 * Higher luck = higher crit multiplier
 */
export function getCriticalMultiplier(luck: number): number {
  // Base crit: 2x
  // +0.1x per 5 luck (max +1.0x at luck 50)
  return 2.0 + Math.floor(luck / 5) * 0.1;
}

/**
 * Calculate if an attack is a critical hit (on natural 21)
 * or expanded crit range based on luck
 */
export function isCriticalHit(roll: number, luck: number): boolean {
  // Natural 21 is always a crit
  if (roll === 21) return true;

  // Luck expands crit range
  // Luck 20+: 19-21 (15% crit chance)
  // Luck 30+: 18-21 (20% crit chance)
  // Luck 40+: 17-21 (25% crit chance)
  if (luck >= 40 && roll >= 17) return true;
  if (luck >= 30 && roll >= 18) return true;
  if (luck >= 20 && roll >= 19) return true;

  return false;
}

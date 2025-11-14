// Probability-based combat system for offline simulation
// Simplified version of the full combat system for performance

import { performAttackRoll, getCriticalMultiplier, isCriticalHit } from "./dice.ts";
import type { Character, Enemy, CombatResult } from "./types.ts";

/**
 * Get appropriate attack stat based on job class
 */
function getAttackStat(character: Character): number {
  const jobClass = character.job_class.toLowerCase();

  // Physical attackers use STR
  if (
    ["warrior", "assassin", "rogue", "archer", "paladin", "ranger"].includes(
      jobClass
    )
  ) {
    return character.strength;
  }

  // Magic users use INT
  if (["mage", "priest", "warlock", "battle mage"].includes(jobClass)) {
    return character.intelligence;
  }

  // Hybrid/special classes
  if (jobClass === "bard") {
    return Math.max(character.charisma, character.intelligence);
  }

  if (jobClass === "scholar") {
    return character.intelligence;
  }

  if (jobClass === "merchant") {
    return Math.max(character.charisma, character.agility);
  }

  // Default to strength
  return character.strength;
}

/**
 * Calculate character's defense value
 * Based on agility and vitality
 */
function getDefenseValue(character: Character): number {
  // Defense = (AGI + VIT) / 2
  return Math.floor((character.agility + character.vitality) / 2);
}

/**
 * Calculate base damage for an attack
 */
function calculateBaseDamage(
  attackStat: number,
  level: number,
  jobClass: string
): number {
  // Base damage = attackStat + (level * 2)
  let damage = attackStat + level * 2;

  // Job class damage modifiers
  const jobClassLower = jobClass.toLowerCase();
  if (["warrior", "paladin"].includes(jobClassLower)) {
    damage *= 1.2; // 20% more damage for tanks
  } else if (["assassin", "rogue"].includes(jobClassLower)) {
    damage *= 1.3; // 30% more damage for assassins
  } else if (["mage", "warlock"].includes(jobClassLower)) {
    damage *= 1.25; // 25% more damage for mages
  }

  return Math.floor(damage);
}

/**
 * Simulate a single combat encounter
 * Uses probability-based resolution for speed
 */
export function simulateCombat(
  character: Character,
  enemy: Enemy
): CombatResult {
  const characterAttack = getAttackStat(character);
  const characterDefense = getDefenseValue(character);

  // Perform attack roll
  const attackRoll = performAttackRoll(
    characterAttack,
    enemy.defense,
    character.luck
  );

  // If attack fails, character takes damage but doesn't deal any
  if (!attackRoll.succeeded) {
    const enemyDamage = Math.max(1, enemy.attack - characterDefense);
    return {
      victory: false,
      xpGained: 0,
      goldGained: 0,
      hpLost: enemyDamage,
      itemsFound: [],
      description: `${character.name} attacked ${enemy.name} but missed! Took ${enemyDamage} damage.`,
    };
  }

  // Calculate damage dealt
  let damage = calculateBaseDamage(
    characterAttack,
    character.level,
    character.job_class
  );

  // Check for critical hit
  const isCrit = isCriticalHit(attackRoll.roll, character.luck);
  if (isCrit) {
    const critMultiplier = getCriticalMultiplier(character.luck);
    damage = Math.floor(damage * critMultiplier);
  }

  // Simplified combat resolution
  // Instead of turn-by-turn, calculate probability of victory
  const characterPower = characterAttack + characterDefense + character.level * 2;
  const enemyPower = enemy.attack + enemy.defense + enemy.level * 2;

  const winProbability = characterPower / (characterPower + enemyPower);

  // Roll for victory
  const victoryRoll = Math.random();
  const victory = victoryRoll < winProbability;

  if (victory) {
    // Victory - take some damage but kill enemy
    const damageRatio = enemyPower / characterPower;
    const hpLost = Math.floor(
      (character.max_hp * damageRatio * 0.3) + (Math.random() * 10)
    );

    // XP and gold rewards
    const xpGained = enemy.xpReward;
    const goldGained = enemy.goldReward;

    // Chance for item drop (10% base, +1% per luck point)
    const itemDropChance = 0.1 + (character.luck * 0.01);
    const itemsFound = Math.random() < itemDropChance
      ? [generateRandomItem(enemy.level)]
      : [];

    return {
      victory: true,
      xpGained,
      goldGained,
      hpLost: Math.min(hpLost, character.current_hp - 1), // Don't die from victory
      itemsFound,
      description: isCrit
        ? `${character.name} landed a CRITICAL HIT (${damage} dmg) and defeated ${enemy.name}! Gained ${xpGained} XP and ${goldGained} gold.`
        : `${character.name} defeated ${enemy.name} with ${damage} damage! Gained ${xpGained} XP and ${goldGained} gold.`,
    };
  } else {
    // Defeat - take heavy damage and flee
    const damageRatio = enemyPower / characterPower;
    const hpLost = Math.floor(
      (character.max_hp * damageRatio * 0.5) + (Math.random() * 15)
    );

    return {
      victory: false,
      xpGained: Math.floor(enemy.xpReward * 0.1), // Small XP for trying
      goldGained: 0,
      hpLost,
      itemsFound: [],
      description: `${character.name} fought ${enemy.name} but was overpowered! Lost ${hpLost} HP and fled.`,
    };
  }
}

/**
 * Generate a random item based on enemy level
 */
function generateRandomItem(enemyLevel: number): any {
  const itemTypes = ["weapon", "armor", "accessory", "consumable"];
  const type = itemTypes[Math.floor(Math.random() * itemTypes.length)];

  const rarityRoll = Math.random();
  let rarity = "common";
  if (rarityRoll > 0.95) rarity = "legendary";
  else if (rarityRoll > 0.8) rarity = "rare";
  else if (rarityRoll > 0.5) rarity = "uncommon";

  return {
    id: `item_${Date.now()}_${Math.random()}`,
    name: `${rarity} ${type} Lv.${enemyLevel}`,
    type,
    rarity,
    level: enemyLevel,
  };
}

/**
 * Get an enemy appropriate for the character's level
 */
export function getEnemyForLevel(level: number, location: string): Enemy {
  // Enemy database based on Heartlands region
  const heartlandsEnemies = [
    { name: "Forest Slime", baseLevel: 1, health: 20, attack: 5, defense: 2 },
    { name: "Wild Rabbit", baseLevel: 2, health: 25, attack: 7, defense: 3 },
    { name: "Bandit Scout", baseLevel: 3, health: 35, attack: 10, defense: 5 },
    { name: "Young Wolf", baseLevel: 4, health: 40, attack: 12, defense: 6 },
    { name: "Goblin Warrior", baseLevel: 5, health: 50, attack: 15, defense: 8 },
    { name: "Forest Bear", baseLevel: 7, health: 70, attack: 20, defense: 12 },
    { name: "Bandit Leader", baseLevel: 9, health: 90, attack: 25, defense: 15 },
    { name: "Corrupted Treant", baseLevel: 10, health: 120, attack: 28, defense: 18 },
  ];

  // Find appropriate enemy for level
  let selectedEnemy = heartlandsEnemies[0];
  for (const enemy of heartlandsEnemies) {
    if (enemy.baseLevel <= level) {
      selectedEnemy = enemy;
    } else {
      break;
    }
  }

  // Scale enemy to character level
  const levelDiff = level - selectedEnemy.baseLevel;
  return {
    name: selectedEnemy.name,
    level,
    health: selectedEnemy.health + levelDiff * 10,
    attack: selectedEnemy.attack + levelDiff * 2,
    defense: selectedEnemy.defense + levelDiff * 1,
    xpReward: 10 + level * 5,
    goldReward: 5 + level * 3,
  };
}

/**
 * Check if character should retreat based on HP
 */
export function shouldRetreat(character: Character): boolean {
  const hpPercentage = character.current_hp / character.max_hp;

  // Retreat thresholds based on personality
  const courageThreshold = 0.2 + (character.personality_courage * 0.3); // 20%-50%

  return hpPercentage < courageThreshold;
}

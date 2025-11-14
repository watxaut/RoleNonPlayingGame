// Leveling and stat allocation system
// Matches the implementation from Kotlin

import type { Character, JobClassConfig } from "./types.ts";

/**
 * Calculate XP required for next level
 * Exponential curve: 100 * (level ^ 1.5)
 */
export function getXpForLevel(level: number): number {
  return Math.floor(100 * Math.pow(level, 1.5));
}

/**
 * Check if character leveled up and apply level-ups
 * Returns number of levels gained
 */
export function checkAndApplyLevelUp(character: Character): number {
  let levelsGained = 0;
  let xpRequired = getXpForLevel(character.level);

  while (character.experience >= xpRequired) {
    character.level++;
    character.experience -= xpRequired;
    levelsGained++;

    // Allocate stat points
    allocateStatPoints(character);

    // Recalculate XP for next level
    xpRequired = getXpForLevel(character.level);

    // Cap at level 50 for now
    if (character.level >= 50) {
      character.experience = 0;
      break;
    }
  }

  return levelsGained;
}

/**
 * Allocate stat points on level-up based on job class
 * Each level-up gives 5 stat points distributed according to job class weights
 */
function allocateStatPoints(character: Character): void {
  const jobConfig = getJobClassConfig(character.job_class);
  const pointsToAllocate = 5;

  // Distribute points according to job class weights
  const weights = jobConfig.statWeights;
  const totalWeight =
    weights.strength +
    weights.intelligence +
    weights.agility +
    weights.luck +
    weights.charisma +
    weights.vitality;

  // Allocate points proportionally
  character.strength += Math.floor(
    (pointsToAllocate * weights.strength) / totalWeight
  );
  character.intelligence += Math.floor(
    (pointsToAllocate * weights.intelligence) / totalWeight
  );
  character.agility += Math.floor(
    (pointsToAllocate * weights.agility) / totalWeight
  );
  character.luck += Math.floor((pointsToAllocate * weights.luck) / totalWeight);
  character.charisma += Math.floor(
    (pointsToAllocate * weights.charisma) / totalWeight
  );
  character.vitality += Math.floor(
    (pointsToAllocate * weights.vitality) / totalWeight
  );

  // Recalculate max HP (base 50 + VIT * 10 + level * 5)
  const oldMaxHp = character.max_hp;
  character.max_hp = 50 + character.vitality * 10 + character.level * 5;

  // Restore HP proportionally
  const hpPercentage = character.current_hp / oldMaxHp;
  character.current_hp = Math.floor(character.max_hp * hpPercentage);
}

/**
 * Get job class configuration for stat allocation
 */
function getJobClassConfig(jobClass: string): JobClassConfig {
  const jobClassLower = jobClass.toLowerCase();

  const configs: { [key: string]: JobClassConfig } = {
    warrior: {
      name: "Warrior",
      primaryStat: "strength",
      secondaryStats: ["vitality", "agility"],
      statWeights: {
        strength: 40,
        intelligence: 5,
        agility: 15,
        luck: 5,
        charisma: 5,
        vitality: 30,
      },
    },
    assassin: {
      name: "Assassin",
      primaryStat: "agility",
      secondaryStats: ["strength", "luck"],
      statWeights: {
        strength: 25,
        intelligence: 5,
        agility: 40,
        luck: 15,
        charisma: 5,
        vitality: 10,
      },
    },
    rogue: {
      name: "Rogue",
      primaryStat: "agility",
      secondaryStats: ["luck", "charisma"],
      statWeights: {
        strength: 15,
        intelligence: 5,
        agility: 35,
        luck: 25,
        charisma: 10,
        vitality: 10,
      },
    },
    archer: {
      name: "Archer",
      primaryStat: "agility",
      secondaryStats: ["strength", "luck"],
      statWeights: {
        strength: 20,
        intelligence: 5,
        agility: 40,
        luck: 20,
        charisma: 5,
        vitality: 10,
      },
    },
    mage: {
      name: "Mage",
      primaryStat: "intelligence",
      secondaryStats: ["luck", "vitality"],
      statWeights: {
        strength: 5,
        intelligence: 45,
        agility: 10,
        luck: 15,
        charisma: 5,
        vitality: 20,
      },
    },
    priest: {
      name: "Priest",
      primaryStat: "intelligence",
      secondaryStats: ["charisma", "vitality"],
      statWeights: {
        strength: 5,
        intelligence: 35,
        agility: 5,
        luck: 10,
        charisma: 20,
        vitality: 25,
      },
    },
    warlock: {
      name: "Warlock",
      primaryStat: "intelligence",
      secondaryStats: ["charisma", "luck"],
      statWeights: {
        strength: 5,
        intelligence: 40,
        agility: 10,
        luck: 20,
        charisma: 15,
        vitality: 10,
      },
    },
    bard: {
      name: "Bard",
      primaryStat: "charisma",
      secondaryStats: ["intelligence", "luck"],
      statWeights: {
        strength: 10,
        intelligence: 20,
        agility: 15,
        luck: 15,
        charisma: 30,
        vitality: 10,
      },
    },
    merchant: {
      name: "Merchant",
      primaryStat: "charisma",
      secondaryStats: ["luck", "intelligence"],
      statWeights: {
        strength: 10,
        intelligence: 15,
        agility: 15,
        luck: 30,
        charisma: 20,
        vitality: 10,
      },
    },
    scholar: {
      name: "Scholar",
      primaryStat: "intelligence",
      secondaryStats: ["charisma", "vitality"],
      statWeights: {
        strength: 5,
        intelligence: 40,
        agility: 10,
        luck: 10,
        charisma: 15,
        vitality: 20,
      },
    },
    paladin: {
      name: "Paladin",
      primaryStat: "strength",
      secondaryStats: ["vitality", "charisma"],
      statWeights: {
        strength: 30,
        intelligence: 10,
        agility: 10,
        luck: 5,
        charisma: 20,
        vitality: 25,
      },
    },
    "battle mage": {
      name: "Battle Mage",
      primaryStat: "intelligence",
      secondaryStats: ["strength", "vitality"],
      statWeights: {
        strength: 20,
        intelligence: 35,
        agility: 10,
        luck: 10,
        charisma: 5,
        vitality: 20,
      },
    },
    ranger: {
      name: "Ranger",
      primaryStat: "agility",
      secondaryStats: ["strength", "vitality"],
      statWeights: {
        strength: 20,
        intelligence: 10,
        agility: 35,
        luck: 10,
        charisma: 5,
        vitality: 20,
      },
    },
  };

  return (
    configs[jobClassLower] ||
    configs.warrior // Default to warrior if unknown
  );
}

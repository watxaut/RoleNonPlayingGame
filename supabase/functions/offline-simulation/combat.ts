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
 * Updated for Phase 3 with all 5 regions
 */
export function getEnemyForLevel(level: number, location: string): Enemy {
  // Comprehensive enemy database for all regions
  const allEnemies = [
    // Heartlands (Levels 1-10)
    { name: "Slime", baseLevel: 1, health: 15, attack: 2, defense: 2, region: "heartlands" },
    { name: "Wild Rabbit", baseLevel: 1, health: 10, attack: 1, defense: 1, region: "heartlands" },
    { name: "Grass Snake", baseLevel: 2, health: 12, attack: 3, defense: 1, region: "heartlands" },
    { name: "Wild Boar", baseLevel: 3, health: 25, attack: 4, defense: 3, region: "heartlands" },
    { name: "Forest Wolf", baseLevel: 4, health: 30, attack: 5, defense: 3, region: "heartlands" },
    { name: "Goblin Scout", baseLevel: 4, health: 25, attack: 4, defense: 2, region: "heartlands" },
    { name: "Bandit", baseLevel: 5, health: 35, attack: 5, defense: 3, region: "heartlands" },
    { name: "Goblin Warrior", baseLevel: 7, health: 45, attack: 6, defense: 4, region: "heartlands" },
    { name: "Cave Bear", baseLevel: 8, health: 55, attack: 8, defense: 6, region: "heartlands" },
    { name: "Corrupted Treant", baseLevel: 10, health: 70, attack: 8, defense: 7, region: "heartlands" },

    // Thornwood Wilds (Levels 11-25)
    { name: "Vine Horror", baseLevel: 12, health: 70, attack: 8, defense: 6, region: "thornwood" },
    { name: "Dire Wolf", baseLevel: 13, health: 80, attack: 10, defense: 7, region: "thornwood" },
    { name: "Corrupted Ent", baseLevel: 14, health: 100, attack: 11, defense: 10, region: "thornwood" },
    { name: "Spectral Elf", baseLevel: 15, health: 85, attack: 12, defense: 5, region: "thornwood" },
    { name: "Forest Troll", baseLevel: 16, health: 120, attack: 13, defense: 11, region: "thornwood" },
    { name: "Web Lurker", baseLevel: 17, health: 95, attack: 11, defense: 7, region: "thornwood" },
    { name: "Fae Trickster", baseLevel: 19, health: 90, attack: 15, defense: 6, region: "thornwood" },

    // Ashenveil Desert (Levels 21-35)
    { name: "Desert Scorpion", baseLevel: 22, health: 110, attack: 13, defense: 9, region: "ashenveil" },
    { name: "Desert Bandit", baseLevel: 23, health: 105, attack: 12, defense: 8, region: "ashenveil" },
    { name: "Sand Wurm", baseLevel: 24, health: 130, attack: 15, defense: 11, region: "ashenveil" },
    { name: "Fire Elemental", baseLevel: 26, health: 100, attack: 17, defense: 8, region: "ashenveil" },
    { name: "Sand Golem", baseLevel: 27, health: 180, attack: 18, defense: 16, region: "ashenveil" },
    { name: "Desert Cultist", baseLevel: 28, health: 115, attack: 18, defense: 9, region: "ashenveil" },
    { name: "Skeletal Warrior", baseLevel: 29, health: 125, attack: 16, defense: 10, region: "ashenveil" },
    { name: "Sand Wraith", baseLevel: 30, health: 140, attack: 16, defense: 10, region: "ashenveil" },
    { name: "Mummy Guardian", baseLevel: 31, health: 160, attack: 17, defense: 14, region: "ashenveil" },

    // Frostpeak Mountains (Levels 26-40)
    { name: "Snow Wolf", baseLevel: 26, health: 120, attack: 14, defense: 10, region: "frostpeak" },
    { name: "Yeti", baseLevel: 27, health: 150, attack: 18, defense: 13, region: "frostpeak" },
    { name: "Ice Sprite", baseLevel: 28, health: 100, attack: 16, defense: 8, region: "frostpeak" },
    { name: "Frost Elemental", baseLevel: 30, health: 130, attack: 18, defense: 11, region: "frostpeak" },
    { name: "Frozen Horror", baseLevel: 32, health: 180, attack: 20, defense: 16, region: "frostpeak" },
    { name: "Snow Leopard", baseLevel: 33, health: 140, attack: 17, defense: 11, region: "frostpeak" },
    { name: "Ice Troll", baseLevel: 34, health: 200, attack: 22, defense: 18, region: "frostpeak" },
    { name: "Frost Giant", baseLevel: 36, health: 250, attack: 25, defense: 22, region: "frostpeak" },

    // Stormcoast Reaches (Levels 35-50)
    { name: "Sahuagin Warrior", baseLevel: 38, health: 220, attack: 22, defense: 16, region: "stormcoast" },
    { name: "Drowned Priest", baseLevel: 39, health: 210, attack: 25, defense: 16, region: "stormcoast" },
    { name: "Sea Elemental", baseLevel: 40, health: 200, attack: 24, defense: 15, region: "stormcoast" },
    { name: "Ghost Pirate", baseLevel: 41, health: 230, attack: 23, defense: 17, region: "stormcoast" },
    { name: "Cursed Sailor", baseLevel: 40, health: 215, attack: 21, defense: 18, region: "stormcoast" },
    { name: "Sea Specter", baseLevel: 43, health: 240, attack: 22, defense: 16, region: "stormcoast" },
    { name: "Storm Elemental", baseLevel: 45, health: 250, attack: 28, defense: 18, region: "stormcoast" },
    { name: "Sea Serpent", baseLevel: 46, health: 300, attack: 28, defense: 24, region: "stormcoast" },
    { name: "Lightning Drake", baseLevel: 48, health: 350, attack: 30, defense: 26, region: "stormcoast" },
  ];

  // Determine region from location string
  let region = "heartlands"; // Default
  const locationLower = location.toLowerCase();
  if (locationLower.includes("thornwood")) region = "thornwood";
  else if (locationLower.includes("ashenveil") || locationLower.includes("desert")) region = "ashenveil";
  else if (locationLower.includes("frostpeak") || locationLower.includes("mountain")) region = "frostpeak";
  else if (locationLower.includes("stormcoast") || locationLower.includes("coast")) region = "stormcoast";

  // Filter enemies by region and appropriate level
  const regionalEnemies = allEnemies.filter(e =>
    e.region === region && e.baseLevel <= level + 3
  );

  // If no regional enemies found, fall back to heartlands
  const availableEnemies = regionalEnemies.length > 0 ? regionalEnemies : allEnemies.filter(e => e.region === "heartlands");

  // Select enemy closest to character level
  let selectedEnemy = availableEnemies[0];
  for (const enemy of availableEnemies) {
    if (Math.abs(enemy.baseLevel - level) < Math.abs(selectedEnemy.baseLevel - level)) {
      selectedEnemy = enemy;
    }
  }

  // Scale enemy stats to match exact level
  const levelDiff = level - selectedEnemy.baseLevel;
  const scaleFactor = level / selectedEnemy.baseLevel;

  return {
    name: selectedEnemy.name,
    level,
    health: Math.floor(selectedEnemy.health * scaleFactor * 1.2),
    attack: Math.floor(selectedEnemy.attack * scaleFactor),
    defense: Math.floor(selectedEnemy.defense * scaleFactor),
    xpReward: level * 10 + Math.floor(Math.random() * 20),
    goldReward: level * 5 + Math.floor(Math.random() * 10),
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

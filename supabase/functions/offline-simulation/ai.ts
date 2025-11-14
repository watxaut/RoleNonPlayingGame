// Simplified AI Decision Engine for Offline Simulation
// Based on the full decision engine but optimized for server-side batch processing

import { shouldRetreat, getEnemyForLevel } from "./combat.ts";
import type { Character, Decision, DecisionType } from "./types.ts";

/**
 * Make a decision for the character based on their current state
 * Simplified priority system:
 * 1. SURVIVAL - Low HP → Rest or return to town
 * 2. EXPLORATION - High curiosity → Explore new areas
 * 3. COMBAT - Aggression + level appropriate enemies
 * 4. REST - Restore HP if moderately low
 */
export function makeDecision(character: Character): Decision {
  const hpPercentage = character.current_hp / character.max_hp;

  // Priority 1: SURVIVAL (HP < 30%)
  if (hpPercentage < 0.3) {
    // Very low HP - return to town
    if (hpPercentage < 0.15 || !character.personality_courage) {
      return {
        type: "return_to_town" as DecisionType,
        reason: "Critically low HP - returning to town for safety",
        location: "Willowdale Village",
      };
    }
    // Low HP - rest in place
    return {
      type: "rest" as DecisionType,
      reason: "Low HP - resting to recover",
    };
  }

  // Priority 2: EXPLORATION (curiosity-driven)
  // Higher curiosity = more likely to explore
  const exploreChance = character.personality_curiosity * 0.5; // 0-50% based on curiosity
  if (Math.random() < exploreChance) {
    const shouldExploreNew = Math.random() < 0.3; // 30% chance to explore new location
    if (shouldExploreNew) {
      return {
        type: "explore" as DecisionType,
        reason: "Curiosity drives exploration of new areas",
        location: getRandomLocation(character.discovered_locations),
      };
    }
  }

  // Priority 3: COMBAT (aggression-driven)
  // Higher aggression = more combat encounters
  const combatChance =
    character.personality_aggression * 0.7 + // Base from aggression
    (1 - character.personality_courage) * 0.1; // Courage affects combat willingness

  if (Math.random() < combatChance) {
    // Find appropriate enemy
    const enemy = getEnemyForLevel(character.level, character.current_location);

    // Check if character should flee based on current HP
    if (shouldRetreat(character)) {
      return {
        type: "flee" as DecisionType,
        reason: "Encountered enemy but HP too low - fleeing",
      };
    }

    return {
      type: "combat" as DecisionType,
      reason: `Seeking combat with ${enemy.name}`,
      targetEnemy: enemy,
    };
  }

  // Priority 4: REST (restore HP if below 70%)
  if (hpPercentage < 0.7) {
    return {
      type: "rest" as DecisionType,
      reason: "Moderate HP - taking time to rest",
    };
  }

  // Default: EXPLORE
  return {
    type: "explore" as DecisionType,
    reason: "No pressing needs - exploring the area",
    location: character.current_location,
  };
}

/**
 * Get a random location for exploration
 * Favors undiscovered locations
 */
function getRandomLocation(discoveredLocations: string[]): string {
  // All Heartlands locations
  const heartlandsLocations = [
    "Willowdale Village",
    "Meadowbrook Fields",
    "Stonehaven Outpost",
    "Whispering Woods",
    "Crystal Lake",
    "Old Mill Ruins",
    "Bandit Camp",
    "Sunset Hills",
  ];

  // Undiscovered locations
  const undiscovered = heartlandsLocations.filter(
    (loc) => !discoveredLocations.includes(loc)
  );

  // 70% chance to discover new location if available
  if (undiscovered.length > 0 && Math.random() < 0.7) {
    return undiscovered[Math.floor(Math.random() * undiscovered.length)];
  }

  // Otherwise, random discovered location
  if (discoveredLocations.length > 0) {
    return discoveredLocations[
      Math.floor(Math.random() * discoveredLocations.length)
    ];
  }

  // Fallback
  return "Willowdale Village";
}

/**
 * Calculate time taken for a decision (in minutes)
 * Used for pacing offline simulation
 */
export function getDecisionDuration(decisionType: DecisionType): number {
  switch (decisionType) {
    case "rest" as DecisionType:
      return 30 + Math.random() * 30; // 30-60 minutes
    case "explore" as DecisionType:
      return 15 + Math.random() * 15; // 15-30 minutes
    case "combat" as DecisionType:
      return 5 + Math.random() * 10; // 5-15 minutes
    case "flee" as DecisionType:
      return 2 + Math.random() * 3; // 2-5 minutes
    case "return_to_town" as DecisionType:
      return 20 + Math.random() * 20; // 20-40 minutes
    default:
      return 10;
  }
}

/**
 * Calculate HP recovered from resting
 */
export function calculateRestHealing(
  character: Character,
  durationMinutes: number
): number {
  // Base healing: 1% max HP per 5 minutes
  const baseHealing = (character.max_hp * durationMinutes) / 500;

  // Vitality bonus: +0.5% per VIT point
  const vitalityBonus = (character.vitality * character.max_hp * durationMinutes) / 1000;

  const totalHealing = Math.floor(baseHealing + vitalityBonus);

  return Math.min(totalHealing, character.max_hp - character.current_hp);
}

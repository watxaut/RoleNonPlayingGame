// Main offline simulation orchestrator
// Simulates game hours with time compression

import { makeDecision, getDecisionDuration, calculateRestHealing } from "./ai.ts";
import { simulateCombat } from "./combat.ts";
import { checkAndApplyLevelUp } from "./leveling.ts";
import type {
  Character,
  Activity,
  SimulationResult,
  SimulationSummary,
  DecisionType,
} from "./types.ts";

/**
 * Simulate offline progress for a character
 * @param character - The character to simulate
 * @param gameHours - Number of game hours to simulate
 * @param supabaseClient - Supabase client for database access (if needed)
 * @returns SimulationResult with updated character and activities
 */
export async function simulateOfflineProgress(
  character: Character,
  gameHours: number,
  supabaseClient: any
): Promise<SimulationResult> {
  // Deep clone character to avoid mutating original
  const char: Character = JSON.parse(JSON.stringify(character));

  const activities: Activity[] = [];
  const summary: SimulationSummary = {
    totalCombats: 0,
    combatsWon: 0,
    combatsLost: 0,
    totalXpGained: 0,
    totalGoldGained: 0,
    levelsGained: 0,
    deaths: 0,
    locationsDiscovered: 0,
    itemsFound: 0,
    majorEvents: [],
  };

  // Simulation loop
  let elapsedMinutes = 0;
  const totalMinutes = gameHours * 60;
  const startTime = new Date(char.last_active_at);

  while (elapsedMinutes < totalMinutes) {
    // Make AI decision
    const decision = makeDecision(char);
    const decisionDuration = getDecisionDuration(decision.type);

    // Calculate timestamp for this activity
    const activityTime = new Date(
      startTime.getTime() + elapsedMinutes * 60 * 1000
    );

    // Execute decision
    const activity = executeDecision(char, decision, activityTime, summary);

    if (activity) {
      activities.push(activity);
    }

    // Check for level-up
    const levelsGained = checkAndApplyLevelUp(char);
    if (levelsGained > 0) {
      summary.levelsGained += levelsGained;
      summary.majorEvents.push(
        `Reached level ${char.level}${levelsGained > 1 ? ` (+${levelsGained} levels)` : ""}`
      );

      activities.push({
        timestamp: activityTime.toISOString(),
        activity_type: "level_up",
        description: `${char.name} leveled up to level ${char.level}!`,
        rewards: undefined,
        metadata: { new_level: char.level },
        is_major_event: true,
      });
    }

    // Check for death
    if (char.current_hp <= 0) {
      handleDeath(char, activityTime, activities, summary);
    }

    // Advance time
    elapsedMinutes += decisionDuration;

    // Safety limit: max 1000 activities to prevent runaway simulations
    if (activities.length >= 1000) {
      console.log("Activity limit reached, stopping simulation");
      break;
    }
  }

  // Final HP restoration if character is in town
  if (char.current_location === "Willowdale Village") {
    char.current_hp = char.max_hp;
  }

  return {
    updatedCharacter: char,
    activities,
    summary,
  };
}

/**
 * Execute a decision and return the resulting activity
 */
function executeDecision(
  character: Character,
  decision: any,
  timestamp: Date,
  summary: SimulationSummary
): Activity | null {
  switch (decision.type as DecisionType) {
    case "rest" as DecisionType:
      return executeRest(character, timestamp);

    case "explore" as DecisionType:
      return executeExplore(character, decision, timestamp, summary);

    case "combat" as DecisionType:
      return executeCombat(character, decision, timestamp, summary);

    case "flee" as DecisionType:
      return executeFlee(character, timestamp);

    case "return_to_town" as DecisionType:
      return executeReturnToTown(character, timestamp);

    default:
      return null;
  }
}

/**
 * Execute REST decision
 */
function executeRest(character: Character, timestamp: Date): Activity {
  const durationMinutes = 30 + Math.random() * 30; // 30-60 minutes
  const healing = calculateRestHealing(character, durationMinutes);

  character.current_hp = Math.min(
    character.current_hp + healing,
    character.max_hp
  );

  return {
    timestamp: timestamp.toISOString(),
    activity_type: "rest",
    description: `${character.name} rested and recovered ${healing} HP`,
    rewards: undefined,
    metadata: { hp_restored: healing },
    is_major_event: false,
  };
}

/**
 * Execute EXPLORE decision
 */
function executeExplore(
  character: Character,
  decision: any,
  timestamp: Date,
  summary: SimulationSummary
): Activity {
  const newLocation = decision.location || character.current_location;
  const isNewLocation = !character.discovered_locations.includes(newLocation);

  if (isNewLocation) {
    character.discovered_locations.push(newLocation);
    summary.locationsDiscovered++;
    summary.majorEvents.push(`Discovered ${newLocation}`);
  }

  character.current_location = newLocation;

  // Small XP reward for exploration
  const explorationXp = 5 + Math.floor(Math.random() * 10);
  character.experience += explorationXp;
  summary.totalXpGained += explorationXp;

  return {
    timestamp: timestamp.toISOString(),
    activity_type: "exploration",
    description: isNewLocation
      ? `${character.name} discovered ${newLocation}!`
      : `${character.name} explored ${newLocation}`,
    rewards: { xp: explorationXp },
    metadata: { location: newLocation, new_discovery: isNewLocation },
    is_major_event: isNewLocation,
  };
}

/**
 * Execute COMBAT decision
 */
function executeCombat(
  character: Character,
  decision: any,
  timestamp: Date,
  summary: SimulationSummary
): Activity {
  const enemy = decision.targetEnemy;
  const combatResult = simulateCombat(character, enemy);

  summary.totalCombats++;

  if (combatResult.victory) {
    summary.combatsWon++;
    summary.totalXpGained += combatResult.xpGained;
    summary.totalGoldGained += combatResult.goldGained;
    summary.itemsFound += combatResult.itemsFound.length;

    character.experience += combatResult.xpGained;
    character.gold += combatResult.goldGained;
    character.current_hp -= combatResult.hpLost;

    // Add items to inventory
    if (combatResult.itemsFound.length > 0) {
      character.inventory.push(...combatResult.itemsFound);
    }

    return {
      timestamp: timestamp.toISOString(),
      activity_type: "combat",
      description: combatResult.description,
      rewards: {
        xp: combatResult.xpGained,
        gold: combatResult.goldGained,
        items: combatResult.itemsFound,
      },
      metadata: { enemy: enemy.name, victory: true },
      is_major_event: false,
    };
  } else {
    summary.combatsLost++;
    summary.totalXpGained += combatResult.xpGained; // Small XP for trying

    character.experience += combatResult.xpGained;
    character.current_hp -= combatResult.hpLost;

    return {
      timestamp: timestamp.toISOString(),
      activity_type: "combat",
      description: combatResult.description,
      rewards: { xp: combatResult.xpGained },
      metadata: { enemy: enemy.name, victory: false },
      is_major_event: false,
    };
  }
}

/**
 * Execute FLEE decision
 */
function executeFlee(character: Character, timestamp: Date): Activity {
  return {
    timestamp: timestamp.toISOString(),
    activity_type: "flee",
    description: `${character.name} fled from danger`,
    rewards: undefined,
    metadata: {},
    is_major_event: false,
  };
}

/**
 * Execute RETURN_TO_TOWN decision
 */
function executeReturnToTown(character: Character, timestamp: Date): Activity {
  character.current_location = "Willowdale Village";
  character.current_hp = character.max_hp; // Full heal in town

  return {
    timestamp: timestamp.toISOString(),
    activity_type: "return_to_town",
    description: `${character.name} returned to Willowdale Village and fully recovered`,
    rewards: undefined,
    metadata: { location: "Willowdale Village" },
    is_major_event: false,
  };
}

/**
 * Handle character death and respawn
 */
function handleDeath(
  character: Character,
  timestamp: Date,
  activities: Activity[],
  summary: SimulationSummary
): void {
  summary.deaths++;
  summary.majorEvents.push(`Died and respawned at Willowdale Village`);

  // Death penalties
  const goldLost = Math.floor(character.gold * 0.1); // Lose 10% gold
  const xpLost = Math.floor(character.experience * 0.05); // Lose 5% XP

  character.gold -= goldLost;
  character.experience = Math.max(0, character.experience - xpLost);

  // Respawn
  character.current_location = "Willowdale Village";
  character.current_hp = character.max_hp;

  activities.push({
    timestamp: timestamp.toISOString(),
    activity_type: "death",
    description: `${character.name} died and respawned at Willowdale Village. Lost ${goldLost} gold and ${xpLost} XP.`,
    rewards: undefined,
    metadata: { gold_lost: goldLost, xp_lost: xpLost },
    is_major_event: true,
  });
}
